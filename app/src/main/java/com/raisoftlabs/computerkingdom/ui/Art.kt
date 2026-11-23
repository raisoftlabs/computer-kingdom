package com.raisoftlabs.computerkingdom.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.raisoftlabs.computerkingdom.game.CharacterId
import com.raisoftlabs.computerkingdom.game.Expression
import kotlin.math.sin

private val FaceDark = Color(0xFF101733)
private val EyeWhite = Color(0xFFFFFFFF)

@Composable
fun KingdomBackdrop(content: @Composable () -> Unit) {
    val transition = rememberInfiniteTransition(label = "backdrop")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Reverse),
        label = "drift",
    )
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Ink, Color(0xFF111A3E), Color(0xFF0B1129))))
    ) {
        CanvasBackdrop(drift)
        content()
    }
}

@Composable
private fun CanvasBackdrop(drift: Float) {
    androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        for (index in 0 until 26) {
            val x = ((index * 137) % 1000) / 1000f * w
            val y = ((index * 79) % 700) / 700f * h * 0.62f
            val radius = if (index % 5 == 0) 2.4.dp.toPx() else 1.3.dp.toPx()
            drawCircle(color = Cream.copy(alpha = 0.18f + 0.16f * sin((index + drift * 3f).toDouble()).toFloat()), radius = radius, center = Offset(x, y))
        }
        val hill = Path().apply {
            moveTo(0f, h * 0.82f)
            quadraticBezierTo(w * 0.25f, h * (0.72f + drift * 0.01f), w * 0.5f, h * 0.84f)
            quadraticBezierTo(w * 0.78f, h * 0.94f, w, h * 0.8f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(hill, Color(0xFF131E48).copy(alpha = 0.65f))
        val hill2 = Path().apply {
            moveTo(0f, h * 0.91f)
            quadraticBezierTo(w * 0.4f, h * 0.84f, w * 0.7f, h * 0.93f)
            quadraticBezierTo(w * 0.88f, h * 0.97f, w, h * 0.9f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(hill2, Color(0xFF0A0F26).copy(alpha = 0.8f))
        for (index in 0 until 4) {
            val x = w * (0.12f + index * 0.26f)
            val y = h * (0.16f + (index % 2) * 0.1f) + drift * 14.dp.toPx()
            drawCloud(x, y, 34.dp.toPx())
        }
    }
}

private fun DrawScope.drawCloud(x: Float, y: Float, radius: Float) {
    val color = Color(0xFF8FA3DF).copy(alpha = 0.08f)
    drawCircle(color, radius, Offset(x, y))
    drawCircle(color, radius * 0.8f, Offset(x + radius * 0.9f, y + radius * 0.15f))
    drawCircle(color, radius * 0.7f, Offset(x - radius * 0.85f, y + radius * 0.2f))
}

@Composable
fun CharacterArt(
    character: CharacterId,
    expression: Expression = Expression.HAPPY,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.Canvas(modifier) {
        val unit = minOf(size.width, size.height) / 100f
        val scale = Size(unit * 100f, unit * 100f)
        val topLeft = Offset((size.width - scale.width) / 2f, (size.height - scale.height) / 2f)
        when (character) {
            CharacterId.BYTE -> drawByte(topLeft, unit, expression)
            CharacterId.CORE -> drawCore(topLeft, unit, expression)
            CharacterId.RAMI -> drawRami(topLeft, unit, expression)
            CharacterId.STELLA -> drawStella(topLeft, unit, expression)
            CharacterId.PIXEL -> drawPixel(topLeft, unit, expression)
            CharacterId.VOLT -> drawVolt(topLeft, unit, expression)
            CharacterId.MOTHERBOARD -> drawMotherboard(topLeft, unit, expression)
        }
    }
}

private fun DrawScope.rect(color: Color, x: Float, y: Float, w: Float, h: Float, corner: Float, unit: Float, outlined: Boolean = true) {
    val topLeft = Offset(x * unit, y * unit)
    val dimensions = Size(w * unit, h * unit)
    drawRoundRect(color, topLeft, dimensions, androidx.compose.ui.geometry.CornerRadius(corner * unit, corner * unit))
    if (outlined) {
        drawRoundRect(FaceDark, topLeft, dimensions, androidx.compose.ui.geometry.CornerRadius(corner * unit, corner * unit), style = Stroke(2.2f * unit))
    }
}

private fun DrawScope.circle(color: Color, x: Float, y: Float, radius: Float, unit: Float, outlined: Boolean = false) {
    drawCircle(color, radius * unit, Offset(x * unit, y * unit))
    if (outlined) drawCircle(FaceDark, radius * unit, Offset(x * unit, y * unit), style = Stroke(2f * unit))
}

private fun DrawScope.eyes(x1: Float, x2: Float, y: Float, expression: Expression, unit: Float, radius: Float = 6f) {
    val shift = when (expression) {
        Expression.HAPPY -> 0f
        Expression.THINK -> 1.6f
        Expression.SURPRISED -> 0f
    }
    circle(EyeWhite, x1, y, radius, unit)
    circle(EyeWhite, x2, y, radius, unit)
    circle(FaceDark, x1 + shift, y + 0.5f, radius * 0.5f, unit)
    circle(FaceDark, x2 + shift, y + 0.5f, radius * 0.5f, unit)
    if (expression == Expression.SURPRISED) {
        circle(EyeWhite, x1, y, radius * 1.2f, unit)
        circle(EyeWhite, x2, y, radius * 1.2f, unit)
        circle(FaceDark, x1, y, radius * 0.55f, unit)
        circle(FaceDark, x2, y, radius * 0.55f, unit)
    }
}

private fun DrawScope.mouth(x: Float, y: Float, expression: Expression, unit: Float, color: Color = FaceDark) {
    when (expression) {
        Expression.HAPPY -> drawArc(color, 15f, 150f, false, Offset((x - 11) * unit, (y - 8) * unit), Size(22 * unit, 18 * unit), style = Stroke(2.6f * unit))
        Expression.THINK -> drawLine(color, Offset((x - 6) * unit, y * unit), Offset((x + 6) * unit, (y - 1) * unit), strokeWidth = 2.6f * unit)
        Expression.SURPRISED -> circle(color, x, y, 4f, unit)
    }
}

private fun DrawScope.drawByte(topLeft: Offset, unit: Float, expression: Expression) {
    drawLine(Cyan, Offset(50f * unit, 22f * unit), Offset(50f * unit, 11f * unit), 3f * unit)
    circle(Gold, 50f, 9f, 5f, unit, true)
    rect(Cyan, 16f, 22f, 68f, 60f, 18f, unit)
    circle(Gold, 13f, 52f, 6f, unit, true)
    circle(Gold, 87f, 52f, 6f, unit, true)
    rect(InkSoft, 23f, 30f, 54f, 42f, 12f, unit, false)
    eyes(38f, 62f, 45f, expression, unit, 6f)
    mouth(50f, 56f, expression, unit, Gold)
}

private fun DrawScope.drawCore(topLeft: Offset, unit: Float, expression: Expression) {
    for (index in 0..3) {
        val pin = 34f + index * 11f
        drawLine(Lavender, Offset(pin * unit, 18f * unit), Offset(pin * unit, 10f * unit), 3f * unit)
        drawLine(Lavender, Offset(pin * unit, 82f * unit), Offset(pin * unit, 90f * unit), 3f * unit)
        drawLine(Lavender, Offset(18f * unit, pin * unit), Offset(10f * unit, pin * unit), 3f * unit)
        drawLine(Lavender, Offset(82f * unit, pin * unit), Offset(90f * unit, pin * unit), 3f * unit)
    }
    rect(Lavender, 18f, 20f, 64f, 60f, 12f, unit)
    drawRoundRect(FaceDark, Offset(30f * unit, 38f * unit), Size(16 * unit, 15 * unit), androidx.compose.ui.geometry.CornerRadius(6f * unit, 6f * unit), style = Stroke(2.4f * unit))
    drawRoundRect(FaceDark, Offset(54f * unit, 38f * unit), Size(16 * unit, 15 * unit), androidx.compose.ui.geometry.CornerRadius(6f * unit, 6f * unit), style = Stroke(2.4f * unit))
    drawLine(FaceDark, Offset(46f * unit, 44f * unit), Offset(54f * unit, 44f * unit), 2.4f * unit)
    eyes(38f, 62f, 45f, expression, unit, 4.6f)
    mouth(50f, 62f, expression, unit)
}

private fun DrawScope.drawRami(topLeft: Offset, unit: Float, expression: Expression) {
    rect(Gold, 18f, 44f, 20f, 36f, 4f, unit)
    rect(Cyan, 30f, 40f, 20f, 40f, 4f, unit)
    rect(Coral, 42f, 48f, 20f, 32f, 4f, unit)
    circle(Cream, 50f, 32f, 21f, unit, true)
    rect(Cyan, 33f, 52f, 34f, 34f, 12f, unit)
    eyes(42f, 58f, 30f, expression, unit, 5f)
    mouth(50f, 40f, expression, unit)
}

private fun DrawScope.drawStella(topLeft: Offset, unit: Float, expression: Expression) {
    circle(Cream, 50f, 30f, 20f, unit, true)
    drawArc(Color(0xFF6D4C41), 180f, 180f, false, Offset(29f * unit, 13f * unit), Size(42 * unit, 38 * unit), style = Stroke(5f * unit))
    rect(Mint, 30f, 50f, 40f, 38f, 14f, unit)
    rect(Gold, 56f, 58f, 22f, 18f, 3f, unit)
    drawLine(Cream, Offset(67f * unit, 58f * unit), Offset(67f * unit, 76f * unit), 1.6f * unit)
    eyes(43f, 57f, 30f, expression, unit, 4.6f)
    mouth(50f, 39f, expression, unit)
}

private fun DrawScope.drawPixel(topLeft: Offset, unit: Float, expression: Expression) {
    circle(Cream, 50f, 32f, 19f, unit, true)
    rect(Coral, 31f, 51f, 38f, 37f, 14f, unit)
    drawLine(Gold, Offset(76f * unit, 66f * unit), Offset(88f * unit, 28f * unit), 4f * unit)
    circle(Cyan, 89f, 25f, 5f, unit, true)
    circle(Lavender, 24f, 70f, 9f, unit, true)
    eyes(43f, 57f, 31f, expression, unit, 4.6f)
    mouth(50f, 40f, expression, unit)
}

private fun DrawScope.drawVolt(topLeft: Offset, unit: Float, expression: Expression) {
    rect(Gold, 20f, 26f, 60f, 60f, 14f, unit)
    val bolt = Path().apply {
        moveTo(56f * unit, 34f * unit)
        lineTo(38f * unit, 56f * unit)
        lineTo(49f * unit, 56f * unit)
        lineTo(42f * unit, 76f * unit)
        lineTo(63f * unit, 50f * unit)
        lineTo(51f * unit, 50f * unit)
        close()
    }
    drawPath(bolt, Cyan)
    eyes(38f, 62f, 34f, expression, unit, 3.8f)
}

private fun DrawScope.drawMotherboard(topLeft: Offset, unit: Float, expression: Expression) {
    rect(Mint, 14f, 20f, 72f, 60f, 10f, unit)
    for (index in 0..2) {
        val y = (32f + index * 16f) * unit
        drawLine(Gold, Offset(20f * unit, y), Offset(38f * unit, y), 2f * unit)
        drawLine(Cyan, Offset(62f * unit, y), Offset(80f * unit, y), 2f * unit)
    }
    rect(InkSoft, 40f, 38f, 20f, 24f, 4f, unit)
    eyes(45f, 55f, 46f, expression, unit, 3.2f)
    mouth(50f, 55f, expression, unit, Cyan)
}

@Composable
fun GlowDot(color: Color, modifier: Modifier = Modifier) {
    Box(modifier.drawBehind { drawCircle(color.copy(alpha = 0.2f), size.minDimension / 2f); drawCircle(color, size.minDimension / 5f) })
}
