package com.imtbytes.samazarqa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imtbytes.samazarqa.screens.home.HomeScreen
import com.imtbytes.samazarqa.screens.splash.SplashScreen
import com.imtbytes.samazarqa.ui.theme.SamazarqaTheme
import com.imtbytes.samazarqa.viewmodel.MainViewModel
import com.imtbytes.samazarqa.viewmodel.UiState

/**
 * Production-ready MainActivity
 * Optimized for performance and reliability
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            val viewModel: MainViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            SamazarqaTheme(darkTheme = isDarkTheme) {
                when (val state = uiState) {
                    is UiState.Loading -> {
                        // Show nothing or simple loading
                        // State will change quickly to Ready
                    }
                    
                    is UiState.Ready -> {
                        // Splash screen handles both first launch and returning users
                        SplashScreen(
                            isFirstLaunch = state.isFirstLaunch,
                            onFinished = {
                                if (state.isFirstLaunch) {
                                    // First time - save and go to home
                                    viewModel.completeOnboarding()
                                } else {
                                    // Returning user - just go to home
                                    viewModel.navigateToHome()
                                }
                            }
                        )
                    }
                    
                    is UiState.Home -> {
                        // Smooth transition to home
                        HomeScreen(
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = { isDarkTheme = !isDarkTheme },
                            isSecure = state.isSecure
                        )
                    }
                }
            }
        }
    }
}