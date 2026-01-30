package com.imtbytes.samazarqa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface // এই ইম্পোর্টটি দরকার ছিল
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imtbytes.samazarqa.data.AppTheme
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
            val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
            
            val isDarkTheme = when (appTheme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            SamazarqaTheme(darkTheme = isDarkTheme) {
                // Surface বাইরে আনা হয়েছে যাতে @Composable এরর না আসে
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
                            UiState.Loading -> { /* Loading View */ }
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
                                            isDarkTheme = isDarkTheme, // missing parameter fixed
                                            isSecure = state.isSecure,
                                            currentTheme = appTheme,
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