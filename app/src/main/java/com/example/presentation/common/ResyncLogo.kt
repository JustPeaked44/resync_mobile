package com.example.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
 * Animated Resync Logo sequence using the exact brand asset (R.drawable.resynce_logo).
 *
 * Sequence:
 * 1. Initial State (Image 1): The S logo symbol appears centered on the screen with a clean scale/fade entrance.
 * 2. Slide State (Image 2): The S logo shifts left as the "Re\nSync" text smoothly slides out and reveals beside it.
 * 3. Final State (Image 3): The full official logo lockup is complete and centered.
 */
@Composable
fun ResyncAnimatedLogo(
    modifier: Modifier = Modifier,
    logoWidth: Dp? = null,
    logoHeight: Dp = 220.dp,
    onAnimationFinish: () -> Unit = {}
) {
    val painter = painterResource(id = R.drawable.resynce_logo)
    val aspectRatio = remember(painter.intrinsicSize) {
        if (painter.intrinsicSize.height > 0f) {
            painter.intrinsicSize.width / painter.intrinsicSize.height
        } else {
            1.18f
        }
    }
    val effectiveWidth = logoWidth ?: (logoHeight * aspectRatio)
    val effectiveHeight = if (logoWidth != null) (logoWidth / aspectRatio) else logoHeight

    val symbolAlpha = remember { Animatable(0f) }
    val symbolScale = remember { Animatable(0.72f) }
    val transitionProgress = remember { Animatable(0f) }

    // Split masks dividing the brand asset cleanly into the S-symbol and the ReSync typography
    val symbolShape = remember {
        GenericShape { size, _ ->
            addRect(Rect(0f, 0f, size.width * 0.42f, size.height))
        }
    }
    val textShape = remember {
        GenericShape { size, _ ->
            addRect(Rect(size.width * 0.415f, 0f, size.width, size.height))
        }
    }

    LaunchedEffect(Unit) {
        // Step 1: Initial S-logo appears centered on the screen
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
                stiffness = Spring.StiffnessLow
            )
        )

        // Hold the centered S-logo so the user sees the logo first
        delay(550)

        // Step 2: S-logo slides to the left while ReSync text slides out beside the logo
        transitionProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 850,
                easing = CubicBezierEasing(0.18f, 1.0f, 0.22f, 1.0f)
            )
        )

        // Step 3: Hold the full lockup
        delay(650)
        onAnimationFinish()
    }

    val progress = transitionProgress.value

    // When progress = 0: S-symbol is centered horizontally (center of symbol aligned with center of Box)
    // S-symbol center in the asset is at ~0.21 * width, Box center is 0.50 * width -> shift is ~0.29 * width
    val symbolOffsetX = ((effectiveWidth.value * 0.29f) * (1f - progress)).dp

    // ReSync text slides smoothly out from the left (adjacent to S-symbol) to its spot
    val textSlideOffset = ((-effectiveWidth.value * 0.26f) * (1f - progress)).dp
    val textAlpha = (progress * 1.5f).coerceIn(0f, 1f)

    Box(
        modifier = modifier.size(width = effectiveWidth, height = effectiveHeight),
        contentAlignment = Alignment.Center
    ) {
        // 1. Text Layer ("Re\nSync" typography sliding into place beside the logo)
        if (progress > 0.005f) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = textSlideOffset)
                    .alpha(textAlpha)
                    .clip(textShape),
                contentScale = ContentScale.FillBounds
            )
        }

        // 2. S-Logo Symbol Layer (starts centered on screen, slides left to make room for text)
        Image(
            painter = painter,
            contentDescription = "Resync Logo",
            modifier = Modifier
                .fillMaxSize()
                .offset(x = symbolOffsetX)
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
            addRect(Rect(0f, 0f, s.width * 0.42f, s.height))
        }
    }
    val fullWidth = size / 0.35f

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
                .height(size * 1.15f)
                .offset(x = (fullWidth.value * 0.29f).dp)
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

