package com.imtbytes.samazarqa.screens.home

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.*
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

    // হ্যাপটিক ফিডব্যাক ফাংশন (ক্লিক করলে ফোন ভাইব্রেট হবে)
    val triggerHaptic = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    Scaffold(
        containerColor = if (isDarkTheme) Color(0xFF101010) else Color(0xFFFBFBFE),
        topBar = {
            LargeTopAppBar(
                title = { 
                    Column {
                        Text("SAMAZARQA", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text("Security Hub", fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { triggerHaptic(); onThemeToggle() },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(0.4f), CircleShape)
                    ) {
                        Icon(if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, null, tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            
            // ১. সিকিউরিটি স্ট্যাটাস কার্ড (Animated Pulse)
            item { SecurityStatusCard(isSecure) }

            // ২. গ্রিড টুলস (Figma Home Service Style)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Security Tools", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ActionCard("System Lock", Icons.Default.VerifiedUser, Modifier.weight(1f), triggerHaptic)
                        ActionCard("Network Scan", Icons.Default.Radar, Modifier.weight(1f), triggerHaptic)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ActionCard("Vault Pro", Icons.Default.VpnKey, Modifier.weight(1f), triggerHaptic)
                        ActionCard("Hardening", Icons.Default.ShieldMoon, Modifier.weight(1f), triggerHaptic)
                    }
                }
            }

            // ৩. অ্যাক্টিভিটি লগস
            item {
                Text("Live Protection Logs", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Spacer(Modifier.height(8.dp))
                LogItem("Encrypted Traffic", "Active Tunneling", Icons.Default.SecurityUpdateGood, Color(0xFF4CAF50))
                Spacer(Modifier.height(12.dp))
                LogItem("Anti-Tamper", "Shield Active", Icons.Default.AdminPanelSettings, PrimaryBlue)
            }
        }
    }
}

@Composable
fun SecurityStatusCard(isSecure: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse), label = "pulse"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSecure) PrimaryBlue else Color(0xFFB71C1C)),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            Icon(
                Icons.Default.Security, null, 
                modifier = Modifier.size(180.dp).align(Alignment.CenterEnd).offset(x = 40.dp).graphicsLayer(alpha = 0.1f),
                tint = Color.White
            )
            Column(Modifier.padding(24.dp).align(Alignment.CenterStart)) {
                Surface(color = Color.White.copy(0.2f), shape = RoundedCornerShape(8.dp)) {
                    Text(" LIVE PROTECTION ", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text(if (isSecure) "System Secured" else "Security Risk", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
                Text("All protocols are active and encrypted", color = Color.White.copy(0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ActionCard(title: String, icon: ImageVector, modifier: Modifier, onAction: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(if (isPressed) 0.92f else 1f, label = "scale")

    Surface(
        modifier = modifier
            .graphicsLayer(scaleX = animatedScale, scaleY = animatedScale)
            .height(120.dp)
            .clickable(interactionSource = interactionSource, indication = LocalIndication.current) { onAction() },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isPressed) 2.dp else 6.dp
    ) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(48.dp).background(PrimaryBlue.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
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
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(status, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(Modifier.weight(1f))
            Box(Modifier.size(8.dp).background(color, CircleShape))
        }
    }
}