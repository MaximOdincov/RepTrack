package com.example.reptrack.presentation.auth.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

/**
 * Модификатор, который очищает фокус при нажатии вне компонента
 * Полезно для текстовых полей и других компонентов, требующих фокуса
 */
@Composable
fun Modifier.clearFocusOnTapOutside(
    focusManager: FocusManager? = null
): Modifier {
    val localFocusManager = LocalFocusManager.current
    val effectiveFocusManager = focusManager ?: localFocusManager

    return this.pointerInput(Unit) {
        detectTapGestures(
            onPress = { offset ->
                // Проверяем, что нажатие вне области фокуса
                effectiveFocusManager.clearFocus()
            }
        )
    }
}