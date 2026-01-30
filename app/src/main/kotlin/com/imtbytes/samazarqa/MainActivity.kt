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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.imtbytes.samazarqa.data.AppTheme

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
            
            // ViewModel থেকে থিম স্টেট নেওয়া হচ্ছে
            val themePreference by viewModel.appTheme.collectAsStateWithLifecycle()
            
            val isDarkTheme = when (themePreference) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            SamazarqaTheme(darkTheme = isDarkTheme) {
                Surface {
                    AnimatedContent(
                        targetState = uiState,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(500)) togetherWith 
                            fadeOut(animationSpec = tween(500))
                        },
                        label = "screen_transition"
                    ) { state ->
                        when (state) {
                            UiState.Loading -> {
                                // Loading state handle
                            }
                            is UiState.Home -> {
                                if (state.isFirstLaunch) {
                                    SplashScreen(
                                        isFirstLaunch = true,
                                        onSplashFinished = {
                                            viewModel.completeOnboarding()
                                        }
                                    )
                                } else {
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
                                            isSecure = state.isSecure,
                                            currentTheme = themePreference,
                                            onThemeChanged = { newTheme ->
                                                viewModel.setTheme(newTheme)
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
}