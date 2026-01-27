package com.imtbytes.samazarqa.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imtbytes.samazarqa.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay

/**
 * Enhanced Splash Screen with smooth animations
 * Displays for ~2 seconds before transitioning to onboarding or home
 */
@Composable
fun SplashScreen(
    onSplashFinished: suspend () -> Unit
) {
    // Logo fade-in animation
    val alphaAnim = remember { Animatable(0f) }
    
    // Scale animation for logo
    val scaleAnim = remember { Animatable(0.3f) }
    
    // Progress indicator visibility
    var showProgress by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Animate logo appearance
        launch {
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        
        launch {
            alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(900, easing = FastOutSlowInEasing)
            )
        }

        // Show progress indicator after logo appears
        delay(600)
        showProgress = true
        
        // Wait for total splash duration
        delay(1400)
        
        // Notify that splash is finished
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Logo Text
            Text(
                text = "SAMAZARQA",
                color = Color.White.copy(alpha = alphaAnim.value),
                fontSize = (36 * scaleAnim.value).sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp,
                modifier = Modifier.graphicsLayer(
                    scaleX = scaleAnim.value,
                    scaleY = scaleAnim.value
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            AnimatedVisibility(
                visible = alphaAnim.value > 0.7f,
                enter = fadeIn(tween(500)) + slideInVertically { -20 }
            ) {
                Text(
                    text = "Security Suite",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Progress Indicator
            AnimatedVisibility(
                visible = showProgress,
                enter = fadeIn(tween(400)) + scaleIn(tween(400))
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Version info at bottom
        AnimatedVisibility(
            visible = alphaAnim.value > 0.8f,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            enter = fadeIn(tween(600))
        ) {
            Text(
                text = "v1.0.0",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}
