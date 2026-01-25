package com.imtbytes.samazarqa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imtbytes.samazarqa.ui.theme.SamazarqaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            
            // ViewModel কানেক্ট করা হলো
            val viewModel: MainViewModel = viewModel()
            // ViewModel থেকে স্টেট সংগ্রহ করা হচ্ছে (Lifecycle-aware ভাবে)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            SamazarqaTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 🔥 স্মুথ ট্রানজিশন অ্যানিমেশন
                    AnimatedContent(
                        targetState = uiState,
                        transitionSpec = {
                            // নতুন স্ক্রিন আসার সময় Fade In হবে, পুরোনোটি Fade Out হবে (১ সেকেন্ড ধরে)
                            fadeIn(animationSpec = tween(1000)) togetherWith
                                    fadeOut(animationSpec = tween(1000))
                        },
                        label = "Screen Transition"
                    ) { targetState ->
                        when (targetState) {
                            is UiState.Loading -> {
                                SplashScreen()
                            }
                            is UiState.Home -> {
                                HomeScreen(
                                    isDarkTheme = isDarkTheme,
                                    onThemeToggle = { isDarkTheme = !isDarkTheme },
                                    isSecure = targetState.isSecure // ViewModel থেকে পাওয়া স্ট্যাটাস
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}