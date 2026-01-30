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
import com.imtbytes.samazarqa.screens.HomeScreen
import com.imtbytes.samazarqa.screens.SplashScreen
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
            val viewModel: MainViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            
            val appTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
            
            val systemInDarkTheme = isSystemInDarkTheme()
            
            var currentTheme by remember { mutableStateOf(AppTheme.SYSTEM) }
            
           
            val isDarkTheme = when (currentTheme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            SamazarqaTheme(darkTheme = isDarkTheme) {
                AnimatedContent(
                    targetState = uiState,
                    transitionSpec = {
                    /*
                        when {
                            initialState is UiState.Loading && targetState is UiState.Home -> {
                                slideInHorizontally(tween(700)) { it } + fadeIn(tween(700)) togetherWith
                                        slideOutHorizontally(tween(700)) { -it } + fadeOut(tween(700))
                            }
                            else -> {
                                fadeIn(tween(500)) togetherWith fadeOut(tween(500))
                            }
                        }
                        */
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
                            // FIX: Check if first launch to decide splash behavior
                            if (state.isFirstLaunch) {
                                // First time user - show splash with onboarding
                                SplashScreen(
                                    isFirstLaunch = true,
                                    onSplashFinished = {
                                        viewModel.completeOnboarding()
                                    }
                                )
                            } else {
                                // FIX: Returning user - show splash then go to home
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
                    isSecure = true, // অথবা ViewModel থেকে ডাটা নিন
                    currentTheme = currentTheme,
                    isSecure = state.isSecure,
                    onThemeChanged = { newTheme ->
                        currentTheme = newTheme
                    }
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
