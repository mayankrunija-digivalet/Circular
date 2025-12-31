//package com.example.slider_animation
//
//import android.util.Log
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.wrapContentSize
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.geometry.center
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.text.font.FontWeight
//import kotlin.math.atan2
//import kotlin.math.cos
//import kotlin.math.sin
//import kotlin.math.roundToInt
//
//@Composable
//fun MainScreen() {
//    // Current temperature state (using float for smooth movement)
//    var currentTemp by remember { mutableStateOf(16f) }
//
//    val minTemp = 16f
//    val maxTemp = 32f
//
//    // Visual Angles (Android Canvas: 0 is 3 o'clock)
//    // 135 degrees is bottom-left, 45 degrees is bottom-right
//    val startAngle = 180f  // Starting at the bottom (180 degrees)
//    val sweepAngle = 180f  // Half-circle (180 degrees)
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(end = 16.dp), // To leave a little margin on the right
//        horizontalAlignment = Alignment.End
//    ) {
//        Canvas(
//            modifier = Modifier
//                .wrapContentSize() // Ensures the Canvas takes just the needed space
//                .pointerInput(Unit) {
//                    detectDragGestures { change, _ ->
//                        val center = Offset(size.width / 2f + (size.width * .1f), size.height / 2f)
//
//                        // 1. Get touch angle in Radians, then Degrees
//                        val touchAngleRad = atan2(
//                            change.position.y - center.y,
//                            change.position.x - center.x
//                        )
//                        var touchAngleDeg = Math.toDegrees(touchAngleRad.toDouble()).toFloat()
//
//                        // 2. Normalize angle: make it relative to our startAngle (135)
//                        // We want the result to be 0 at the start of the dial and 180 at the end
//                        var relativeAngle = (touchAngleDeg - startAngle) % 360
//                        if (relativeAngle < 0) relativeAngle += 360
//
//                        // 3. Convert angle to 0.0 - 1.0 progress
//                        // We ignore touches in the "dead zone" (the bottom gap)
//                        if (relativeAngle <= sweepAngle || relativeAngle > 180f) {
//                            val progress = (relativeAngle.coerceAtMost(sweepAngle) / sweepAngle).coerceIn(0f, 1f)
//                            currentTemp = minTemp + (progress * (maxTemp - minTemp))
//                        }
//                        Log.d("angle", relativeAngle.toString())
//                    }
//                }
//        ) {
//            val radius = 700f
//            val offsetX = size.width * .8f
//            val canvasCenter = Offset(size.center.x + offsetX, size.center.y)
//
//            // Draw the background track (Optional, helps visual)
//            drawArc(
//                color = Color.LightGray.copy(alpha = 0.3f),
//                startAngle = startAngle,
//                sweepAngle = sweepAngle,
//                useCenter = false,
//                style = Stroke(width = radius * 0.15f, cap = StrokeCap.Round),
//                topLeft = Offset(canvasCenter.x - radius, canvasCenter.y - radius),
//                size = Size(radius * 2, radius * 2)
//            )
//
//            // Draw the Gold active track
//            val currentSweep = ((currentTemp - minTemp) / (maxTemp - minTemp)) * sweepAngle
//            drawArc(
//                color = Color(0xFFD4AF37), // Gold
//                startAngle = startAngle,
//                sweepAngle = currentSweep,
//                useCenter = false,
//                style = Stroke(width = radius * 0.15f, cap = StrokeCap.Round),
//                topLeft = Offset(canvasCenter.x - radius, canvasCenter.y - radius),
//                size = Size(radius * 2, radius * 2)
//            )
//
//            // Calculate Knob position
//            val knobAngle = startAngle + currentSweep
//            val knobAngleRad = Math.toRadians(knobAngle.toDouble())
//            val knobCenter = Offset(
//                x = canvasCenter.x + radius * cos(knobAngleRad).toFloat(),
//                y = canvasCenter.y + radius * sin(knobAngleRad).toFloat()
//            )
//
//            // Draw the knob
//            drawCircle(
//                radius = radius * 0.1f,
//                color = Color.White,
//                center = knobCenter,
//            )
//        }
//
//        // Display the Temperature
//        Column(modifier = Modifier.padding(32.dp)) {
//            Text(
//                text = "${currentTemp.toInt()}°C",
//                style = androidx.compose.ui.text.TextStyle(
//                    color = Color.Black,
//                    fontSize = 48.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            )
//            Text(text = "Adjust Temperature", style = androidx.compose.ui.text.TextStyle(color = Color.Gray))
//        }
//    }
//}
package com.example.slider_animation

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.unit.sp
import com.example.slider_animation.ui.theme.BgColor
import com.example.slider_animation.ui.theme.Gold

