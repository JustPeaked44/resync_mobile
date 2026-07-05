package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.local.SessionManager
import com.example.data.remote.ResyncApiService
import com.example.data.repository.ScanRepositoryImpl
import com.example.domain.usecase.ScanManuscriptUseCase
import com.example.presentation.dashboard.DashboardViewModel
import com.example.presentation.navigation.ResyncNavHost
import com.example.presentation.splash.SplashViewModel
import com.example.presentation.auth.AuthViewModel
import com.example.presentation.onboarding.OnboardingViewModel
import com.example.ui.theme.ResyncTheme
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize SessionManager DataStore
        val sessionManager = SessionManager(applicationContext)

        // Initialize our network layer with Moshi converter and repository pattern
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.resync.example.com/") // Placeholder baseUrl for FastAPI backend routing
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        val apiService = retrofit.create(ResyncApiService::class.java)
        val repository = ScanRepositoryImpl(apiService)
        val scanManuscriptUseCase = ScanManuscriptUseCase(repository)
        
        // Manual constructor injection of dependencies into ViewModel
        val dashboardViewModel = DashboardViewModel(scanManuscriptUseCase, sessionManager, androidx.work.WorkManager.getInstance(applicationContext))
        val splashViewModel = SplashViewModel(sessionManager)
        val authViewModel = AuthViewModel(apiService, sessionManager)
        val onboardingViewModel = OnboardingViewModel(sessionManager)

        setContent {
            val themePreferenceState by sessionManager.themePreference.collectAsState(initial = "system")
            val isDarkTheme = when (themePreferenceState) {
                "light" -> false
                "dark" -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            ResyncTheme(isDarkTheme = isDarkTheme) {
                ResyncNavHost(
                    sessionManager = sessionManager,
                    dashboardViewModel = dashboardViewModel,
                    splashViewModel = splashViewModel,
                    authViewModel = authViewModel,
                    onboardingViewModel = onboardingViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

