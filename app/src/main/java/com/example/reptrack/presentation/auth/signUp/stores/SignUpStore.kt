package com.example.reptrack.presentation.auth.signUp

import android.icu.text.CaseMap
import androidx.compose.ui.text.intl.Locale
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.profile.GdprConsent
import com.example.reptrack.domain.auth.usecases.SignUpUseCase
import com.example.reptrack.presentation.auth.signIn.SignInStoreFactory
import com.example.reptrack.presentation.auth.signUp.SignUpStore.Intent
import com.example.reptrack.presentation.auth.signUp.SignUpStore.Label
import com.example.reptrack.presentation.auth.signUp.SignUpStore.State
import com.google.android.play.integrity.internal.a
import kotlinx.coroutines.launch

interface SignUpStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class EmailChanged(val value: String): Intent
        data class PasswordChanged(val value: String): Intent
        data class UsernameChanged(val value: String): Intent
        data class PrivacyStatusChanged(val value: Boolean): Intent
        data class DataConsentChanged(val value: Boolean): Intent
        object SignUpClicked: Intent
        data class EmailValidationChanged(val isValid: Boolean, val error: String?): Intent
        data class PasswordValidationChanged(val isValid: Boolean, val error: String?): Intent
        data class UsernameValidationChanged(val isValid: Boolean, val error: String?): Intent
    }

    data class State(
        val email: String = "",
        val password: String = "",
        val username: String = "",
        val isEmailValid: Boolean = true,
        val isPasswordValid: Boolean = true,
        val isUsernameValid: Boolean = true,
        val privacyAccepted: Boolean = false,
        val dataConsent: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed interface Label {
        object Authorize: Label
        data class Error(val msg: String): Label
        data class EmailValidationChanged(val isValid: Boolean, val error: String?): Label
        data class PasswordValidationChanged(val isValid: Boolean, val error: String?): Label
        data class UsernameValidationChanged(val isValid: Boolean, val error: String?): Label
    }
}

