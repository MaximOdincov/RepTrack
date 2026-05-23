package com.example.reptrack.presentation.statistics.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun LineChartView(
    data: Map<String, List<Pair<Float, Float>>>, // map of seriesName to list of (x, y) points
    seriesColors: Map<String, Color>,
    modifier: Modifier = Modifier,
    showPoints: Boolean = true
) {
    val density = LocalDensity.current

    Canvas(modifier = modifier.background(Color.Transparent)) {
        if (data.isEmpty()) return@Canvas

        val allXValues = data.values.flatten().map { it.first }
        val allYValues = data.values.flatten().map { it.second }

        if (allXValues.isEmpty()) return@Canvas

        val minX = allXValues.minOrNull() ?: 0f
        val maxX = allXValues.maxOrNull() ?: 1f
        val minY = allYValues.minOrNull() ?: 0f
        val maxY = allYValues.maxOrNull() ?: 1f

        val padding = 40.dp.toPx()
        val chartWidth = size.width - padding * 2
        val chartHeight = size.height - padding * 2

        fun mapToScreen(x: Float, y: Float): Offset {
            val normalizedX = if (maxX - minX == 0f) 0.5f else (x - minX) / (maxX - minX)
            val normalizedY = if (maxY - minY == 0f) 0.5f else (y - minY) / (maxY - minY)
            return Offset(
                x = padding + normalizedX * chartWidth,
                y = size.height - padding - normalizedY * chartHeight
            )
        }

        // Draw grid lines
        val gridLines = 5
        for (i in 0..gridLines) {
            val y = padding + (chartHeight / gridLines) * i
            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(padding, y),
                end = Offset(size.width - padding, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw each series
        data.forEach { (seriesName, points) ->
            if (points.isEmpty()) return@forEach

            val color = seriesColors[seriesName] ?: Color.Blue
            android.util.Log.d("LineChartView", "Drawing series: $seriesName with color: $color")
            android.util.Log.d("LineChartView", "  Float A=${color.alpha}, R=${color.red}, G=${color.green}, B=${color.blue}")

            // Draw line
            val path = Path().apply {
                points.sortedBy { it.first }.forEachIndexed { index, (x, y) ->
                    val screenPos = mapToScreen(x, y)
                    if (index == 0) {
                        moveTo(screenPos.x, screenPos.y)
                    } else {
                        lineTo(screenPos.x, screenPos.y)
                    }
                }
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 3.dp.toPx())
            )

            // Draw points
            if (showPoints) {
                points.forEach { (x, y) ->
                    val screenPos = mapToScreen(x, y)
                    drawCircle(
                        color = color,
                        radius = 6.dp.toPx(),
                        center = screenPos
                    )
                }
                android.util.Log.d("LineChartView", "  Drew ${points.size} points with color $color")
            }
        }

        // Draw axis labels using native canvas
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 12.dp.toPx()
            textAlign = android.graphics.Paint.Align.CENTER
        }

        // X-axis labels (date format)
        val xLabelCount = 5
        for (i in 0 until xLabelCount) {
            val normalizedX = i.toFloat() / (xLabelCount - 1)
            val x = padding + normalizedX * chartWidth
            val value = minX + (maxX - minX) * normalizedX

            // Convert epoch days to date string
            val date = java.time.LocalDate.ofEpochDay(value.toLong())
            val day = date.dayOfMonth
            val month = date.monthValue
            val dateText = "$day.$month"

            drawContext.canvas.nativeCanvas.drawText(
                dateText,
                x,
                size.height - 10.dp.toPx(),
                paint
            )
        }

        // Y-axis labels
        val yLabelCount = 5
        for (i in 0 until yLabelCount) {
            val normalizedY = i.toFloat() / (yLabelCount - 1)
            val y = size.height - padding - normalizedY * chartHeight
            val value = minY + (maxY - minY) * normalizedY
            drawContext.canvas.nativeCanvas.drawText(
                "%.1f".format(value),
                padding - 15.dp.toPx(),
                y + 4.dp.toPx(),
                paint
            )
        }
    }
}