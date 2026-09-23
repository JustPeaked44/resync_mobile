package com.example.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlusJakartaSansFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

val ResyncBrandBlue = Color(0xFF0011B8)

/**
 * High-precision vector Canvas rendering of the Resync 'S' symbol.
 */
@Composable
fun ResyncSymbol(
    modifier: Modifier = Modifier,
    color: Color = ResyncBrandBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        // Proportions matching the Resync brand mark geometry
        val outerRadius = w * 0.48f
        val innerRadius = w * 0.22f
        val topCenterY = h * 0.36f
        val bottomCenterY = h * 0.64f
        val centerX = w * 0.5f

        // Top arc segment
        val topPath = Path().apply {
            val topRectOuter = Rect(
                centerX - outerRadius,
                topCenterY - outerRadius,
                centerX + outerRadius,
                topCenterY + outerRadius
            )
            val topRectInner = Rect(
                centerX - innerRadius,
                topCenterY - innerRadius,
                centerX + innerRadius,
                topCenterY + innerRadius
            )

            // Outer arc from 225 deg to 45 deg (180 deg sweep clockwise)
            arcTo(topRectOuter, 225f, 180f, forceMoveTo = true)

            // Cut edge at 45 deg to inner circle
            val rad45 = (45.0 * PI / 180.0)
            val inner45 = Offset(
                centerX + (innerRadius * cos(rad45)).toFloat(),
                topCenterY + (innerRadius * sin(rad45)).toFloat()
            )
            lineTo(inner45.x, inner45.y)

            // Inner arc from 45 deg back to 225 deg (180 deg sweep counter-clockwise)
            arcTo(topRectInner, 45f, -180f, forceMoveTo = false)

            close()
        }

        // Bottom arc segment
        val bottomPath = Path().apply {
            val bottomRectOuter = Rect(
                centerX - outerRadius,
                bottomCenterY - outerRadius,
                centerX + outerRadius,
                bottomCenterY + outerRadius
            )
            val bottomRectInner = Rect(
                centerX - innerRadius,
                bottomCenterY - innerRadius,
                centerX + innerRadius,
                bottomCenterY + innerRadius
            )

            // Outer arc from 45 deg to 225 deg (180 deg sweep clockwise)
            arcTo(bottomRectOuter, 45f, 180f, forceMoveTo = true)

            // Cut edge at 225 deg to inner circle
            val rad225 = (225.0 * PI / 180.0)
            val inner225 = Offset(
                centerX + (innerRadius * cos(rad225)).toFloat(),
                bottomCenterY + (innerRadius * sin(rad225)).toFloat()
            )
            lineTo(inner225.x, inner225.y)

            // Inner arc from 225 deg back to 45 deg (180 deg sweep counter-clockwise)
            arcTo(bottomRectInner, 225f, -180f, forceMoveTo = false)

            close()
        }

        drawPath(topPath, color)
        drawPath(bottomPath, color)
    }
}

/**
 * Animated Resync Logo component:
 * Starts with the 'S' symbol centered, then shifts smoothly to the left
 * while the "Re Sync" text slides out to the right.
 */
@Composable
fun ResyncAnimatedLogo(
    modifier: Modifier = Modifier,
    symbolSize: Dp = 90.dp,
    brandColor: Color = ResyncBrandBlue,
    onAnimationFinish: () -> Unit = {}
) {
    val symbolAlpha = remember { Animatable(0f) }
    val symbolScale = remember { Animatable(0.75f) }
    val textSlideProgress = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Stage 1: S symbol fades in & pops into center
        launch {
            symbolAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500, easing = LinearEasing)
            )
        }
        symbolScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // Hold centered symbol briefly
        delay(350)

        // Stage 2: S symbol shifts left while "Re Sync" text slides out to the right
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = LinearEasing)
            )
        }
        textSlideProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 850,
                easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f) // Smooth deceleration
            )
        )

        // Hold full logo
        delay(600)
        onAnimationFinish()
    }

    val progress = textSlideProgress.value
    val textWidth = (symbolSize * 1.35f) * progress
    val textSlideOffset = (35 * (1f - progress)).dp

    Row(
        modifier = modifier.wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // S Symbol
        ResyncSymbol(
            modifier = Modifier
                .size(width = symbolSize * 0.72f, height = symbolSize)
                .scale(symbolScale.value)
                .alpha(symbolAlpha.value),
            color = brandColor
        )

        // Sliding "Re Sync" text container
        Box(
            modifier = Modifier
                .width(textWidth)
                .height(symbolSize)
                .clipToBounds(),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .offset(x = -textSlideOffset)
                    .alpha(textAlpha.value),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(14.dp))
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Re",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (symbolSize.value * 0.44f).sp,
                        lineHeight = (symbolSize.value * 0.44f).sp,
                        letterSpacing = (-0.5).sp,
                        color = brandColor
                    )
                    Text(
                        text = "Sync",
                        fontFamily = PlusJakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (symbolSize.value * 0.44f).sp,
                        lineHeight = (symbolSize.value * 0.44f).sp,
                        letterSpacing = (-0.5).sp,
                        color = brandColor
                    )
                }
            }
        }
    }
}

/**
 * Static complete Resync Logo for general use throughout the app.
 */
@Composable
fun ResyncLogo(
    modifier: Modifier = Modifier,
    symbolSize: Dp = 64.dp,
    brandColor: Color = ResyncBrandBlue
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        ResyncSymbol(
            modifier = Modifier.size(width = symbolSize * 0.72f, height = symbolSize),
            color = brandColor
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Re",
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (symbolSize.value * 0.44f).sp,
                lineHeight = (symbolSize.value * 0.44f).sp,
                letterSpacing = (-0.5).sp,
                color = brandColor
            )
            Text(
                text = "Sync",
                fontFamily = PlusJakartaSansFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (symbolSize.value * 0.44f).sp,
                lineHeight = (symbolSize.value * 0.44f).sp,
                letterSpacing = (-0.5).sp,
                color = brandColor
            )
        }
    }
}
