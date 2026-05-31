package com.example.reptrack.presentation.timer.stores

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.example.reptrack.service.sound.TimerSoundManager
import kotlinx.coroutines.launch

interface TimerStore : Store<TimerStore.Intent, TimerStore.State, TimerStore.Label> {

    sealed interface Intent {
        object StartTimer : Intent
        object PauseTimer : Intent
        object ResumeTimer : Intent
        object ResetTimer : Intent
        data class SetDuration(val seconds: Int) : Intent
        object StopService : Intent
        data class Tick(val remainingSeconds: Int) : Intent
    }

    data class State(
        val durationSeconds: Int = 60,
        val remainingSeconds: Int = 60,
        val isRunning: Boolean = false,
        val isPaused: Boolean = false,
        val isServiceRunning: Boolean = false
    ) {
        val progress: Float
            get() = if (durationSeconds > 0) remainingSeconds.toFloat() / durationSeconds else 1f

        val formattedTime: String
            get() = String.format("%02d:%02d:%02d",
                remainingSeconds / 3600,
                (remainingSeconds % 3600) / 60,
                remainingSeconds % 60
            )
    }

    sealed interface Label {
        object TimerCompleted : Label
        object ServiceStarted : Label
        object ServiceStopped : Label
    }
}