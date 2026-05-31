package com.example.reptrack.presentation.timer.screens.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.presentation.theme.LightAccentOrange
import kotlinx.coroutines.launch

@Composable
fun TimePickerDialog(
    currentDurationSeconds: Int,
    onDismiss: () -> Unit,
    onConfirm: (seconds: Int) -> Unit
) {
    var hours by remember { mutableIntStateOf(currentDurationSeconds / 3600) }
    var minutes by remember { mutableIntStateOf((currentDurationSeconds % 3600) / 60) }
    var seconds by remember { mutableIntStateOf(currentDurationSeconds % 60) }
    
    // Анимация при переключении пресетов
    val animatedHours by animateIntAsState(targetValue = hours, label = "hours_animation")
    val animatedMinutes by animateIntAsState(targetValue = minutes, label = "minutes_animation")
    val animatedSeconds by animateIntAsState(targetValue = seconds, label = "seconds_animation")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        textContentColor = Color.Black,
        titleContentColor = Color.Black,
        title = {
            Text(
                text = "Выбрать время",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
        },
        text = {
            Column {
                // Кнопки быстрого выбора
                Text(
                    text = "Быстрый выбор",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Первая строка пресетов
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetButton(
                        label = "30с",
                        duration = 30,
                        isSelected = animatedHours * 3600 + animatedMinutes * 60 + animatedSeconds == 30,
                        onClick = {
                            hours = 0
                            minutes = 0
                            seconds = 30
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PresetButton(
                        label = "1м",
                        duration = 60,
                        isSelected = animatedHours * 3600 + animatedMinutes * 60 + animatedSeconds == 60,
                        onClick = {
                            hours = 0
                            minutes = 1
                            seconds = 0
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PresetButton(
                        label = "3м",
                        duration = 180,
                        isSelected = animatedHours * 3600 + animatedMinutes * 60 + animatedSeconds == 180,
                        onClick = {
                            hours = 0
                            minutes = 3
                            seconds = 0
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Вторая строка пресетов
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetButton(
                        label = "5м",
                        duration = 300,
                        isSelected = animatedHours * 3600 + animatedMinutes * 60 + animatedSeconds == 300,
                        onClick = {
                            hours = 0
                            minutes = 5
                            seconds = 0
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PresetButton(
                        label = "10м",
                        duration = 600,
                        isSelected = animatedHours * 3600 + animatedMinutes * 60 + animatedSeconds == 600,
                        onClick = {
                            hours = 0
                            minutes = 10
                            seconds = 0
                        },
                        modifier = Modifier.weight(1f)
                    )
                    PresetButton(
                        label = "15м",
                        duration = 900,
                        isSelected = animatedHours * 3600 + animatedMinutes * 60 + animatedSeconds == 900,
                        onClick = {
                            hours = 0
                            minutes = 15
                            seconds = 0
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Интуитивный выбор времени
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ModernTimeWheel(
                        label = "Часы",
                        range = 0..23,
                        selectedValue = animatedHours,
                        onValueChange = { hours = it },
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = ":",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightAccentOrange,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    ModernTimeWheel(
                        label = "Минуты",
                        range = 0..59,
                        selectedValue = animatedMinutes,
                        onValueChange = { minutes = it },
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = ":",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightAccentOrange,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    ModernTimeWheel(
                        label = "Секунды",
                        range = 0..59,
                        selectedValue = animatedSeconds,
                        onValueChange = { seconds = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Предпросмотр выбранного времени
                Text(
                    text = String.format("%02d:%02d:%02d", animatedHours, animatedMinutes, animatedSeconds),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = LightAccentOrange,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(hours * 3600 + minutes * 60 + seconds) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightAccentOrange
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Запустить таймер", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun PresetButton(
    label: String,
    duration: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) LightAccentOrange else Color(0xFFF0F0F0),
        border = if (!isSelected) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFFDDDDDD)
            )
        } else null
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun ModernTimeWheel(
    label: String,
    range: IntRange,
    selectedValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val rangeSize = range.count()
    val items = range.toList()
    
    // Для карусельного выбора: повторяем значения несколько раз
    // Это позволяет скроллить циклически (59->00->01 и т.д.)
    val totalRepeats = 10  // Повторяем цикл 10 раз для бесконечного скролла
    val infiniteItems = (0 until totalRepeats).flatMap { items }
    
    // Начальный индекс - примерно в середине "бесконечного" списка
    val middleRepeat = totalRepeats / 2
    val initialIndex = middleRepeat * rangeSize + selectedValue
    
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val coroutineScope = rememberCoroutineScope()
    var isUserScrolling by remember { mutableStateOf(false) }

    // Автоматическая прокрутка к выбранному значению при изменении (только если не из самого пикера)
    LaunchedEffect(selectedValue) {
        if (!isUserScrolling) {
            val centerRepeat = (totalRepeats / 2)
            val newIndex = centerRepeat * rangeSize + selectedValue
            coroutineScope.launch {
                listState.scrollToItem(newIndex)
            }
        }
    }

    // Обработка изменения выбранного значения при завершении прокрутки
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress && isUserScrolling) {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                // Найти элемент, который находится ближе всего к центру
                val centerViewportY = listState.layoutInfo.viewportSize.height / 2
                val centerItem = visibleItems.minByOrNull {
                    Math.abs((it.offset + it.size / 2) - centerViewportY)
                }
                centerItem?.let {
                    val realValue = infiniteItems[it.index]
                    if (realValue != selectedValue) {
                        onValueChange(realValue)
                    }
                }
            }
            isUserScrolling = false
        } else if (listState.isScrollInProgress) {
            isUserScrolling = true
        }
    }

    Column(
        modifier = modifier.height(160.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Индикатор выбранного значения (в центре)
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(44.dp)
                    .background(
                        LightAccentOrange.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
            )

            LazyColumn(
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.height(140.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 48.dp)
            ) {
                items(infiniteItems.size) { index ->
                    val value = infiniteItems[index]
                    val isSelected = value == selectedValue

                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .fillMaxWidth()
                            .clickable {
                                isUserScrolling = false
                                coroutineScope.launch {
                                    listState.animateScrollToItem(index)
                                    onValueChange(value)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = value.toString().padStart(2, '0'),
                            fontSize = 24.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) LightAccentOrange else Color.Black.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}