package com.example.reptrack.presentation.auth.signIn

import androidx.lifecycle.viewmodel.viewModelFactory
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.auth.usecases.LoginAsGuestUseCase
import com.example.reptrack.domain.auth.usecases.ResetPasswordUseCase
import com.example.reptrack.domain.auth.usecases.SignInUseCase
import com.example.reptrack.domain.auth.usecases.SignInWithGoogleUseCase
import com.example.reptrack.presentation.auth.signIn.SignInStore.Intent
import com.example.reptrack.presentation.auth.signIn.SignInStore.Label
import com.example.reptrack.presentation.auth.signIn.SignInStore.State
import com.example.reptrack.presentation.auth.signIn.SignInStoreFactory.Msg.*
import kotlinx.coroutines.launch

interface SignInStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class EmailChanged(val value: String): Intent
        data class PasswordChanged(val value: String): Intent
        object SignInClicked: Intent
        object NavigateToSignUp: Intent
        object NavigateBack: Intent
        object LoginAsGuest: Intent
        data class ResetPasswordClicked(val email: String): Intent
        data class GoogleSignedIn(val idToken: String) : Intent
        data class GoogleSignInError(val error: String): Intent
        object ClearError: Intent
    }

    data class State(
        val email: String,
        val password: String,
        val isEmailValid: Boolean = true,
        val isPasswordValid: Boolean = true,
        val isLoading: Boolean = false,
        val error: String?
    )

    sealed interface Label {
        object Authorized: Label
        object OpenSignUp: Label
        object NavigateBack: Label
        data class Error(val message: String): Label
        data class Success(val message: String): Label
        data class EmailValidationChanged(val isValid: Boolean, val error: String?): Label
        data class PasswordValidationChanged(val isValid: Boolean, val error: String?): Label
    }
}

