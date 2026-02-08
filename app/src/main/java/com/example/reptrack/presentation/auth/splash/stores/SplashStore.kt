package com.example.reptrack.presentation.auth.splash

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.domain.auth.usecases.GetCurrentUserUseCase
import com.example.reptrack.presentation.auth.splash.SplashStore.Intent
import com.example.reptrack.presentation.auth.splash.SplashStore.Label
import com.example.reptrack.presentation.auth.splash.SplashStore.State
import kotlinx.coroutines.launch

interface SplashStore : Store<Intent, State, Label> {

    sealed interface Intent {
        object CheckAuth: Intent
    }

    data class State(
        val isLoading: Boolean
    )

    sealed interface Label {
        object Authorized: Label
        object UnAuthorized: Label
    }
}

internal class SplashStoreFactory(
    private val storeFactory: StoreFactory,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {

    fun create(): SplashStore =
        object : SplashStore, Store<Intent, State, Label> by storeFactory.create(
            name = "SplashStore",
            initialState = State(isLoading = true),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        object CheckAuth: Action
    }

    private sealed interface Msg {
        object LoadingFinished: Msg
    }

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            dispatch(Action.CheckAuth)
        }
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when(intent){
                Intent.CheckAuth -> checkAuth()
            }
        }

        override fun executeAction(action: Action, getState: () -> State) {

        }

        private fun checkAuth(){
            scope.launch {
                val user = getCurrentUserUseCase()
                dispatch(Msg.LoadingFinished)
                if(user != null) publish(Label.Authorized)
                else publish(Label.UnAuthorized)
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                Msg.LoadingFinished -> copy(isLoading = false)
            }
    }


}
