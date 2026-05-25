package com.example.reptrack.presentation.timer.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.service.sound.TimerSoundManager
import kotlinx.coroutines.launch

internal class TimerStoreFactory(
    private val storeFactory: StoreFactory,
    private val timerSoundManager: TimerSoundManager
) {

    fun create(): TimerStore =
        object : TimerStore, Store<TimerStore.Intent, TimerStore.State, TimerStore.Label> by storeFactory.create(
            name = "TimerStore",
            initialState = TimerStore.State(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Msg {
        data class TimerStarted(val duration: Int) : Msg
        data object TimerPaused : Msg
        data object TimerResumed : Msg
        data object TimerReset : Msg
        data class DurationChanged(val duration: Int) : Msg
        data object ServiceStarted : Msg
        data object ServiceStopped : Msg
        data class TimerTicked(val remainingSeconds: Int) : Msg
        data object TimerCompleted : Msg
    }

    private inner class ExecutorImpl : CoroutineExecutor<TimerStore.Intent, Nothing, TimerStore.State, Msg, TimerStore.Label>() {
        override fun executeIntent(intent: TimerStore.Intent, getState: () -> TimerStore.State) {
            when (intent) {
                is TimerStore.Intent.StartTimer -> {
                    dispatch(Msg.TimerStarted(getState().durationSeconds))
                    publish(TimerStore.Label.ServiceStarted)
                }
                is TimerStore.Intent.PauseTimer -> {
                    dispatch(Msg.TimerPaused)
                }
                is TimerStore.Intent.ResumeTimer -> {
                    dispatch(Msg.TimerResumed)
                }
                is TimerStore.Intent.ResetTimer -> {
                    dispatch(Msg.TimerReset)
                    publish(TimerStore.Label.ServiceStopped)
                }
                is TimerStore.Intent.SetDuration -> {
                    dispatch(Msg.DurationChanged(intent.seconds))
                }
                is TimerStore.Intent.StopService -> {
                    dispatch(Msg.ServiceStopped)
                }
                is TimerStore.Intent.Tick -> {
                    dispatch(Msg.TimerTicked(intent.remainingSeconds))
                    if (intent.remainingSeconds <= 0) {
                        dispatch(Msg.TimerCompleted)
                        publish(TimerStore.Label.TimerCompleted)
                        // Play sound on completion
                        scope.launch {
                            timerSoundManager.playCompletionSound()
                        }
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<TimerStore.State, Msg> {
        override fun TimerStore.State.reduce(message: Msg): TimerStore.State =
            when (message) {
                is Msg.TimerStarted -> copy(
                    remainingSeconds = message.duration,
                    isRunning = true,
                    isPaused = false,
                    isServiceRunning = true
                )
                is Msg.TimerPaused -> copy(
                    isRunning = false,
                    isPaused = true
                )
                is Msg.TimerResumed -> copy(
                    isRunning = true,
                    isPaused = false
                )
                is Msg.TimerReset -> copy(
                    remainingSeconds = durationSeconds,
                    isRunning = false,
                    isPaused = false,
                    isServiceRunning = false
                )
                is Msg.DurationChanged -> copy(
                    durationSeconds = message.duration,
                    remainingSeconds = message.duration
                )
                is Msg.ServiceStarted -> copy(isServiceRunning = true)
                is Msg.ServiceStopped -> copy(isServiceRunning = false)
                is Msg.TimerTicked -> copy(remainingSeconds = message.remainingSeconds)
                is Msg.TimerCompleted -> copy(
                    remainingSeconds = 0,
                    isRunning = false,
                    isPaused = false,
                    isServiceRunning = false
                )
            }
    }
}