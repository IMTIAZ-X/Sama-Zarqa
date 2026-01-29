package com.imtbytes.samazarqa.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imtbytes.samazarqa.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay

@Composable
fun ScannerScreen(isDarkTheme: Boolean) {
    var isScanning by remember { mutableStateOf(true) }
    
    // Scanner Line Animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 280f, // Scan area height
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "QR Scanner",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
        )
        Text(
            text = "Align code within the frame",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Scanner Area
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color.Black, RoundedCornerShape(24.dp))
                .border(2.dp, PrimaryBlue, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            // Simulated Camera Preview (Dark gray for now)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF121212))
            ) {
                 // Crosshair corners
                 // (Simulated UI elements would go here)
                 Icon(
                     imageVector = Icons.Rounded.QrCodeScanner,
                     contentDescription = null,
                     tint = Color.White.copy(alpha = 0.1f),
                     modifier = Modifier.align(Alignment.Center).size(150.dp)
                 )
            }

            // Animated Scan Line
            Box(
                modifier = Modifier
                    .offset(y = scanLineY.dp)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                PrimaryBlue,
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Toggle Flash */ },
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(Icons.Rounded.FlashOn, contentDescription = "Flash")
            }
            
            Spacer(modifier = Modifier.width(32.dp))
            
            // Scan Button
            Button(
                onClick = { isScanning = !isScanning },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.CenterFocusWeak, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if(isScanning) "Scanning..." else "Scan")
            }
            
            Spacer(modifier = Modifier.width(32.dp))
            
            IconButton(
                onClick = { /* Gallery Pick */ },
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(Icons.Rounded.Image, contentDescription = "Gallery")
            }
        }
    }
}