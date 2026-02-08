package com.example.reptrack.presentation.auth.splash

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states

@Composable
fun SplashScreen(
    store: SplashStore,
    onAuthorized: () -> Unit,
    onUnAuthorized: () -> Unit
){
    val state by store.states.collectAsState(SplashStore.State(isLoading = true))

    LaunchedEffect(store){
        Log.d("fddfd", "fvdcfd")
        store.labels.collect { label ->
            when(label){
                SplashStore.Label.Authorized -> onAuthorized()
                SplashStore.Label.UnAuthorized -> onUnAuthorized()
            }
        }
    }

    LaunchedEffect(Unit) {
        store.accept(SplashStore.Intent.CheckAuth)
    }

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        if(state.isLoading){
            CircularProgressIndicator()
        }
    }
}