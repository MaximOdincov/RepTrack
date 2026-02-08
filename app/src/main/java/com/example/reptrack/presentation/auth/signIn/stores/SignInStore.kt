package com.example.reptrack.presentation.auth.signIn

import androidx.compose.material3.Label
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
        object LoginAsGuest: Intent
        object ResetPasswordClicked: Intent
        data class GoogleSignedIn(val idToken: String) : Intent
    }

    data class State(
        val email: String,
        val password: String,
        val isLoading: Boolean = false,
        val error: String?
    )

    sealed interface Label {
        object Authorized: Label
        object OpenSignUp: Label
        data class Error(val message: String): Label
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
        object Loading: Msg
        object Idle: Msg
    }


    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.EmailChanged -> dispatch(EmailChanged(value = intent.value))
                is Intent.PasswordChanged -> dispatch(PasswordChanged(value = intent.value))
                Intent.LoginAsGuest -> loginAsGuest()
                Intent.NavigateToSignUp -> publish(Label.OpenSignUp)
                Intent.SignInClicked -> signIn(getState())
                is Intent.GoogleSignedIn -> signInWithGoogle(intent.idToken)
                Intent.ResetPasswordClicked -> resetPassword(getState())
            }
        }

        private fun loginAsGuest() = scope.launch {
            dispatch(Loading)
            try {
                loginAsGuestUseCase()
                publish(Label.Authorized)
            } catch (e: Exception) {
                publish(Label.Error("Sign in error - please try again later"))
            } finally {
                dispatch(Msg.Idle)
            }
        }

        private fun signIn(state: State) = scope.launch {
            dispatch(Loading)
            val result = signInUseCase(state.email, state.password)
            if (result.isSuccess){
                publish(Label.Authorized)
            }
            else publish(Label.Error(result.exceptionOrNull().toString()))
            dispatch(Msg.Idle)
        }

        private fun signInWithGoogle(idToken: String) = scope.launch {
            dispatch(Loading)
            val result = signInWithGoogleUseCase(idToken)
            if (result.isSuccess){
                publish(Label.Authorized)
            }
            else publish(Label.Error(result.exceptionOrNull().toString()))
            dispatch(Msg.Idle)
        }

        private fun resetPassword(state: State) = scope.launch {
            dispatch(Loading)
            val result =  resetPasswordUseCase(state.email)
            if (result.isSuccess){
                publish(Label.Error("отправленно"))
            }
            else publish(Label.Error(result.exceptionOrNull().toString()))
            dispatch(Msg.Idle)
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.EmailChanged -> copy(email = msg.value)
                is Msg.PasswordChanged -> copy(password = msg.value)
                Msg.Idle -> copy(isLoading = false)
                Msg.Loading -> copy(isLoading = true)
            }
    }
}
