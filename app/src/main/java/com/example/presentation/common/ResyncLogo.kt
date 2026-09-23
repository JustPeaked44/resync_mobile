package com.example.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Animated Logo using the exact provided brand PNG asset (resynce_logo.png).
 * 
 * Animation Stages:
 * 1. Shows only the exact S-symbol from the PNG, perfectly centered on the screen.
 * 2. The S-symbol smoothly shifts to the left while the exact "Re Sync" text slides
 *    out from behind the symbol to reveal the full authentic logo.
 * 3. Settles as the complete official logo with 100% authentic typography and spacing.
 */
@Composable
fun ResyncAnimatedLogo(
    modifier: Modifier = Modifier,
    logoWidth: Dp = 240.dp,
    logoHeight: Dp = 110.dp,
    onAnimationFinish: () -> Unit = {}
) {
    val symbolAlpha = remember { Animatable(0f) }
    val symbolScale = remember { Animatable(0.75f) }
    val slideProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Stage 1: Symbol fades in & pops into the center of the screen
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

        // Hold centered S-symbol
        delay(400)

        // Stage 2: S-symbol shifts left while "Re Sync" text slides out to the right
        slideProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 850,
                easing = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
            )
        )

        // Hold complete logo
        delay(600)
        onAnimationFinish()
    }

    val progress = slideProgress.value

    // When progress = 0: offset image to center the S-symbol in the screen
    // When progress = 1: offset is 0.dp (entire logo centered in the screen)
    val shiftX = (0.29f * (1f - progress)) * logoWidth.value

    // When progress = 0: clip width reveals only the S-symbol (~41% of logo width)
    // When progress = 1: clip width reveals the full image (100% of logo width)
    val revealFraction = 0.41f + (0.59f * progress)

    Box(
        modifier = modifier
            .wrapContentSize()
            .scale(symbolScale.value)
            .alpha(symbolAlpha.value),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width((logoWidth.value * revealFraction).dp)
                .height(logoHeight)
                .offset(x = shiftX.dp)
                .clipToBounds(),
            contentAlignment = Alignment.CenterStart
        ) {
            Image(
                painter = painterResource(id = R.drawable.resynce_logo),
                contentDescription = "Resync Logo",
                modifier = Modifier
                    .width(logoWidth)
                    .height(logoHeight),
                contentScale = ContentScale.Fit
            )
        }
    }
}

/**
 * Reusable static Resync Logo using the exact PNG asset.
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
