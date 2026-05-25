package com.example.reptrack.presentation.statistics.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.sqrt

@Composable
fun LineChartView(
    data: Map<String, List<Pair<Float, Float>>>, // map of seriesName to list of (x, y) points
    seriesColors: Map<String, Color>,
    modifier: Modifier = Modifier,
    showPoints: Boolean = true
) {
    val density = LocalDensity.current
    var tooltipData by remember { mutableStateOf<Pair<Pair<Float, Float>, Offset>?>(null) }

    // Add key to force recomposition when data changes
    val dataKey = remember(data) { data.keys.joinToString(",") + data.values.map { it.size }.joinToString(",") }

    Box(modifier = modifier) {
        var chartPadding by remember { mutableStateOf(0f) }
        var chartSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
        var mapToScreenFn by remember { mutableStateOf<(Float, Float) -> Offset>({ _, _ -> Offset.Zero }) }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        android.util.Log.d("important", "=== Chart tapped ===")
                        android.util.Log.d("important", "Tap offset: x=${offset.x}, y=${offset.y}")
                        android.util.Log.d("important", "Data size: ${data.size}, Data keys: ${data.keys}")
                        android.util.Log.d("important", "Total points: ${data.values.flatten().toList().size}")

                        // Find closest point
                        val allPoints = data.values.flatten().toList()
                        if (allPoints.isNotEmpty()) {
                            android.util.Log.d("important", "Finding closest point...")
                            val closest = allPoints.minByOrNull { point ->
                                val screenPos = mapToScreenFn(point.first, point.second)
                                val dx = screenPos.x - offset.x
                                val dy = screenPos.y - offset.y
                                val distance = sqrt((dx * dx + dy * dy).toDouble())
                                distance
                            }

                            val screenPos = mapToScreenFn(closest!!.first, closest.second)
                            val dx = screenPos.x - offset.x
                            val dy = screenPos.y - offset.y
                            val distance = sqrt((dx * dx + dy * dy).toDouble())

                            android.util.Log.d("important", "Closest point: ${closest.first}, ${closest.second}")
                            android.util.Log.d("important", "Closest point screen pos: x=${screenPos.x}, y=${screenPos.y}")
                            android.util.Log.d("important", "Distance from tap: $distance")

                            // Show tooltip if tap is within 100px of a point (increased from 30)
                            if (distance <= 100f) {
                                android.util.Log.d("important", "SHOWING TOOLTIP - distance $distance <= 100f")
                                tooltipData = Pair(closest, screenPos)
                            } else {
                                android.util.Log.d("important", "Distance too large ($distance > 100f), hiding tooltip")
                                tooltipData = null
                            }
                        } else {
                            android.util.Log.d("important", "No points to search")
                        }
                    }
                }
        ) {
            if (data.isEmpty()) return@Canvas

            val allXValues = data.values.flatten().map { it.first }
            val allYValues = data.values.flatten().map { it.second }

            if (allXValues.isEmpty()) return@Canvas

            val minX = allXValues.minOrNull() ?: 0f
            val maxX = allXValues.maxOrNull() ?: 1f
            val minY = allYValues.minOrNull() ?: 0f
            val maxY = allYValues.maxOrNull() ?: 1f

            // Ensure at least 1 day range for X axis to avoid flat charts
            val effectiveMaxX = if (maxX - minX < 1f) minX + 1f else maxX
            // Add small padding to Y axis
            val effectiveMaxY = maxY + (maxY - minY) * 0.1f
            val effectiveMinY = (minY - (maxY - minY) * 0.1f).coerceAtLeast(0f)

            val xPadding = 45.dp.toPx()  // Reduced for larger chart
            val yPadding = 30.dp.toPx()  // Reduced for larger chart
            val bottomPadding = 25.dp.toPx()  // Reduced for larger chart
            val rightPadding = 10.dp.toPx()  // Reduced right padding

            chartPadding = xPadding
            chartSize = size

            val chartWidth = size.width - xPadding - rightPadding
            val chartHeight = size.height - yPadding - bottomPadding

            fun mapToScreen(x: Float, y: Float): Offset {
                val normalizedX = if (effectiveMaxX - minX == 0f) 0.5f else (x - minX) / (effectiveMaxX - minX)
                val normalizedY = if (effectiveMaxY - effectiveMinY == 0f) 0.5f else (y - effectiveMinY) / (effectiveMaxY - effectiveMinY)
                return Offset(
                    x = xPadding + normalizedX * chartWidth,
                    y = size.height - bottomPadding - normalizedY * chartHeight
                )
            }

            // Store mapToScreen function for tap detection
            mapToScreenFn = ::mapToScreen

            // Calculate round Y values (step 0.5)
            fun roundToHalf(value: Float): Float {
                return (value * 2).let { Math.round(it.toDouble()) / 2f }
            }

            // Calculate Y axis labels with step 0.5
            val yRange = effectiveMaxY - effectiveMinY
            val steps = 8
            val stepSize = (yRange / steps).let {
                // Round to nearest 0.5
                val rounded = roundToHalf(it)
                if (rounded < 0.5f) 0.5f else rounded
            }

            // Draw grid lines (horizontal - Y axis)
            val gridLines = steps
            for (i in 0..gridLines) {
                val value = roundToHalf(effectiveMinY + stepSize * i)
                val normalizedY = if (effectiveMaxY - effectiveMinY == 0f) i.toFloat() / gridLines
                                  else (value - effectiveMinY) / (effectiveMaxY - effectiveMinY)
                val y = size.height - bottomPadding - normalizedY * chartHeight
                drawLine(
                    color = Color.Gray.copy(alpha = 0.2f),
                    start = Offset(xPadding, y),
                    end = Offset(size.width - rightPadding, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Draw vertical grid lines based on unique days
            val uniqueDays = allXValues.map { it.toLong() }.distinct().sorted()
            if (uniqueDays.size > 1) {
                uniqueDays.forEach { dayEpoch ->
                    val x = xPadding + ((dayEpoch - minX) / (effectiveMaxX - minX)) * chartWidth
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.15f),
                        start = Offset(x, yPadding),
                        end = Offset(x, size.height - bottomPadding),
                        strokeWidth = 1.dp.toPx()
                    )
                }
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

            // Draw axis labels
            val axisLabelPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#6B7280")
                textSize = 11.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }

            // X-axis labels (date format with leading zeros)
            val xLabelCount = if (uniqueDays.size > 6) 6 else uniqueDays.size
            for (i in 0 until xLabelCount) {
                val normalizedX = if (xLabelCount > 1) i.toFloat() / (xLabelCount - 1) else 0.5f
                val x = xPadding + normalizedX * chartWidth
                val value = minX + (effectiveMaxX - minX) * normalizedX

                // Convert epoch days to date string with leading zeros
                val date = java.time.LocalDate.ofEpochDay(value.toLong())
                val day = date.dayOfMonth.toString().padStart(2, '0')
                val month = date.monthValue.toString().padStart(2, '0')
                val dateText = "$day.$month"

                drawContext.canvas.nativeCanvas.drawText(
                    dateText,
                    x,
                    size.height - 10.dp.toPx(),
                    axisLabelPaint
                )
            }

            // Y-axis labels (kg) with step 0.5
            val yLabelPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#6B7280")
                textSize = 10.dp.toPx()
                textAlign = android.graphics.Paint.Align.RIGHT
                isAntiAlias = true
            }

            for (i in 0..gridLines) {
                val value = roundToHalf(effectiveMinY + stepSize * i)
                val normalizedY = if (effectiveMaxY - effectiveMinY == 0f) i.toFloat() / gridLines
                                  else (value - effectiveMinY) / (effectiveMaxY - effectiveMinY)
                val y = size.height - bottomPadding - normalizedY * chartHeight
                drawContext.canvas.nativeCanvas.drawText(
                    "%.1f".format(value),
                    xPadding - 8.dp.toPx(),
                    y + 4.dp.toPx(),
                    yLabelPaint
                )
            }

            // Draw axis labels (kg on left side, rotated)
            val yAxisLabelPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#9CA3AF")
                textSize = 10.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }

            drawContext.canvas.nativeCanvas.save()
            drawContext.canvas.nativeCanvas.rotate(-90f, 5.dp.toPx(), size.height / 2f)
            drawContext.canvas.nativeCanvas.drawText(
                "Вес (кг)",
                5.dp.toPx(),
                size.height / 2f,
                yAxisLabelPaint
            )
            drawContext.canvas.nativeCanvas.restore()

            // X-axis label (Days on bottom) - removed as requested
        }

        // Show tooltip if point selected
        tooltipData?.let { (dataPoint, screenPos) ->
            Surface(
                modifier = Modifier
                    .padding(8.dp)
                    .offset {
                        IntOffset(
                            x = (screenPos.x + 15).toInt(),
                            y = (screenPos.y - 60).toInt()
                        )
                    },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 8.dp
            ) {
                Text(
                    text = formatTooltip(dataPoint.first, dataPoint.second),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

fun formatTooltip(x: Float, y: Float): String {
    val date = java.time.LocalDate.ofEpochDay(x.toLong())
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale("ru"))
    return "${date.format(formatter)}: ${y} кг"
}