package com.imtbytes.samazarqa.screens

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat

// -------------------------------------------------------------------------
// 1. Logic Engine: Accessibility Service (Keep Original)
// -------------------------------------------------------------------------

class OTPForceService : AccessibilityService() {
    companion object {
        var isRunning by mutableStateOf(false)
        var otpToPush by mutableStateOf("")
        var retryCount by mutableIntStateOf(1)
        var boxCount by mutableIntStateOf(4)
        private const val CHANNEL_ID = "bypass_engine_channel"
        private const val NOTIF_ID = 1
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isRunning || otpToPush.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
            return
        }
        val rootNode = rootInActiveWindow ?: return
        updateNotification("Engine Scanning: ${event.packageName}")
        findAndForceInject(rootNode)
    }

    private fun findAndForceInject(node: AccessibilityNodeInfo?) {
        if (node == null) return
        if (node.isEditable || node.className?.contains("EditText", true) == true) {
            val payload = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, otpToPush)
            }
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
            repeat(retryCount) { node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, payload) }
            Log.d("ForceEngine", "Payload Forced: $otpToPush")
        }
        for (i in 0 until node.childCount) findAndForceInject(node.getChild(i))
    }

    private fun updateNotification(content: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Bypass Running", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sama-Zarqa Active")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
        startForeground(NOTIF_ID, notification)
    }

    override fun onInterrupt() { isRunning = false }
}

// -------------------------------------------------------------------------
// 2. Main Dashboard UI (New ToolScreen)
// -------------------------------------------------------------------------

@Composable
fun ToolScreen(isDarkTheme: Boolean) {
    val context = LocalContext.current
    var currentSubScreen by remember { mutableStateOf("dashboard") }
    val isDark = isSystemInDarkTheme() || isDarkTheme
    
    val bgGradient = if (isDark) Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF020617))) 
                     else Brush.verticalGradient(listOf(Color(0xFFF8FAFC), Color(0xFFCBD5E1)))

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        AnimatedContent(
            targetState = currentSubScreen,
            transitionSpec = { slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut() }
        ) { screen ->
            when (screen) {
                "dashboard" -> DashboardContent(isDark) { currentSubScreen = it }
                "otp_typer" -> TypeScreen(isDark) { currentSubScreen = "dashboard" }
            }
        }
    }
}

@Composable
fun DashboardContent(isDark: Boolean, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Sama-Zarqa", color = if (isDark) Color.White else Color.Black, fontSize = 32.sp, fontWeight = FontWeight.Black)
        Text("Ultimate Pentesting Tools", color = Color.Gray, fontSize = 14.sp)
        
        Spacer(modifier = Modifier.height(32.dp))

        // Card 1: OTP Typer
        ToolCard(
            title = "OTP Force Injector",
            desc = "Auto injection for bypass boxes",
            icon = Icons.Default.Bolt,
            color = Color(0xFF3D5AFE),
            isDark = isDark
        ) { onNavigate("otp_typer") }

        // Card 2: Speed Tester
        ToolCard(
            title = "Engine Speed Tester",
            desc = "Test injection response time",
            icon = Icons.Default.Speed,
            color = Color(0xFF00E676),
            isDark = isDark
        ) { Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show() }

        // Card 3: Pin Tester
        ToolCard(
            title = "Internet PIN Tester",
            desc = "Network protocol latency test",
            icon = Icons.Default.NetworkCheck,
            color = Color(0xFFFF9100),
            isDark = isDark
        ) { Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show() }
    }
}

@Composable
fun ToolCard(title: String, desc: String, icon: ImageVector, color: Color, isDark: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if(isDark) Color.White.copy(0.05f) else Color.White),
        border = BorderStroke(1.dp, if(isDark) Color.White.copy(0.1f) else Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(50.dp), shape = RoundedCornerShape(14.dp), color = color.copy(alpha = 0.2f)) {
                Icon(icon, null, tint = color, modifier = Modifier.padding(12.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if(isDark) Color.White else Color.Black)
                Text(desc, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

// -------------------------------------------------------------------------
// 3. Original OTP Typer UI (Inside TypeScreen)
// -------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeScreen(isDark: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    val isEnabled = remember { mutableStateOf(checkAccess(context)) }

    DisposableEffect(Unit) {
        isEnabled.value = checkAccess(context)
        onDispose {}
    }

    val statusColor by animateColorAsState(if (OTPForceService.isRunning) Color(0xFF00E676) else Color(0xFFFF5252))

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = if(isDark) Color.White else Color.Black) }
        
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Bypass Engine", color = if (isDark) Color.White else Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Black)
            }
            Surface(shape = RoundedCornerShape(50), color = statusColor.copy(alpha = 0.15f), border = BorderStroke(1.dp, statusColor)) {
                Text(if (OTPForceService.isRunning) "RUNNING" else "STANDBY", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = statusColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (!isEnabled.value) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.1f)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFFF5252)),
                modifier = Modifier.fillMaxWidth().clickable {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = Color(0xFFFF5252))
                    Spacer(Modifier.width(12.dp))
                    Text("Service is OFF\nClick to Enable", color = if(isDark) Color.White else Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color.White.copy(0.05f) else Color.White),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("CONTROL PARAMETERS", color = Color.Cyan, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(24.dp))
                AdvancedOtpPreview()
                Spacer(modifier = Modifier.height(30.dp))
                CustomSlider("Box Limit", OTPForceService.boxCount.toFloat(), 4f..10f) { OTPForceService.boxCount = it.toInt() }
                CustomSlider("Intensity", OTPForceService.retryCount.toFloat(), 1f..10f) { OTPForceService.retryCount = it.toInt() }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { 
                if (checkAccess(context)) OTPForceService.isRunning = !OTPForceService.isRunning 
                else context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (OTPForceService.isRunning) Color(0xFFFF5252) else Color(0xFF3D5AFE))
        ) {
            Icon(if (OTPForceService.isRunning) Icons.Default.Stop else Icons.Default.Bolt, null)
            Spacer(Modifier.width(8.dp))
            Text(if (OTPForceService.isRunning) "STOP ENGINE" else "START INJECTION", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdvancedOtpPreview() {
    BasicTextField(
        value = OTPForceService.otpToPush,
        onValueChange = { if (it.length <= OTPForceService.boxCount) OTPForceService.otpToPush = it },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                repeat(OTPForceService.boxCount) { index ->
                    val char = OTPForceService.otpToPush.getOrNull(index)?.toString() ?: ""
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f).border(1.dp, if (char.isNotEmpty()) Color.Cyan else Color.DarkGray, RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.05f)), contentAlignment = Alignment.Center) {
                        Text(char, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}

@Composable
fun CustomSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.LightGray, fontSize = 14.sp)
            Text("${value.toInt()}", color = Color.Cyan, fontWeight = FontWeight.Bold)
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = range, steps = (range.endInclusive - range.start).toInt() - 1, colors = SliderDefaults.colors(thumbColor = Color.Cyan, activeTrackColor = Color.Cyan))
    }
}

fun checkAccess(context: Context): Boolean {
    val service = ComponentName(context, OTPForceService::class.java).flattenToString()
    val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    return enabledServices?.contains(service) == true
}