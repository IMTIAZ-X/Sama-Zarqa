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
import com.imtbytes.samazarqa.screens.onboarding.OnboardingScreen
import com.imtbytes.samazarqa.screens.splash.SplashScreen
import com.imtbytes.samazarqa.ui.theme.SamazarqaTheme
import com.imtbytes.samazarqa.viewmodel.MainViewModel
import com.imtbytes.samazarqa.viewmodel.UiState

/**
 * Main Activity - Entry point of Samazarqa Security App
 * Features: Splash → Onboarding (first time) → Home
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
                AnimatedContent(
                    targetState = uiState,
                    transitionSpec = {
                        when {
                            initialState is UiState.Loading && targetState is UiState.Onboarding -> {
                                fadeIn(tween(600)) togetherWith fadeOut(tween(400))
                            }
                            initialState is UiState.Onboarding && targetState is UiState.Home -> {
                                slideInHorizontally(tween(700)) { it } + fadeIn(tween(700)) togetherWith
                                        slideOutHorizontally(tween(700)) { -it } + fadeOut(tween(700))
                            }
                            initialState is UiState.Loading && targetState is UiState.Home -> {
                                fadeIn(tween(700)) togetherWith fadeOut(tween(700))
                            }
                            else -> {
                                fadeIn(tween(500)) togetherWith fadeOut(tween(500))
                            }
                        }
                    },
                    label = "AppNavigation"
                ) { state ->
                    when (state) {
                        is UiState.Loading -> {
                            SplashScreen(
                                onSplashFinished = {
                                    viewModel.onSplashFinished()
                                }
                            )
                        }
                        
                        is UiState.Onboarding -> {
                            OnboardingScreen(
                                onFinished = {
                                    viewModel.completeOnboarding()
                                }
                            )
                        }
                        
                        is UiState.Home -> {
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
}
