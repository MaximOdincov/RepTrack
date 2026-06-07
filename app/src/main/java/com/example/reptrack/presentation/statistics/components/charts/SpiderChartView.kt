package com.example.reptrack.presentation.statistics.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

@Composable
fun SpiderChartView(
    data: List<SpiderChartData>, // multiple series (user + friends)
    modifier: Modifier = Modifier,
        labels: List<String> = listOf("Грудь", "Спина", "Ноги", "Плечи", "Руки", "Пресс"),
    maxValue: Float = 100f
) {
    // Показываем placeholder, если данных нет
    if (data.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет данных",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        return
    }

    val density = LocalDensity.current
    val labelStyle = TextStyle(fontSize = 12.sp, color = Color.Gray)
    val labelWidth = with(density) { labelStyle.fontSize.toPx() * 3 }

    Canvas(modifier = modifier.background(Color.Transparent)) {
        val size = Size(size.width - labelWidth, size.height)
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(centerX, centerY) - 20.dp.toPx()

        val numSides = labels.size
        val angleStep = (2 * Math.PI) / numSides

        // Draw background grid (pentagons)
        val gridLevels = 5
        for (level in 1..gridLevels) {
            val levelRadius = (radius / gridLevels) * level
            val path = Path().apply {
                for (i in 0 until numSides) {
                    val angle = i * angleStep - Math.PI / 2
                    val x = centerX + (Math.cos(angle) * levelRadius).toFloat()
                    val y = centerY + (Math.sin(angle) * levelRadius).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }
            drawPath(
                path = path,
                color = Color.Gray.copy(alpha = 0.3f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
            )
        }

        // Draw axes
        for (i in 0 until numSides) {
            val angle = i * angleStep - Math.PI / 2
            val x = centerX + (Math.cos(angle) * radius).toFloat()
            val y = centerY + (Math.sin(angle) * radius).toFloat()
            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = Offset(centerX, centerY),
                end = Offset(x, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw labels
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 12.dp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
        }
        for (i in 0 until numSides) {
            val angle = i * angleStep - Math.PI / 2
            val labelRadius = radius + 20.dp.toPx()
            val x = centerX + (Math.cos(angle) * labelRadius).toFloat()
            val y = centerY + (Math.sin(angle) * labelRadius).toFloat() + 4.dp.toPx()
            drawContext.canvas.nativeCanvas.drawText(labels[i], x, y, paint)
        }

        // Draw data series with filled areas
        data.forEach { series ->
            val path = Path().apply {
                for (i in 0 until numSides) {
                    val angle = i * angleStep - Math.PI / 2
                    val normalizedValue = (series.values[i] / maxValue).coerceIn(0f, 1f)
                    val valueRadius = radius * normalizedValue
                    val x = centerX + (Math.cos(angle) * valueRadius).toFloat()
                    val y = centerY + (Math.sin(angle) * valueRadius).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            }

            // Draw filled area with transparency
            drawPath(
                path = path,
                color = series.color.copy(alpha = 0.3f), // Semi-transparent fill
                style = androidx.compose.ui.graphics.drawscope.Fill
            )

            // Draw outline
            drawPath(
                path = path,
                color = series.color,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = null
                )
            )
        }
    }
}

data class SpiderChartData(
    val values: List<Float>, // should match labels.size
    val color: Color,
    val label: String
)