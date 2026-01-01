package com.example.circular

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun Controller() {
    val minValue = 16f
    val maxValue = 32f
    var tempValue by remember { mutableFloatStateOf(24f) }

    val startAngle = 140f
    val sweepAngle = 80f
    val horizontalOffset = (-360).dp

    var isDraggingKnob by remember { mutableStateOf(false) }

    val animatedValue by animateFloatAsState(
        targetValue = tempValue,
        label = "Value"
    )

    val density = androidx.compose.ui.platform.LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val offsetPx = horizontalOffset.toPx() * .9f
                        val centerX = size.width - offsetPx
                        val centerY = size.height / 2f
                        val radius = size.height * 0.45f

                        val currentAngle = startAngle + ((tempValue - minValue) / (maxValue - minValue)) * sweepAngle
                        val angleRad = Math.toRadians(currentAngle.toDouble()).toFloat()

                        val knobCenterX = centerX + radius * cos(angleRad)
                        val knobCenterY = centerY + radius * sin(angleRad)

                        val strokeWidth = 80f
                        val touchRadius = strokeWidth * 1.5f

                        val distance = sqrt(
                            (offset.x - knobCenterX).toDouble().pow(2.0) +
                                    (offset.y - knobCenterY).toDouble().pow(2.0)
                        )

                        isDraggingKnob = distance <= touchRadius
                    },
                    onDragEnd = { isDraggingKnob = false },
                    onDragCancel = { isDraggingKnob = false },
                    onVerticalDrag = { change, dragAmount ->
                        if (isDraggingKnob) {
                            change.consume()
                            val sensitivity = 1200f
                            val delta = -(dragAmount / sensitivity) * (maxValue - minValue)
                            tempValue = (tempValue + delta).coerceIn(minValue, maxValue)
                        }
                    }
                )
            }
    ) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        val offsetPx = with(density) { horizontalOffset.toPx() * .9f }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width - offsetPx
            val centerY = size.height / 2f
            val radius = size.height * 0.45f

            val currentAngle = startAngle + ((animatedValue - minValue) / (maxValue - minValue)) * sweepAngle

            val trackColor = Color(0xFFC5B8A5)
            val strokeWidth = 80f



            val halfStroke = strokeWidth /1.7f
            val numberOfLines = 150
            val lineLengthEnd = sweepAngle + 10f
            val lineLengthStart = startAngle -5f
            val lineDegreeStep = lineLengthEnd / numberOfLines
            val influenceRange = 4f
            val tickLength = strokeWidth * 0.15f

            for (i in 0..numberOfLines) {
                val lineAngle = lineLengthStart + (i * lineDegreeStep)
                val angleDiff = abs(currentAngle - lineAngle)

                val scale = if (angleDiff < influenceRange) {
                    (1f - (angleDiff / influenceRange)) * 3f
                } else {
                    0f
                }

                val angleRad = Math.toRadians(lineAngle.toDouble()).toFloat()

                val tickStart = radius + halfStroke
                val tickEnd = tickStart + tickLength + (tickLength * scale)

                drawLine(
                    color = Color.LightGray,
                    start = Offset(
                        x = centerX + tickStart * cos(angleRad),
                        y = centerY + tickStart * sin(angleRad)
                    ),
                    end = Offset(
                        x = centerX + tickEnd * cos(angleRad),
                        y = centerY + tickEnd * sin(angleRad)
                    ),
                    strokeWidth = if (scale > 0f) 3f else 3f
                )
            }




            val knobAngleRad = Math.toRadians(currentAngle.toDouble()).toFloat()
            val knobCenterX = centerX + radius * cos(knobAngleRad)
            val knobCenterY = centerY + radius * sin(knobAngleRad)

            val knobRadius = strokeWidth * 1.2f

            withTransform({
                rotate(degrees = currentAngle, pivot = Offset(knobCenterX, knobCenterY))
            }) {
                val path = Path().apply {
                    val peakHeight = knobRadius * 0.8f
                    val halfWidth = knobRadius * 2f
                    moveTo(knobCenterX, knobCenterY - halfWidth)

                    cubicTo(
                        x1 = knobCenterX, y1 = knobCenterY - halfWidth * 0.5f,
                        x2 = knobCenterX + peakHeight, y2 = knobCenterY - halfWidth * 0.2f,
                        x3 = knobCenterX + peakHeight, y3 = knobCenterY
                    )

                    cubicTo(
                        x1 = knobCenterX + peakHeight, y1 = knobCenterY + halfWidth * 0.2f,
                        x2 = knobCenterX, y2 = knobCenterY + halfWidth * 0.5f,
                        x3 = knobCenterX, y3 = knobCenterY + halfWidth
                    )
                    close()
                }

                drawPath(path = path, color = trackColor)
                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(centerX - radius, centerY - radius)
            )

            val outerRadius = radius + (strokeWidth / 2)
            val innerRadius = radius - (strokeWidth / 2)

            val progressSweep = currentAngle - startAngle

            drawArc(
                color = Color.White,
                startAngle = startAngle,
                sweepAngle = progressSweep-3.95f,
                useCenter = false,
                style = Stroke(width = 5f, cap = StrokeCap.Round),
                size = Size(outerRadius * 2, outerRadius * 2),
                topLeft = Offset(centerX - outerRadius, centerY - outerRadius)
            )

            drawArc(
                color = Color.White,
                startAngle = startAngle,
                sweepAngle = progressSweep-5f,
                useCenter = false,
                style = Stroke(width = 5f, cap = StrokeCap.Round),
                size = Size(innerRadius * 2, innerRadius * 2),
                topLeft = Offset(centerX - innerRadius, centerY - innerRadius)
            )



            for (i in 0..numberOfLines) {
                val lineAngle = startAngle + (i * lineDegreeStep)
                val angleRad = Math.toRadians(lineAngle.toDouble()).toFloat()

                val tickStart = radius - halfStroke
                val tickEnd = tickStart - tickLength

                drawLine(
                    color = Color.LightGray,
                    start = Offset(
                        x = centerX + tickStart * cos(angleRad),
                        y = centerY + tickStart * sin(angleRad)
                    ),
                    end = Offset(
                        x = centerX + tickEnd * cos(angleRad),
                        y = centerY + tickEnd * sin(angleRad)
                    ),
                    strokeWidth = 3f
                )
            }





        }

        Text(
            text = "${animatedValue.toInt()}°",
            modifier = Modifier.align(CenterStart),
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