@Composable
fun MainScreen() {
    val startAngle = 0f
    val endAngle = 360f
    var angle by remember { mutableStateOf(startAngle) }
    val normalizedAngle = (angle - startAngle) % (endAngle - startAngle + 360f) + startAngle
    val outOfScreen = .05f

    val startValue = 16f
    val endValue = 32f

    val range = endValue - startValue
    val fraction = (normalizedAngle - startAngle) / (endAngle - startAngle)
    val value = startValue + (fraction * range)

    // Keep radius as a Float for drawing calculations
    val radius = 700f  // In pixels, not dp

    Canvas(
        modifier = Modifier.fillMaxSize().background(BgColor)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    // Calculate the distance from the center to the touch point
                    val center = Offset(
                        x = size.width / 2f,
                        y = size.height / 2f
                    )

                    val distance = center.getDistanceTo(change.position)

                    // Check if the touch is within the stroke's area (radius to radius + stroke width)
                    val strokeWidth = radius * .15f
                    val outerRadius = radius + strokeWidth

                    if (distance in radius..outerRadius) {
                        val touchAngle = atan2(
                            change.position.y - center.y,
                            change.position.x - center.x
                        ) * 360f / Math.PI

                        // Ensure the angle stays between startAngle and endAngle
                        angle = (touchAngle.toFloat() + 90f).coerceIn(startAngle, endAngle)
                        Log.d("angle", angle.toString())
                    }
                }
            }
    ) {
        val halfScreenWidth = size.width * .5f
        val offsetX = size.width * outOfScreen

        // Draw the main circle (outer track)
        drawCircle(
            radius = radius,
            color = Gold,
            style = Stroke(width = (radius * .15f)),
            center = Offset(size.center.x + offsetX, size.center.y)
        )

        val numberOfLine = 360
        val lineDegree = (360 / numberOfLine)

        // Draw radial lines around the circle
        for (lineNumbers in 0 until numberOfLine) {
            val angleInDegree = lineDegree * lineNumbers - 90f
            val angleInRadian = Math.toRadians(angleInDegree.toDouble()).toFloat()

            val startLine = radius * 1.1f
            val endLine = radius * 1.13f

            drawLine(
                color = Color.LightGray,
                start = Offset(
                    x = size.center.x + offsetX + startLine * cos(angleInRadian),
                    y = size.center.y + startLine * sin(angleInRadian)
                ),
                end = Offset(
                    x = size.center.x + offsetX + endLine * cos(angleInRadian),
                    y = size.center.y + endLine * sin(angleInRadian)
                ),
                strokeWidth = 1f
            )
        }

        // Draw the inner radial lines
        for (lineNumbers in 0 until numberOfLine) {
            val angleInDegree = lineDegree * lineNumbers - 90f
            val angleInRadian = Math.toRadians(angleInDegree.toDouble()).toFloat()

            val startLine = radius * .87f
            val endLine = radius * .90f

            drawLine(
                color = Color.LightGray,
                start = Offset(
                    x = size.center.x + offsetX + startLine * cos(angleInRadian),
                    y = size.center.y + startLine * sin(angleInRadian)
                ),
                end = Offset(
                    x = size.center.x + offsetX + endLine * cos(angleInRadian),
                    y = size.center.y + endLine * sin(angleInRadian)
                ),
                strokeWidth = 1f
            )
        }

        // Draw the knob (circle indicating the current value)
        val knobCenter = Offset(
            x = size.center.x + offsetX + radius * cos(Math.toRadians((angle - 90f).toDouble())).toFloat(),
            y = size.center.y + radius * sin(Math.toRadians((angle - 90f).toDouble())).toFloat()
        )
        drawCircle(
            radius = radius * .078f,
            color = Color.White,
            center = knobCenter
        )

        // Draw the arc representing the current value
        val outerRadius = radius * 1.08f
        drawArc(
            color = Color.Black,
            startAngle = -90f,
            sweepAngle = normalizedAngle + 4f,
            useCenter = false,
            style = Stroke(width = radius * 0.01f),
            topLeft = Offset(
                x = size.center.x + offsetX - outerRadius,
                y = size.center.y - outerRadius
            ),
            size = Size(outerRadius * 2, outerRadius * 2)
        )

        val innerRadius = radius * 0.92f
        drawArc(
            color = Color.Black,
            startAngle = -90f,
            sweepAngle = normalizedAngle - 1.5f,
            useCenter = false,
            style = Stroke(width = radius * 0.01f),
            topLeft = Offset(
                x = size.center.x + offsetX - innerRadius,
                y = size.center.y - innerRadius
            ),
            size = Size(innerRadius * 2, innerRadius * 2)
        )
    }

    // Display the value based on the angle
    Text(
        text = "Value: ${value.toInt()}",
        style = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 10.sp),
        modifier = Modifier.padding(top = 200.dp, start = 200.dp)
    )
}

@Composable
@Preview(showBackground = true)
fun Preview() {
    MainScreen()
}

// Extension function to calculate distance from two points
fun Offset.getDistanceTo(other: Offset): Float {
    return sqrt(
        ((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y)).toDouble()
    ).toFloat()
}
