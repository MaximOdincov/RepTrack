package com.example.reptrack.presentation.auth.signUp

import android.icu.text.CaseMap
import androidx.compose.material3.Label
import androidx.compose.ui.text.intl.Locale
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.workout.GdprConsent
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
    }

    data class State(
        val email: String = "",
        val password: String = "",
        val username: String = "",
        val privacyAccepted: Boolean = false,
        val dataConsent: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed interface Label {
        object Authorize: Label
        data class Error(val msg: String): Label
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
        data class EmailChanged(val value: String):  Msg
        data class PasswordChanged(val value: String):  Msg
        data class UsernameChanged(val value: String): Msg
        data class PrivacyStatusChanged(val value: Boolean): Msg
        data class DataConsentChanged(val value: Boolean): Msg
        object Loading: Msg
        object Idle: Msg
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when(intent) {
                is Intent.DataConsentChanged -> dispatch(Msg.DataConsentChanged(value = intent.value))
                is Intent.EmailChanged -> dispatch(Msg.EmailChanged(value = intent.value))
                is Intent.PasswordChanged -> dispatch(Msg.PasswordChanged(value = intent.value))
                is Intent.UsernameChanged -> dispatch(Msg.UsernameChanged(value = intent.value))
                is Intent.PrivacyStatusChanged -> dispatch(Msg.PrivacyStatusChanged(value = intent.value))
                Intent.SignUpClicked -> scope.launch {
                    dispatch(Msg.Loading)
                    val state = getState()
                    val result = signUpUseCase(state.email, state.password, state.username, state.dataConsent)
                    if (result.isSuccess){
                        publish(Label.Authorize)
                    }
                    else publish(Label.Error(result.exceptionOrNull().toString()))
                    dispatch(Msg.Idle)
                }
            }
        }

    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(message: Msg): State =
            when (message) {
                is Msg.DataConsentChanged -> copy(dataConsent = message.value)
                is Msg.EmailChanged -> copy(email = message.value)
                is Msg.PasswordChanged -> copy(password = message.value)
                is Msg.PrivacyStatusChanged -> copy(privacyAccepted = message.value)
                is Msg.UsernameChanged -> copy(username = message.value)
                Msg.Idle -> copy(isLoading = false)
                Msg.Loading -> copy(isLoading = true)
            }
    }
}
