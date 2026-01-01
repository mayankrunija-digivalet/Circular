package com.example.circular

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun Controller() {
    val minTemp = 16f
    val maxTemp = 32f
    var tempValue by remember { mutableFloatStateOf(24f) }

    val startAngle = 140f
    val sweepAngle = 80f

    val horizontalOffset = -360.dp

    val animatedTemp by animateFloatAsState(
        targetValue = tempValue,
        label = "TempAnim"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    val sensitivity = 1200f
                    val delta = -(dragAmount / sensitivity) * (maxTemp - minTemp)
                    tempValue = (tempValue + delta).coerceIn(minTemp, maxTemp)
                }
            }
    ) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        val offsetPx = with(density) { horizontalOffset.toPx()*.9f }

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val centerX = size.width - offsetPx
            val centerY = size.height / 2f

            val radius = size.height * 0.45f
            val currentAngle = startAngle + ((animatedTemp - minTemp) / (maxTemp - minTemp)) * sweepAngle

            val numberOfTicks = 60
            for (i in 0..numberOfTicks) {
                val tickAngle = startAngle + (i.toFloat() / numberOfTicks) * sweepAngle
                val angleRad = Math.toRadians(tickAngle.toDouble()).toFloat()

                val startLine = radius * 1.05f
                val endLine = radius * 1.12f
                val isHighlighted = abs(tickAngle - currentAngle) < 1.5f

                drawLine(
                    color = if (isHighlighted) Color(0xFF333333) else Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(
                        x = centerX + startLine * cos(angleRad),
                        y = centerY + startLine * sin(angleRad)
                    ),
                    end = Offset(
                        x = centerX + endLine * cos(angleRad),
                        y = centerY + endLine * sin(angleRad)
                    ),
                    strokeWidth = if (isHighlighted) 5f else 2f
                )
            }

            val trackColor = Color(0xFFC5B8A5)
            val strokeWidth = 80f
            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(centerX - radius, centerY - radius)
            )

            val knobAngleRad = Math.toRadians(currentAngle.toDouble()).toFloat()
            val knobOffset = Offset(
                x = centerX + radius * cos(knobAngleRad),
                y = centerY + radius * sin(knobAngleRad)
            )

            drawCircle(
                color = trackColor,
                radius = strokeWidth * 0.8f,
                center = knobOffset
            )

            val borderPadding = 100f
            drawArc(
                color = Color.Black.copy(alpha = 0.05f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 2f),
                size = Size((radius + borderPadding) * 2, (radius + borderPadding) * 2),
                topLeft = Offset(centerX - (radius + borderPadding), centerY - (radius + borderPadding))
            )
        }

            Text(
                text = "${animatedTemp.toInt()}°",
                style = androidx.compose.ui.text.TextStyle(
                    color = Color(0xFF222222),
                    fontSize = 110.sp,
                    fontWeight = FontWeight.ExtraLight
                )
            )
    }
}

@Composable
@Preview(showBackground = true)
fun ControllerPreview() {
    Controller()
}

