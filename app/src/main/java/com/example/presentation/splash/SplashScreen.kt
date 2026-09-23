package com.example.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.common.ResyncAnimatedLogo
import com.example.presentation.common.ResyncBrandBlue

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.splashState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        when (state) {
            is SplashState.NavigateToOnboarding -> {
                onNavigate("onboarding")
            }
            is SplashState.NavigateToAuth -> {
                onNavigate("auth")
            }
            is SplashState.NavigateToDashboard -> {
                onNavigate("dashboard_host")
            }
            is SplashState.ShowSplash -> {
                // Showing animated logo
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        ResyncAnimatedLogo(
            symbolSize = 100.dp,
            brandColor = ResyncBrandBlue
        )
    }
}