internal class SignInStoreFactory(
    private val storeFactory: StoreFactory,
    private val signInUseCase: SignInUseCase,
    private val loginAsGuestUseCase: LoginAsGuestUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) {

    fun create(): SignInStore =
        object : SignInStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SignInStore",
            initialState = State(email = "", password = "", error = null),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Msg {
        data class EmailChanged(val value: String): Msg
        data class PasswordChanged(val value: String): Msg
        data class EmailValidationChanged(val isValid: Boolean, val error: String?): Msg
        data class PasswordValidationChanged(val isValid: Boolean, val error: String?): Msg
        data class GoogleSignInError(val error: String): Msg
        object ClearError: Msg
        object Loading: Msg
        object Idle: Msg
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.EmailChanged -> {
                    dispatch(EmailChanged(value = intent.value))
                    val email = intent.value
                    val isValid = email.isNotEmpty() &&
                            email.contains("@") &&
                            email.length > 5 &&
                            email.length < 255

                    val error = if (!isValid) {
                        if (email.isEmpty()) "Email обязателен"
                        else if (!email.contains("@")) "Пожалуйста, введите корректный email адрес"
                        else if (email.contains("'") || email.contains("\"")) "Email содержит недопустимые символы"
                        else if (email.length > 254) "Email адрес слишком длинный"
                        else "Неверный формат email"
                    } else null

                    publish(Label.EmailValidationChanged(isValid, error))
                }
                is Intent.PasswordChanged -> {
                    dispatch(PasswordChanged(value = intent.value))
                    val password = intent.value
                    val errors = mutableListOf<String>()

                    if (password.length < 8) {
                        errors.add("Пароль должен содержать минимум 8 символов")
                    }

                    if (!password.any { it.isUpperCase() }) {
                        errors.add("Пароль должен содержать хотя бы одну заглавную букву")
                    }

                    if (!password.any { it.isLowerCase() }) {
                        errors.add("Пароль должен содержать хотя бы одну строчную букву")
                    }

                    if (!password.any { it.isDigit() }) {
                        errors.add("Пароль должен содержать хотя бы одну цифру")
                    }

                    if (password.contains("'") || password.contains("\"") ||
                        password.contains("--") || password.contains(";") ||
                        password.contains("DROP") || password.contains("UNION") ||
                        password.contains("SELECT") || password.contains("INSERT")) {
                        errors.add("Пароль содержит недопустимые символы")
                    }

                    // Проверка на слабые пароли
                    val weakPasswords = listOf("password", "qwerty", "12345678", "admin", "letmein")
                    if (weakPasswords.any { password.lowercase().contains(it) }) {
                        errors.add("Это часто используемый пароль")
                    }

                    val isValid = errors.isEmpty()
                    val error = if (!isValid) errors.first() else null
                    this.publish(Label.PasswordValidationChanged(isValid, error))
                }
                Intent.LoginAsGuest -> loginAsGuest()
                Intent.NavigateToSignUp -> publish(Label.OpenSignUp)
                Intent.NavigateBack -> publish(Label.NavigateBack)
                Intent.SignInClicked -> signIn(getState())
                is Intent.GoogleSignedIn -> signInWithGoogle(intent.idToken)
                is Intent.GoogleSignInError -> dispatch(Msg.GoogleSignInError(intent.error))
                is Intent.ResetPasswordClicked -> resetPassword(intent.email)
                Intent.ClearError -> dispatch(Msg.ClearError)
            }
        }

        private fun loginAsGuest() = scope.launch {
            dispatch(Loading)
            try {
                loginAsGuestUseCase()
                publish(Label.Authorized)
            } catch (e: Exception) {
                publish(Label.Error("Ошибка входа - пожалуйста, попробуйте позже"))
            } finally {
                dispatch(Msg.Idle)
            }
        }

        private fun signIn(state: State) = scope.launch {

            // Временно отключаем валидацию для тестирования
            val email = state.email
            val password = state.password

            android.util.Log.d("SignInStore", "DEBUG: email=$email, password=${"*".repeat(password.length)}")

            if (email.isEmpty() || password.isEmpty()) {
                publish(Label.Error("Пожалуйста, введите email и пароль"))
                dispatch(Msg.Idle)
                return@launch
            }

            dispatch(Loading)
            android.util.Log.d("SignInStore", "DEBUG: Вызываем signInUseCase")
            val result = signInUseCase(state.email, state.password)
            android.util.Log.d("SignInStore", "DEBUG: Результат = $result")
            if (result.isSuccess){
                android.util.Log.d("SignInStore", "DEBUG: Успех!")
                publish(Label.Authorized)
            }
            else {
                android.util.Log.d("SignInStore", "DEBUG: Ошибка: ${result.exceptionOrNull()}")
                publish(Label.Error(result.exceptionOrNull()?.toString() ?: "Ошибка входа"))
            }
            dispatch(Msg.Idle)
        }

        private fun signInWithGoogle(idToken: String) = scope.launch {
            dispatch(Loading)
            val result = signInWithGoogleUseCase(idToken)
            if (result.isSuccess){
                publish(Label.Authorized)
            }
            else publish(Label.Error("Ошибка входа с Google: ${result.exceptionOrNull()?.toString()}"))
            dispatch(Msg.Idle)
        }

        private fun resetPassword(email: String) = scope.launch {
            dispatch(Loading)
            val result = resetPasswordUseCase(email)
            if (result.isSuccess){
                publish(Label.Success("Письмо для сброса пароля отправлено"))
            }
            else publish(Label.Error(result.exceptionOrNull()?.toString() ?: "Не удалось отправить письмо для сброса пароля"))
            dispatch(Msg.Idle)
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.EmailChanged -> copy(email = msg.value)
                is Msg.PasswordChanged -> copy(password = msg.value)
                is Msg.EmailValidationChanged -> copy(
                    isEmailValid = msg.isValid,
                    error = if (msg.isValid) null else msg.error
                )
                is Msg.PasswordValidationChanged -> copy(
                    isPasswordValid = msg.isValid,
                    error = if (msg.isValid) null else msg.error
                )
                is Msg.GoogleSignInError -> copy(error = msg.error, isLoading = false)
                Msg.ClearError -> copy(error = null)
                Msg.Idle -> copy(isLoading = false)
                Msg.Loading -> copy(isLoading = true)
            }
    }
}