package com.example.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlusJakartaSansFontFamily

@Composable
fun CoherenceGauge(
    score: Int,
    color: Color,
    size: Dp = 90.dp,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(score) {
        animatedProgress.animateTo(
            targetValue = score / 100f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 8.dp.toPx()
            val arcSize = Size(this.size.width - strokeWidth, this.size.height - strokeWidth)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            // Background Track Arc (270 degrees from 135 to 405)
            drawArc(
                color = Color(0xFFE2E8F0),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Animated Score Arc
            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress.value,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                fontSize = (size.value * 0.22f).sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSansFontFamily,
                color = Color(0xFF1E293B),
                lineHeight = (size.value * 0.22f).sp
            )
            Text(
                text = "/100",
                fontSize = (size.value * 0.085f).sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSansFontFamily,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
