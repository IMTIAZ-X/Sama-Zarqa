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

    // হ্যাপটিক ফিডব্যাক (ভাইব্রেশন) লজিক - Unit টাইপ নিশ্চিত করা হয়েছে
    val triggerHaptic: () -> Unit = {
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(35)
                }
            }
        } catch (e: Exception) { /* Ignore */ }
    }

    Scaffold(
        containerColor = if (isDarkTheme) Color(0xFF101010) else Color(0xFFFBFBFE),
        topBar = {
            LargeTopAppBar(
                title = { 
                    Column {
                        Text("SAMAZARQA", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text("Dashboard", fontSize = 26.sp, fontWeight = FontWeight.Black, color = if(isDarkTheme) Color.White else Color.Black)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { triggerHaptic(); onThemeToggle() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.5f), CircleShape)
                    ) {
                        Icon(if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, null, tint = PrimaryBlue)
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
            
            // ১. আপনার পছন্দের সেই ডিজাইন (SecurityStatusCard)
            item {
                SecurityStatusCard(isSecure)
            }

            item {
                Text("Security Suite", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = if(isDarkTheme) Color.White else Color.Black)
            }

            // ২. গ্রিড টুলস (ServiceCard ব্যবহার করে)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ServiceCard("System Lock", Icons.Default.AdminPanelSettings, Modifier.weight(1f), triggerHaptic) {
                            Toast.makeText(context, "System Hardened", Toast.LENGTH_SHORT).show()
                        }
                        ServiceCard("WiFi Scan", Icons.Default.WifiTethering, Modifier.weight(1f), triggerHaptic) {
                            Toast.makeText(context, "Scanning Network...", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ServiceCard("Vault Pro", Icons.Default.EnhancedEncryption, Modifier.weight(1f), triggerHaptic) {
                            Toast.makeText(context, "Vault Secured", Toast.LENGTH_SHORT).show()
                        }
                        ServiceCard("Log Wipe", Icons.Default.CleaningServices, Modifier.weight(1f), triggerHaptic) {
                            Toast.makeText(context, "Logs Purged", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            item {
                Text("Protection Logs", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = if(isDarkTheme) Color.White else Color.Black)
            }

            item {
                LogItem("Encrypted Traffic", "AES-256 Enabled", Icons.Default.Security, Color(0xFF4CAF50))
                Spacer(Modifier.height(12.dp))
                LogItem("Anti-Tamper", "Shielding Memory", Icons.Default.RemoveModerator, PrimaryBlue)
            }
        }
    }
}

@Composable
fun SecurityStatusCard(isSecure: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSecure) PrimaryBlue else Color(0xFFC62828)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.1f),
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 40.dp, y = 40.dp)
            )

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.CenterStart)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (isSecure) " PROTECTED " else " DANGER ",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isSecure) "System is\nSecured" else "Security\nBreached!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 34.sp
                )
            }
        }
    }
}

@Composable
fun ServiceCard(title: String, icon: ImageVector, modifier: Modifier, onHaptic: () -> Unit, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // ক্লিক করলে বাটনটি ডেবে যাবে (Visual Feedback)
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, label = "scale")

    Surface(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .height(115.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = { onHaptic(); onClick() }
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isPressed) 1.dp else 4.dp
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = PrimaryBlue.copy(alpha = 0.1f),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun LogItem(title: String, status: String, icon: ImageVector, color: Color) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
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