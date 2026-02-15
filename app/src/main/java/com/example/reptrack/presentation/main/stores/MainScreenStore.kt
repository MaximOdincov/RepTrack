package com.example.reptrack.presentation.main.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.presentation.main.stores.MainScreenStore.Intent
import com.example.reptrack.presentation.main.stores.MainScreenStore.State
import java.time.LocalDate

internal interface MainScreenStore : Store<Intent, State, Nothing> {

    sealed interface Intent {
        data class SelectDate(val date: LocalDate) : Intent
    }

    data class State constructor(
        val currentDate: LocalDate = LocalDate.now()
    )
}

internal class MainScreenStoreFactory(
    private val storeFactory: StoreFactory
) {

    fun create(): MainScreenStore =
        object : MainScreenStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "MainScreenStore",
            initialState = State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Msg {
        data class DateChanged(val newDate: LocalDate) : Msg
    }

    private class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Nothing>() {

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.SelectDate -> {
                    dispatch(Msg.DateChanged(intent.date))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(message: Msg): State =
            when (message) {
                is Msg.DateChanged -> copy(currentDate = message.newDate)
            }
    }
}