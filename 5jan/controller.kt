package com.example.circular

import android.util.Log
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.slider_animation.ui.theme.BgColor
import com.example.slider_animation.ui.theme.BorderColor
import com.example.slider_animation.ui.theme.LineColor
import com.example.slider_animation.ui.theme.trackColor
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2


@Composable
fun Controller() {

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        Log.d("screen width","$screenWidth")

        val horizontalOffset = screenWidth * -0.55f
//        val horizontalOffset = -30.dp

        val minValue = 16f
        val maxValue = 32f


//    val minValue = 10f
//    val maxValue = 1f
        val tempValue = remember { Animatable(24f) }

        val startAngle = 125f
        val sweepAngle = 110f
        val strokeWidth = 130f
//    val horizontalOffset = (-250).dp

        var isDraggingKnob by remember { mutableStateOf(false) }

        val haptic = LocalHapticFeedback.current

        val animatedValue = remember { Animatable(minValue) }
        val scope = rememberCoroutineScope()


//    val animatedValue by animateFloatAsState(
//        targetValue = tempValue,
//        label = "Value"
//    )
//     val animatedValue = tempValue


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgColor)


                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown()

                        val offsetPx = horizontalOffset.toPx() * .9f
                        val centerX = size.width - offsetPx
                        val centerY = size.height / 2f
                        val deltaX = down.position.x - centerX
                        val deltaY = down.position.y - centerY


                        val radius = size.height * 0.35f
//                        val touchTolerance = 50f


                        val distanceFromCenter =
                            kotlin.math.sqrt((deltaX * deltaX + deltaY * deltaY).toDouble())
                                .toFloat()

