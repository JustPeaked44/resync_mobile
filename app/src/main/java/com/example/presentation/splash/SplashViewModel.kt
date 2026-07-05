package com.example.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface SplashState {
    object ShowSplash : SplashState
    object NavigateToOnboarding : SplashState
    object NavigateToAuth : SplashState
    object NavigateToDashboard : SplashState
}

class SplashViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _splashState = MutableStateFlow<SplashState>(SplashState.ShowSplash)
    val splashState: StateFlow<SplashState> = _splashState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            // Keep splash visible for a minimum duration to show branding
            delay(1500)
            val hasCompletedOnboarding = sessionManager.hasCompletedOnboarding.first()
            if (!hasCompletedOnboarding) {
                _splashState.value = SplashState.NavigateToOnboarding
            } else {
                val isLoggedIn = sessionManager.isLoggedIn.first()
                if (isLoggedIn) {
                    _splashState.value = SplashState.NavigateToDashboard
                } else {
                    _splashState.value = SplashState.NavigateToAuth
                }
            }
        }
    }
}
