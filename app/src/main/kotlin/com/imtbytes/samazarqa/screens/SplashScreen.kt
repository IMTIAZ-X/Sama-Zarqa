package com.imtbytes.samazarqa.screens.splash

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
import com.imtbytes.samazarqa.ui.theme.*

@Composable
fun SplashScreen() {
    val alphaAnim = rememberSaveable { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SAMAZARQA",
                color = Color.White.copy(alpha = alphaAnim.value),
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}