//                        val minValidRadius = radius - (strokeWidth / 2) - touchTolerance
//                        val maxValidRadius = radius + (strokeWidth / 2) + touchTolerance

                        val minValidRadius = radius - (strokeWidth / 2)
                        val maxValidRadius = radius + (strokeWidth / 2)

                        val isTouchingArc = distanceFromCenter in minValidRadius..maxValidRadius


                        var touchAngle =
                            Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
                        if (touchAngle < 0) touchAngle += 360f

                        val endAngle = startAngle + sweepAngle

                        if (touchAngle in startAngle..endAngle && isTouchingArc) {

                            val ratio = (touchAngle - startAngle) / sweepAngle

                            val targetValue =
                                (minValue + ratio * (maxValue - minValue)).coerceIn(
                                    minValue,
                                    maxValue
                                )
                            val oldValueJump = targetValue.toInt()
                            Log.d("old target value touch", "$oldValueJump")
                            scope.launch {
                                tempValue.animateTo(
                                    targetValue = targetValue,
                                    animationSpec = tween(
                                        durationMillis = 700,
                                        easing = LinearOutSlowInEasing
                                    )
                                )
                            }

                            val newValueJump = tempValue.value.toInt()
                            Log.d("new target value touch", "$newValueJump")

                            if (oldValueJump != newValueJump) {
                                haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                            }

                            isDraggingKnob = true

                            drag(down.id) { change ->
                                val oldValue = tempValue.value.toInt()
                                Log.d("old target value drag", oldValue.toString())
                                val sensitivity = 1200f
                                val dragAmount = change.position.y - change.previousPosition.y
                                val delta = -(dragAmount / sensitivity) * (maxValue - minValue)

                                val nextValue =
                                    (tempValue.value + delta).coerceIn(minValue, maxValue)

                                scope.launch {
                                    tempValue.snapTo(nextValue)
                                }
                                val newValue = nextValue.toInt()
                                Log.d("new target value drag", newValue.toString())
                                if (oldValue != newValue) {
                                    haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                }
                                change.consume()
                            }
                            isDraggingKnob = false
                        }
                    }
                }


        )


        {


            val density = androidx.compose.ui.platform.LocalDensity.current
            val offsetPx = with(density) { horizontalOffset.toPx() * .9f }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width - offsetPx
                val centerY = size.height / 2f
                val radius = size.height * 0.35f


//            val currentAngle = startAngle + ((animatedValue - minValue) / (maxValue - minValue)) * sweepAngle
//            val currentAngle = startAngle + ((tempValue - minValue) / (maxValue - minValue)) * sweepAngle


                val gapInPixels = strokeWidth * 0.7f

                val dynamicOffsetAngle = Math.toDegrees((gapInPixels / radius).toDouble()).toFloat()


                val visualStartAngle = startAngle + dynamicOffsetAngle
                val visualSweepAngle = sweepAngle - (dynamicOffsetAngle * 2)

                val currentAngle =
                    visualStartAngle + ((tempValue.value - minValue) / (maxValue - minValue)) * visualSweepAngle


//                val visualStartAngle = startAngle + 12f
//                val visualSweepAngle = sweepAngle - 22f
//                val currentAngle =
//                    visualStartAngle + ((tempValue.value - minValue) / (maxValue - minValue)) * visualSweepAngle
//


                val halfStroke = strokeWidth / 1.7f
                val numberOfLines = 150
                val lineLengthEnd = sweepAngle + 8.5f
                val lineLengthStart = startAngle - 5f
                val lineDegreeStep = lineLengthEnd / numberOfLines
                val influenceRange = 4f
                val tickLength = strokeWidth * 0.22f

                for (i in 0..numberOfLines) {
                    val lineAngle = lineLengthStart + (i * lineDegreeStep)
                    val angleDiff = abs(currentAngle - lineAngle)

                    val scale = if (angleDiff < influenceRange) {
                        (1f - (angleDiff / influenceRange)) * 1.2f
                    } else {
                        0f
                    }

                    val angleRad = Math.toRadians(lineAngle.toDouble()).toFloat()

                    val tickStart = radius + halfStroke
                    val tickEnd = tickStart + tickLength + (tickLength * scale)

                    drawLine(
                        color = LineColor,
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




                for (i in 0..numberOfLines) {
                    val lineAngle = lineLengthStart + (i * lineDegreeStep)
                    val angleRad = Math.toRadians(lineAngle.toDouble()).toFloat()

                    val tickStart = radius - halfStroke
                    val tickEnd = tickStart - tickLength

                    drawLine(
                        color = LineColor,
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


                val outerRadius = radius + (strokeWidth / 2)
                val progressSweep = currentAngle - startAngle


                drawArc(
                    color = BorderColor,
                    startAngle = startAngle,
                    sweepAngle = progressSweep,

                    useCenter = false,
                    style = Stroke(width = 8f, cap = StrokeCap.Round),
                    size = Size(outerRadius * 2, outerRadius * 2),
                    topLeft = Offset(centerX - outerRadius, centerY - outerRadius)
                )


                val innerRadius = radius - (strokeWidth / 2) - 2f
                val fadeBrush = Brush.sweepGradient(
                    colorStops = arrayOf(
                        0.0f to BorderColor,
                        (progressSweep - 20f) / 360f to BorderColor,
                        progressSweep / 360f to BorderColor.copy(alpha = 0f)
                    ),
                    center = Offset(centerX, centerY)
                )




                rotate(startAngle, pivot = Offset(centerX, centerY)) {
                    drawArc(
                        brush = fadeBrush,
                        startAngle = 0f,
                        sweepAngle = progressSweep,
                        useCenter = false,
                        style = Stroke(width = 4f, cap = StrokeCap.Round),
                        size = Size(innerRadius * 2, innerRadius * 2),
                        topLeft = Offset(
                            centerX - innerRadius,
                            centerY - innerRadius
                        )
                    )
                }


                val knobAngleRad = Math.toRadians(currentAngle.toDouble()).toFloat()
                val knobCenterX = centerX + radius * cos(knobAngleRad)
                val knobCenterY = centerY + radius * sin(knobAngleRad)

                val knobRadius = strokeWidth * 1.2f

                withTransform({
                    rotate(degrees = currentAngle, pivot = Offset(knobCenterX, knobCenterY))
                }) {
                    val peakHeight = knobRadius * 0.65f
                    val halfWidth = knobRadius * 1.4f
                    val path = Path().apply {

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

                    drawPath(
                        path = path,
                        color = trackColor
                    )



                    drawPath(
                        path = path,
                        color = BorderColor,
                        style = Stroke(
                            width = 1.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )


//                val gradientBrush = Brush.linearGradient(
//                    colors = listOf(BorderColor, Color.Transparent),
//                    start = Offset(knobCenterX, knobCenterY),
//                    end = Offset(knobCenterX + peakHeight, knobCenterY)
//                )
//                drawPath(
//                    path = path,
//                    brush = gradientBrush,
//                    style = Stroke(
//                        width = 2.dp.toPx(),
//                        cap = StrokeCap.Round,
//                        join = StrokeJoin.Round
//                    )
//                )

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


            }

            Text(
                text = "${tempValue.value.toInt()}",
                modifier = Modifier
                    .align(CenterStart)
                    .padding(start = 50.dp),
                style = androidx.compose.ui.text.TextStyle(
                    color = Color.Black,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.ExtraLight
                )
            )


        }


    }
}



@Composable
@Preview(showBackground = true)
fun ControllerPreview() {
    Controller()
}
