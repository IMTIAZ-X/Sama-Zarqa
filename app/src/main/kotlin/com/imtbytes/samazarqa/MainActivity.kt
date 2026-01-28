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
// IMPORT FIX: এই লাইনটি নিশ্চিত করা হলো
import com.imtbytes.samazarqa.screens.splash.SplashScreen
import com.imtbytes.samazarqa.ui.theme.SamazarqaTheme
import com.imtbytes.samazarqa.viewmodel.MainViewModel
import com.imtbytes.samazarqa.viewmodel.UiState

/**
 * Main Activity - Entry point of Samazarqa Security App
 * Features: Splash+Onboarding (combined) → Home
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
                        // Custom transition for smoother UX
                        fadeIn(animationSpec = tween(700)) togetherWith 
                        fadeOut(animationSpec = tween(700))
                    },
                    label = "AppNavigation"
                ) { state ->
                    when (state) {
                        is UiState.Loading -> {
                            // Loading state - shows briefly
                        }
                        
                        is UiState.Home -> {
                            // Logic to decide between Splash or Home
                            if (state.isFirstLaunch) {
                                // First time user - show splash with onboarding
                                SplashScreen(
                                    isFirstLaunch = true,
                                    onSplashFinished = {
                                        viewModel.completeOnboarding()
                                    }
                                )
                            } else {
                                // Returning user - show splash briefly then home
                                var showingSplash by remember { mutableStateOf(true) }
                                
                                if (showingSplash) {
                                    SplashScreen(
                                        isFirstLaunch = false,
                                        onSplashFinished = {
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