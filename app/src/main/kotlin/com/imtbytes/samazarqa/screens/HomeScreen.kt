package com.imtbytes.samazarqa.screens.home

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imtbytes.samazarqa.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(isDarkTheme: Boolean, onThemeToggle: () -> Unit, isSecure: Boolean) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)

    // হ্যাপটিক ফিডব্যাক (ভাইব্রেশন) লজিক
    val triggerHaptic = {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(35)
            }
        } catch (e: Exception) {
            // পারমিশন ইস্যু থাকলে অ্যাপ ক্র্যাশ করবে না
        }
    }

    Scaffold(
        containerColor = if (isDarkTheme) Color(0xFF101010) else Color(0xFFFBFBFE),
        topBar = {
            LargeTopAppBar(
                title = { 
                    Column {
                        Text("SAMAZARQA", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text("Dashboard", fontSize = 26.sp, fontWeight = FontWeight.Black)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { triggerHaptic(); onThemeToggle() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.5f), CircleShape)
                    ) {
                        Icon(
                            if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, 
                            null, 
                            tint = PrimaryBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            
            // ১. স্মার্ট সিকিউরিটি কার্ড (Pulse Animation সহ)
            item {
                SecurityStatusCard(isSecure)
            }

            // ২. কুইক টুলস টাইটেল
            item {
                Text(
                    "Security Suite", 
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 18.sp,
                    color = if(isDarkTheme) Color.White else Color.Black
                )
            }

            // ৩. ইন্টারঅ্যাক্টিভ সার্ভিস গ্রিড (Figma Style)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ActionCard("System Lock", Icons.Default.AdminPanelSettings, Modifier.weight(1f), triggerHaptic) {
                            Toast.makeText(context, "System Hardening Active", Toast.LENGTH_SHORT).show()
                        }
                        ActionCard("WiFi Scan", Icons.Default.WifiTethering, Modifier.weight(1f), triggerHaptic) {
                            Toast.makeText(context, "Analyzing WiFi Security...", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ActionCard("Vault Pro", Icons.Default.EnhancedEncryption, Modifier.weight(1f), triggerHaptic) {
                            Toast.makeText(context, "Vault Locked", Toast.LENGTH_SHORT).show()
                        }
                        ActionCard("Log Wipe", Icons.Default.CleaningServices, Modifier.weight(1f), triggerHaptic) {
                            Toast.makeText(context, "Traces Cleared", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            // ৪. রিসেন্ট অ্যাক্টিভিটি লগ
            item {
                Text(
                    "Recent Protection Logs", 
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 10.dp),
                    color = if(isDarkTheme) Color.White else Color.Black
                )
            }

            item {
                LogItem("Encryption Active", "AES-256 Tunneling", Icons.Default.Security, Color(0xFF4CAF50))
                Spacer(Modifier.height(12.dp))
                LogItem("Anti-Tamper", "Monitoring Memory", Icons.Default.RemoveModerator, PrimaryBlue)
            }
        }
    }
}

@Composable
fun SecurityStatusCard(isSecure: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse), label = "pulse"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSecure) PrimaryBlue else Color(0xFFB71C1C)
        ),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Icon(
                Icons.Default.Shield, null, 
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = 30.dp, y = 10.dp)
                    .graphicsLayer(alpha = 0.1f),
                tint = Color.White
            )
            Column(Modifier.padding(24.dp).align(Alignment.CenterStart)) {
                Surface(color = Color.White.copy(0.2f), shape = RoundedCornerShape(8.dp)) {
                    Text(" LIVE SYSTEM ", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    if (isSecure) "System Secured" else "Security Risk", 
                    fontSize = 28.sp, 
                    fontWeight = FontWeight.Black, 
                    color = Color.White
                )
                Text("All protocols are active and running", color = Color.White.copy(0.8f), fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun ActionCard(title: String, icon: ImageVector, modifier: Modifier, triggerHaptic: () -> Unit, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(if (isPressed) 0.94f else 1f, label = "scale")

    Surface(
        modifier = modifier
            .graphicsLayer(scaleX = animatedScale, scaleY = animatedScale)
            .height(120.dp)
            .clickable(
                interactionSource = interactionSource, 
                indication = LocalIndication.current,
                onClick = { triggerHaptic(); onClick() }
            ),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isPressed) 2.dp else 6.dp
    ) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(48.dp).background(PrimaryBlue.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun LogItem(title: String, status: String, icon: ImageVector, color: Color) {
    Surface(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(color.copy(0.1f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text(status, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(Modifier.weight(1f))
            Box(Modifier.size(8.dp).background(color, CircleShape))
        }
    }
}