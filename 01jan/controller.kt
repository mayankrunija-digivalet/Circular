package com.example.circular

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.withTransform
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
        val offsetPx = with(density) { horizontalOffset.toPx() * .9f }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width - offsetPx
            val centerY = size.height / 2f
            val radius = size.height * 0.45f

            // Current angle based on temperature
            val currentAngle = startAngle + ((animatedTemp - minTemp) / (maxTemp - minTemp)) * sweepAngle

            // 1. Draw the Main Track Arc
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

            // 2. NEW: Outer and Inner Border Arcs
            val outerRadius = radius + (strokeWidth / 2) + 10f
            val innerRadius = radius - (strokeWidth / 2) - 10f

// Calculate the progress sweep (from start to current knob position)
            val progressSweep = currentAngle - startAngle

// Outer Dynamic Border
            drawArc(
                color = Color.Black.copy(alpha = 0.2f), // Increased alpha slightly for visibility
                startAngle = startAngle,
                sweepAngle = progressSweep,
                useCenter = false,
                style = Stroke(width = 3f, cap = StrokeCap.Round),
                size = Size(outerRadius * 2, outerRadius * 2),
                topLeft = Offset(centerX - outerRadius, centerY - outerRadius)
            )

// Inner Dynamic Border
            drawArc(
                color = Color.Black.copy(alpha = 0.2f),
                startAngle = startAngle,
                sweepAngle = progressSweep,
                useCenter = false,
                style = Stroke(width = 3f, cap = StrokeCap.Round),
                size = Size(innerRadius * 2, innerRadius * 2),
                topLeft = Offset(centerX - innerRadius, centerY - innerRadius)
            )

            // 3. NEW: High-density Ticks with Proximity Scaling
            val numberOfLines = 120
            val lineDegreeStep = sweepAngle / numberOfLines
            val influenceRange = 3f // Degrees of influence around the knob

            for (i in 0..numberOfLines) {
                val lineAngle = startAngle + (i * lineDegreeStep)

                // Calculate distance from current temperature angle for scaling effect
                val angleDiff = abs(currentAngle - lineAngle)
                val scale = if (angleDiff < influenceRange) {
                    1f + (1f - (angleDiff / influenceRange)) * 0.5f // 15% growth
                } else {
                    1f
                }

                val angleRad = Math.toRadians(lineAngle.toDouble()).toFloat()
                val tickStart = radius * 1.15f
                val tickEnd = radius * (1.15f + (0.05f * scale))

                drawLine(
                    color = if (scale > 1f) Color.DarkGray else Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(
                        x = centerX + tickStart * cos(angleRad),
                        y = centerY + tickStart * sin(angleRad)
                    ),
                    end = Offset(
                        x = centerX + tickEnd * cos(angleRad),
                        y = centerY + tickEnd * sin(angleRad)
                    ),
                    strokeWidth = if (scale > 1f) 3f else 1.5f
                )
            }

            // 4. Draw the Knob (Thumb)
//            val knobAngleRad = Math.toRadians(currentAngle.toDouble()).toFloat()
//            drawCircle(
//                color = trackColor,
//                radius = strokeWidth * 0.9f,
//                center = Offset(
//                    x = centerX + radius * cos(knobAngleRad),
//                    y = centerY + radius * sin(knobAngleRad)
//                )
//            )

            val knobAngleRad = Math.toRadians(currentAngle.toDouble()).toFloat()
            val knobCenterX = centerX + radius * cos(knobAngleRad)
            val knobCenterY = centerY + radius * sin(knobAngleRad)

            val knobRadius = strokeWidth * 1.2f

            withTransform({
                // Rotate the canvas around the knob's center point
                // We add 180 because most humps are drawn "pointing left" (negative X)
                // but the circle starts at the right (0 degrees).
                rotate(degrees = currentAngle, pivot = Offset(knobCenterX, knobCenterY))
            }) {
                val path = Path().apply {
                    val peakHeight = knobRadius * .8f
                    val halfWidth = knobRadius * 2f

                    moveTo(knobCenterX, knobCenterY - halfWidth)

                    cubicTo(
                        x1 = knobCenterX, y1 = knobCenterY - halfWidth * 0.5f,
                        x2 = knobCenterX - peakHeight, y2 = knobCenterY - halfWidth * 0.2f,
                        x3 = knobCenterX - peakHeight, y3 = knobCenterY
                    )

                    cubicTo(
                        x1 = knobCenterX - peakHeight, y1 = knobCenterY + halfWidth * 0.2f,
                        x2 = knobCenterX, y2 = knobCenterY + halfWidth * 0.5f,
                        x3 = knobCenterX, y3 = knobCenterY + halfWidth
                    )
                    close()
                }

                drawPath(path = path, color = trackColor)
            }




        }

        // Temperature Text Overlay
        Text(
            text = "${animatedTemp.toInt()}°",
            modifier = Modifier.align(androidx.compose.ui.Alignment.CenterStart),
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

