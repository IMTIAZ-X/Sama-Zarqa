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
                        fadeIn(animationSpec = tween(700)) togetherWith fadeOut(animationSpec = tween(700))
                    },
                    label = "ScreenSwitch"
                ) { state ->
                    when (state) {
                        is UiState.Loading -> {
                            // Show nothing or a simple loading indicator
                            // This state is very brief
                        }
                       
                        is UiState.Home -> {
                            // FIX: Check if this is first launch
                            if (state.isFirstLaunch) {
                                // First launch - show splash + onboarding
                                SplashScreen(
                                    isFirstLaunch = true,
                                    onOnboardingFinished = { 
                                        // When onboarding finishes, save to DataStore and update state
                                        viewModel.navigateToHome()
                                    }
                                )
                            } else {
                                // Returning user - show home directly or with splash only
                                // FIX: Added a flag to decide
                                var showingSplash by remember { mutableStateOf(true) }
                                
                                if (showingSplash) {
                                    SplashScreen(
                                        isFirstLaunch = false,
                                        onOnboardingFinished = { 
                                            showingSplash = false
                                        }
                                    )
                                } else {
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
    }
}
