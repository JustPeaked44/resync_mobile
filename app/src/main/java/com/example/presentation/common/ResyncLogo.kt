package com.example.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val ResyncBrandBlue = Color(0xFF0010A8)

/**
 * Animated Resync Logo using the exact provided brand asset (R.drawable.resynce_logo).
 * 
 * Animation Stages:
 * 1. Initial State: The exact S-symbol appears pops/fades in directly at the center of the screen.
 * 2. Slide State: The S-symbol smoothly slides to the left, while the stacked "Re\nSync" text
 *    slides out to the right from behind the symbol with an easing curve and alpha fade.
 * 3. Final State: Settles into the exact complete official logo lockup.
 */
@Composable
fun ResyncAnimatedLogo(
    modifier: Modifier = Modifier,
    logoWidth: Dp? = null,
    logoHeight: Dp = 140.dp,
    onAnimationFinish: () -> Unit = {}
) {
    val painter = painterResource(id = R.drawable.resynce_logo)
    val aspectRatio = remember(painter.intrinsicSize) {
        if (painter.intrinsicSize.height > 0f) {
            painter.intrinsicSize.width / painter.intrinsicSize.height
        } else {
            1.15f
        }
    }
    val effectiveWidth = logoWidth ?: (logoHeight * aspectRatio)
    val effectiveHeight = if (logoWidth != null) (logoWidth / aspectRatio) else logoHeight

    val symbolAlpha = remember { Animatable(0f) }
    val symbolScale = remember { Animatable(0.72f) }
    val slideProgress = remember { Animatable(0f) }

    // Split masks dividing the brand asset cleanly into the S-symbol and the ReSync typography
    val symbolShape = remember {
        GenericShape { size, _ ->
            addRect(Rect(0f, 0f, size.width * 0.415f, size.height))
        }
    }
    val textShape = remember {
        GenericShape { size, _ ->
            addRect(Rect(size.width * 0.415f, 0f, size.width, size.height))
        }
    }

    LaunchedEffect(Unit) {
        // Stage 1: S symbol pops into the exact center of the screen
        launch {
            symbolAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
            )
        }
        symbolScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )

        // Hold the centered S symbol
        delay(450)

        // Stage 2: S symbol shifts left while ReSync text slides out to the right
        slideProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 900,
                easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
            )
        )

        // Hold the final complete logo
        delay(600)
        onAnimationFinish()
    }

    val progress = slideProgress.value

    // When progress = 0: S symbol is shifted +27.5% of width so its center is dead-center in the screen.
    // When progress = 1: S symbol is at offset 0 (its official left position in the full lockup).
    val symbolShiftX = (0.275f * (1f - progress)) * effectiveWidth.value

    // Text slides out to the right from behind the S symbol:
    // When progress = 0: offset is shifted left behind the symbol, alpha = 0.
    // When progress = 1: offset is 0dp, alpha = 1.
    val textShiftX = (-0.18f * (1f - progress)) * effectiveWidth.value
    val textAlpha = (progress * 1.5f).coerceIn(0f, 1f)

    Box(
        modifier = modifier.size(width = effectiveWidth, height = effectiveHeight),
        contentAlignment = Alignment.Center
    ) {
        // 1. Text Layer ("Re\nSync" sliding out to the right)
        if (progress > 0.01f) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(width = effectiveWidth, height = effectiveHeight)
                    .offset(x = textShiftX.dp)
                    .alpha(textAlpha)
                    .clip(textShape),
                contentScale = ContentScale.FillBounds
            )
        }

        // 2. S-Symbol Layer (starts centered, shifts left)
        Image(
            painter = painter,
            contentDescription = "Resync Logo",
            modifier = Modifier
                .size(width = effectiveWidth, height = effectiveHeight)
                .offset(x = symbolShiftX.dp)
                .scale(symbolScale.value)
                .alpha(symbolAlpha.value)
                .clip(symbolShape),
            contentScale = ContentScale.FillBounds
        )
    }
}

/**
 * Reusable static S-Symbol standalone composable using the exact brand asset.
 */
@Composable
fun ResyncSymbol(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    val symbolShape = remember {
        GenericShape { s, _ ->
            addRect(Rect(0f, 0f, s.width * 0.415f, s.height))
        }
    }
    val fullWidth = size / 0.32f

    Box(
        modifier = modifier
            .size(size)
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.resynce_logo),
            contentDescription = "Resync Symbol",
            modifier = Modifier
                .width(fullWidth)
                .height(size)
                .offset(x = ((fullWidth.value * 0.275f)).dp)
                .clip(symbolShape),
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * Reusable static full Resync Logo using the exact brand asset.
 */
@Composable
fun ResyncLogo(
    modifier: Modifier = Modifier,
    logoWidth: Dp = 180.dp,
    logoHeight: Dp = 80.dp
) {
    Image(
        painter = painterResource(id = R.drawable.resynce_logo),
        contentDescription = "Resync Logo",
        modifier = modifier
            .width(logoWidth)
            .height(logoHeight),
        contentScale = ContentScale.Fit
    )
}
