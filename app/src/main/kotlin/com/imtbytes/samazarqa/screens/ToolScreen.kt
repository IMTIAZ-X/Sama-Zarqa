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

// 1. Accessibility Service Logic
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

// 2. Main Navigation Wrapper
@Composable
fun ToolScreen(isDarkTheme: Boolean) {
    val isDark = isSystemInDarkTheme() || isDarkTheme
    var currentSubScreen by remember { mutableStateOf("dashboard") }

    val bgGradient = if (isDark) Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF020617))) 
                     else Brush.verticalGradient(listOf(Color(0xFFF8FAFC), Color(0xFFCBD5E1)))

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        AnimatedContent(
            targetState = currentSubScreen,
            transitionSpec = { slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut() },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                "dashboard" -> DashboardContent(isDark) { currentSubScreen = it }
                "otp_typer" -> TypeScreenUI(isDark) { currentSubScreen = "dashboard" }
            }
        }
    }
}

// 3. Dashboard UI
@Composable
fun DashboardContent(isDark: Boolean, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Sama-Zarqa", color = if (isDark) Color.White else Color.Black, fontSize = 32.sp, fontWeight = FontWeight.Black)
        Text("Ultimate Tools Engine", color = Color.Gray, fontSize = 14.sp)
        
        Spacer(modifier = Modifier.height(32.dp))

        ToolCard("OTP Force Injector", "Auto injection for bypass boxes", Icons.Default.Bolt, Color(0xFF3D5AFE), isDark) { 
            onNavigate("otp_typer") 
        }
        ToolCard("Engine Speed Tester", "Test injection response time", Icons.Default.Speed, Color(0xFF00E676), isDark) { 
            Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show() 
        }
        ToolCard("Internet PIN Tester", "Network protocol latency test", Icons.Default.NetworkCheck, Color(0xFFFF9100), isDark) { 
            Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show() 
        }
    }
}

@Composable
fun ToolCard(title: String, desc: String, icon: ImageVector, color: Color, isDark: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if(isDark) Color.White.copy(0.05f) else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.15f)) {
                Icon(icon, null, tint = color, modifier = Modifier.padding(10.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = if(isDark) Color.White else Color.Black)
                Text(desc, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

// 4. OTP Typer Screen UI
@Composable
fun TypeScreenUI(isDark: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    val isEnabled = remember { mutableStateOf(checkAccess(context)) }
    val statusColor by animateColorAsState(if (OTPForceService.isRunning) Color(0xFF00E676) else Color(0xFFFF5252), label = "status")

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = if(isDark) Color.White else Color.Black) }
        
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Bypass Engine", modifier = Modifier.weight(1f), color = if (isDark) Color.White else Color.Black, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Surface(shape = RoundedCornerShape(50), color = statusColor.copy(alpha = 0.15f), border = BorderStroke(1.dp, statusColor)) {
                Text(if (OTPForceService.isRunning) "RUNNING" else "STANDBY", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), color = statusColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!isEnabled.value) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth().clickable { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
            ) {
                Text("Service is OFF - Click to Enable", modifier = Modifier.padding(16.dp), color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(colors = CardDefaults.cardColors(containerColor = if (isDark) Color.White.copy(0.05f) else Color.White)) {
            Column(modifier = Modifier.padding(20.dp)) {
                AdvancedOtpPreview()
                Spacer(modifier = Modifier.height(24.dp))
                CustomSlider("Box Limit", OTPForceService.boxCount.toFloat(), 4f..10f) { OTPForceService.boxCount = it.toInt() }
                CustomSlider("Intensity", OTPForceService.retryCount.toFloat(), 1f..10f) { OTPForceService.retryCount = it.toInt() }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { 
                if (checkAccess(context)) OTPForceService.isRunning = !OTPForceService.isRunning 
                else context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (OTPForceService.isRunning) Color(0xFFFF5252) else Color(0xFF3D5AFE))
        ) {
            Icon(if (OTPForceService.isRunning) Icons.Default.Stop else Icons.Default.Bolt, null)
            Spacer(Modifier.width(8.dp))
            Text(if (OTPForceService.isRunning) "STOP ENGINE" else "START INJECTION", fontWeight = FontWeight.ExtraBold)
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(OTPForceService.boxCount) { index ->
                    val char = OTPForceService.otpToPush.getOrNull(index)?.toString() ?: ""
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f).border(1.dp, if (char.isNotEmpty()) Color.Cyan else Color.DarkGray, RoundedCornerShape(8.dp)).background(Color.White.copy(0.05f)), contentAlignment = Alignment.Center) {
                        Text(char, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}

@Composable
fun CustomSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.Gray, fontSize = 13.sp)
            Text("${value.toInt()}", color = Color.Cyan, fontWeight = FontWeight.Bold)
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = range, steps = (range.endInclusive - range.start).toInt() - 1)
    }
}

fun checkAccess(context: Context): Boolean {
    val service = ComponentName(context, OTPForceService::class.java).flattenToString()
    val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    return enabledServices?.contains(service) == true
}