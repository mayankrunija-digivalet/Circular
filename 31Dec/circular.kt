package com.example.slider_animation

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
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
import com.example.slider_animation.ui.theme.Gold
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.sp

@Composable
fun Controller() {
//    val startAngle = 214f
//    val endAngle = 324f
    val startAngle = 214f
    val endAngle = 324f
    var angle by remember { mutableStateOf(startAngle) }
    val normalizedAngle = (angle - startAngle) % (endAngle - startAngle + 360f) + startAngle
//    val outOfScreen  = .9f
    val outOfScreen  = .9f

    val startValue = 16f
    val endValue = 32f

    val range = endValue - startValue
    val fraction = (normalizedAngle - startAngle) / (endAngle - startAngle)
    val value = startValue + (fraction * range)

    Canvas(
        modifier = Modifier.fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val center = Offset(
                        x = size.width / 2f,
                        y = size.height / 2f
                    )
                    val touchAngle = atan2(
                        change.position.y - center.y,
                        change.position.x - center.x
                    ) * 360f / Math.PI

                    // Ensure the angle stays between startAngle and endAngle
                    angle = (touchAngle.toFloat() + 90f).coerceIn(startAngle, endAngle)
                    Log.d("angle", angle.toString())
                }
            }
    )


    {
        val halfScreenWidth = size.width * .5f
//        val radius = halfScreenWidth - (halfScreenWidth * .25f)
        val radius = 800f
        val offsetX = size.width * outOfScreen

        val numberOfLine = 360
        val lineDegree = (360f / numberOfLine)
        val influenceRange = 3f

        for (lineNumbers in 0 until numberOfLine) {
            val lineAngle = lineDegree * lineNumbers

            val angleDiff = Math.abs(angle - lineAngle).let {
                if (it > 180) 360 - it else it
            }
            val scale = if (angleDiff < influenceRange) {
                1f + (1f - (angleDiff / influenceRange)) * 0.03f
            } else {
                1f
            }

            val angleInRadian = Math.toRadians((lineAngle - 90f).toDouble()).toFloat()
            val startLine = radius * 1.1f
            val endLine = radius * 1.13f * scale

            drawLine(
                color = if (scale > 1f) Color.LightGray else Color.LightGray,
                start = Offset(
                    x = size.center.x + offsetX + startLine * cos(angleInRadian),
                    y = size.center.y + startLine * sin(angleInRadian)
                ),
                end = Offset(
                    x = size.center.x + offsetX + endLine * cos(angleInRadian),
                    y = size.center.y + endLine * sin(angleInRadian)
                ),
                strokeWidth = if (scale > 1f) 1f else 1f
            )
        }


//        val knobCenter = Offset(
//            x = size.center.x + offsetX + radius * cos(Math.toRadians((angle - 90f).toDouble())).toFloat(),
//            y = size.center.y + radius * sin(Math.toRadians((angle - 90f).toDouble())).toFloat()
//        )
//
//        val triangleRadius = radius * .2f
//        val cornerRadius = triangleRadius * 2.5f
//
//        val trianglePath = Path().apply {
//            moveTo(
//                knobCenter.x + triangleRadius * cos(Math.toRadians((angle - 90f).toDouble())).toFloat(),
//                knobCenter.y + triangleRadius * sin(Math.toRadians((angle - 90f).toDouble())).toFloat()
//            )
//            lineTo(
//                knobCenter.x + triangleRadius *  cos(Math.toRadians((angle + 30f).toDouble())).toFloat(),
//                knobCenter.y + triangleRadius *  sin(Math.toRadians((angle + 30f).toDouble())).toFloat()
//            )
//            lineTo(
//                knobCenter.x + triangleRadius * cos(Math.toRadians((angle + 150f).toDouble())).toFloat(),
//                knobCenter.y + triangleRadius * sin(Math.toRadians((angle + 150f).toDouble())).toFloat()
//            )
//            close()
//        }
//
//        drawIntoCanvas { canvas ->
//            canvas.drawOutline(
//                outline = Outline.Generic(trianglePath),
//                paint = Paint().apply {
//                    color = Gold
//                    pathEffect = PathEffect.cornerPathEffect(cornerRadius)
//                    style = PaintingStyle.Fill
//                }
//            )
//            canvas.drawOutline(
//                outline = Outline.Generic(trianglePath),
//                paint = Paint().apply {
//                    color = Color.White
//                    style = PaintingStyle.Stroke
//                    strokeWidth = radius * 0.01f
//                    pathEffect = PathEffect.cornerPathEffect(cornerRadius)
//                    strokeCap = StrokeCap.Round
//                    strokeJoin = StrokeJoin.Round
//                }
//            )
//        }

                val knobCenter = Offset(
            x = size.center.x + offsetX + radius * cos(Math.toRadians((angle - 90f).toDouble())).toFloat(),
            y = size.center.y + radius * sin(Math.toRadians((angle - 90f).toDouble())).toFloat()
        )

        val triangleRadius = radius * .14f

        val trianglePath = Path().apply {
            val bottomRadius = triangleRadius * .8f
            val bottomShrinkFactor = 1.6f
            val newBottomRadius = bottomRadius * bottomShrinkFactor
            val horizontalShift = -25f

            moveTo(
                knobCenter.x + triangleRadius * cos(Math.toRadians((angle - 90f).toDouble())).toFloat() + horizontalShift,
                knobCenter.y + triangleRadius * sin(Math.toRadians((angle - 90f).toDouble())).toFloat()
            )

            lineTo(
                knobCenter.x + newBottomRadius * cos(Math.toRadians((angle + 30f).toDouble())).toFloat() + horizontalShift,
                knobCenter.y + newBottomRadius * sin(Math.toRadians((angle + 30f).toDouble())).toFloat()
            )

            lineTo(
                knobCenter.x + newBottomRadius * cos(Math.toRadians((angle + 150f).toDouble())).toFloat() + horizontalShift,
                knobCenter.y + newBottomRadius * sin(Math.toRadians((angle + 150f).toDouble())).toFloat()
            )
            close()
        }


        drawIntoCanvas { canvas ->
            canvas.drawOutline(
                outline = Outline.Generic(trianglePath),
                paint = Paint().apply {
                    color = Gold
                    pathEffect = PathEffect.cornerPathEffect(triangleRadius)
                    style = PaintingStyle.Fill
                }
            )
            canvas.drawOutline(
                outline = Outline.Generic(trianglePath),
                paint = Paint().apply {
                    color = Color.Black
                    style = PaintingStyle.Stroke
                    strokeWidth = radius * 0.01f
                    pathEffect = PathEffect.cornerPathEffect(triangleRadius)
                    strokeCap = StrokeCap.Round
                    strokeJoin = StrokeJoin.Round
                }
            )
        }




        drawCircle(
            radius = radius,
            color = Gold,
            style = Stroke(width = (radius * .15f)),
            center = Offset(size.center.x + offsetX, size.center.y)
        )



        for (lineNumbers in 0 until (numberOfLine)) {
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



        val outerRadius = radius * 1.08f
        drawArc(
//            brush = fadeWhite,
            color = Color.Black,
            startAngle = -90f,
            sweepAngle = normalizedAngle - 3.2f,
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
            sweepAngle = normalizedAngle - 5.9f,
            useCenter = false,
            style = Stroke(width = radius * 0.01f),
            topLeft = Offset(
                x = size.center.x + offsetX - innerRadius,
                y = size.center.y - innerRadius
            ),
            size = Size(innerRadius * 2, innerRadius * 2)
        )




    }//canvas

    Text(
        text = "Value: ${value.toInt()}",
        style = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 10.sp),
        modifier = Modifier.padding(top = 200.dp, start = 200.dp)
    )
}

@Composable
@Preview(showBackground = true)
fun ControllerPreview() {
    Controller()
}