internal class SignUpStoreFactory(
    private val storeFactory: StoreFactory,
    private val signUpUseCase: SignUpUseCase
) {

    fun create(): SignUpStore =
        object : SignUpStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SignUpStore",
            initialState = State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Msg {
        data class EmailChanged(val value: String): Msg
        data class PasswordChanged(val value: String): Msg
        data class UsernameChanged(val value: String): Msg
        data class PrivacyStatusChanged(val value: Boolean): Msg
        data class DataConsentChanged(val value: Boolean): Msg
        data class EmailValidationChanged(val isValid: Boolean, val error: String?): Msg
        data class PasswordValidationChanged(val isValid: Boolean, val error: String?): Msg
        data class UsernameValidationChanged(val isValid: Boolean, val error: String?): Msg
        object Loading: Msg
        object Idle: Msg
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when(intent) {
                is Intent.DataConsentChanged -> dispatch(Msg.DataConsentChanged(value = intent.value))
                is Intent.EmailChanged -> {
                    dispatch(Msg.EmailChanged(value = intent.value))
                    val email = intent.value
                    val isValid = email.isNotEmpty() &&
                            email.contains("@") &&
                            email.length > 5 &&
                            email.length < 255 &&
                            !email.contains("'") &&
                            !email.contains("\"") &&
                            !email.contains("--") &&
                            !email.contains("DROP") &&
                            !email.contains("UNION") &&
                            !email.contains("SELECT")

                    val error = if (!isValid) {
                        if (email.isEmpty()) "Email обязателен"
                        else if (!email.contains("@")) "Пожалуйста, введите корректный email адрес"
                        else if (email.contains("'") || email.contains("\"")) "Email содержит недопустимые символы"
                        else if (email.length > 254) "Email адрес слишком длинный"
                        else "Неверный формат email"
                    } else null

                    dispatch(Msg.EmailValidationChanged(isValid, error))
                    publish(Label.EmailValidationChanged(isValid, error))
                }
                is Intent.PasswordChanged -> {
                    dispatch(Msg.PasswordChanged(value = intent.value))
                    val errors = mutableListOf<String>()
                    val password = intent.value
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

                    dispatch(Msg.PasswordValidationChanged(isValid, error))
                    publish(Label.PasswordValidationChanged(isValid, error))
                }
                is Intent.UsernameChanged -> {
                    dispatch(Msg.UsernameChanged(value = intent.value))
                    val errors = mutableListOf<String>()
                    val username = intent.value

                    if (username.isEmpty()) {
                        errors.add("Имя пользователя обязательно")
                    } else if (username.length < 3) {
                        errors.add("Имя пользователя должно содержать минимум 3 символа")
                    } else if (username.length > 20) {
                        errors.add("Имя пользователя должно быть не более 20 символов")
                    } else if (!username.matches(Regex("^[a-zA-Z0-9_]+$"))) {
                        errors.add("Имя пользователя может содержать только буквы, цифры и символ подчеркивания")
                    } else if (username.first().isDigit()) {
                        errors.add("Имя пользователя не может начинаться с цифры")
                    } else if (username == "_") {
                        errors.add("Имя пользователя не может состоять только из символа подчеркивания")
                    } else if (username.contains("'") || username.contains("\"") ||
                        username.contains("--") || username.contains(";") ||
                        username.contains("DROP") || username.contains("UNION") ||
                        username.contains("SELECT") || username.contains("INSERT")) {
                        errors.add("Имя пользователя содержит недопустимые символы")
                    }

                    val isValid = errors.isEmpty()
                    val error = if (!isValid) errors.first() else null

                    dispatch(Msg.UsernameValidationChanged(isValid, error))
                    publish(Label.UsernameValidationChanged(isValid, error))
                }
                is Intent.PrivacyStatusChanged -> dispatch(Msg.PrivacyStatusChanged(value = intent.value))
                Intent.SignUpClicked -> scope.launch {
                    val state = getState()
                    if (!state.isEmailValid || !state.isPasswordValid || !state.isUsernameValid) {
                        publish(Label.Error("Пожалуйста, исправьте ошибки валидации перед регистрацией"))
                        dispatch(Msg.Idle)
                        return@launch
                    }
                    if (!state.privacyAccepted) {
                        publish(Label.Error("Пожалуйста, примите политику конфиденциальности чтобы продолжить"))
                        dispatch(Msg.Idle)
                        return@launch
                    }

                    dispatch(Msg.Loading)
                    val result = signUpUseCase(state.email, state.password, state.username, state.dataConsent)
                    if (result.isSuccess){
                        publish(Label.Authorize)
                    }
                    else publish(Label.Error(result.exceptionOrNull()?.toString() ?: "Ошибка регистрации"))
                    dispatch(Msg.Idle)
                }
                is Intent.EmailValidationChanged -> dispatch(
                    Msg.EmailValidationChanged(isValid = intent.isValid, error = intent.error)
                )
                is Intent.PasswordValidationChanged -> dispatch(
                    Msg.PasswordValidationChanged(isValid = intent.isValid, error = intent.error)
                )
                is Intent.UsernameValidationChanged -> dispatch(
                    Msg.UsernameValidationChanged(isValid = intent.isValid, error = intent.error)
                )
            }
        }

    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(message: Msg): State =
            when (message) {
                is Msg.DataConsentChanged -> copy(dataConsent = message.value)
                is Msg.EmailChanged -> copy(email = message.value)
                is Msg.PasswordChanged -> copy(password = message.value)
                is Msg.EmailValidationChanged -> copy(
                    isEmailValid = message.isValid,
                    error = if (message.isValid) null else message.error
                )
                is Msg.PasswordValidationChanged -> copy(
                    isPasswordValid = message.isValid,
                    error = if (message.isValid) null else message.error
                )
                is Msg.UsernameValidationChanged -> copy(
                    isUsernameValid = message.isValid,
                    error = if (message.isValid) null else message.error
                )
                is Msg.Idle -> copy(isLoading = false)
                is Msg.Loading -> copy(isLoading = true)
                is Msg.PrivacyStatusChanged -> copy(privacyAccepted = message.value)
                is Msg.UsernameChanged -> copy(username = message.value)
            }
    }
}