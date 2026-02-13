package com.imtbytes.samazarqa.screens

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat

// --- 1. POWERFUL ACCESSIBILITY ENGINE ---
class OTPForceService : AccessibilityService() {
    companion object {
        var isRunning by mutableStateOf(false)
        var otpToPush by mutableStateOf("")
        var retryCount by mutableIntStateOf(1)
        var boxCount by mutableIntStateOf(4)
        var injectionDelay by mutableLongStateOf(30L)
        var isBoxMode by mutableStateOf(true) // Switch between Box vs Text Editor
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isRunning || otpToPush.isEmpty()) return
        val rootNode = rootInActiveWindow ?: return
        
        // এগ্রেসিভ স্ক্যানিং লজিক
        recursiveScan(rootNode)
    }

    private fun recursiveScan(node: AccessibilityNodeInfo?) {
        if (node == null) return

        if (node.isEditable || node.className?.contains("EditText", true) == true) {
            executeInjection(node)
        }

        for (i in 0 until node.childCount) {
            recursiveScan(node.getChild(i))
        }
    }

    private fun executeInjection(node: AccessibilityNodeInfo) {
        val payload = Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, otpToPush)
        }

        node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
        
        repeat(retryCount) {
            android.os.SystemClock.sleep(injectionDelay)
            
            // ১. আধুনিক ইনজেকশন (Direct Set)
            val success = node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, payload)
            
            // ২. যদি বক্স লক থাকে, তবে পেস্ট বাইপাস ব্যবহার করবে
            if (!success) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("otp", otpToPush))
                node.performAction(AccessibilityNodeInfo.ACTION_PASTE)
            }
        }
    }

    override fun onInterrupt() { isRunning = false }
}

// --- 2. MODERN UI COMPONENTS ---

@Composable
fun ToolScreen() {
    var currentScreen by remember { mutableStateOf("dashboard") }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AnimatedContent(targetState = currentScreen, label = "") { state ->
            when (state) {
                "dashboard" -> DashboardContent { currentScreen = it }
                "typer" -> TypeScreenUI { currentScreen = "dashboard" }
            }
        }
    }
}

@Composable
fun DashboardContent(onNavigate: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Sama-Zarqa", fontSize = 32.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
        Text("Next-Gen Utility Engine", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(40.dp))

        ModernToolCard("Injection Engine", "Advanced OTP & Auto-Typer", Icons.Rounded.FlashOn, Color(0xFF6C63FF)) { onNavigate("typer") }
        ModernToolCard("Network Ping", "Protocol Latency Tester", Icons.Rounded.WifiTethering, Color(0xFF00D2FF)) { }
        ModernToolCard("Deep Scanner", "Accessibility Node Inspector", Icons.Rounded.Troubleshoot, Color(0xFFF9D423)) { }
    }
}

@Composable
fun TypeScreenUI(onBack: () -> Unit) {
    val context = LocalContext.current
    val statusColor by animateColorAsState(if (OTPForceService.isRunning) Color(0xFF00E676) else Color(0xFFFF5252), label = "")

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) {
                Icon(Icons.Rounded.ArrowBack, null)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Bypass Engine", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(statusColor, CircleShape))
                    Spacer(Modifier.width(6.dp))
                    Text(if (OTPForceService.isRunning) "RUNNING" else "STANDBY", color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(30.dp))

        // Mode Switcher (Box vs Text Editor)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(Modifier.padding(4.dp)) {
                ModeButton("Box Mode", Icons.Rounded.GridView, OTPForceService.isBoxMode, Modifier.weight(1f)) { OTPForceService.isBoxMode = true }
                ModeButton("Editor Mode", Icons.Rounded.TextFields, !OTPForceService.isBoxMode, Modifier.weight(1f)) { OTPForceService.isBoxMode = false }
            }
        }

        Spacer(Modifier.height(24.dp))

        // OTP Input / Preview Area
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Input Payload", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                
                AdvancedInputSystem() // Dual Mode Input
            }
        }

        Spacer(Modifier.height(24.dp))

        // Controls
        ControlSlider("Injection Speed (ms)", OTPForceService.injectionDelay.toFloat(), 0f..500f) { OTPForceService.injectionDelay = it.toLong() }
        ControlSlider("Retry Intensity", OTPForceService.retryCount.toFloat(), 1f..15f) { OTPForceService.retryCount = it.toInt() }
        
        if (OTPForceService.isBoxMode) {
            ControlSlider("Box Count", OTPForceService.boxCount.toFloat(), 4f..8f) { OTPForceService.boxCount = it.toInt() }
        }

        Spacer(Modifier.height(40.dp))

        // Action Button
        Button(
            onClick = { 
                if (checkAccess(context)) OTPForceService.isRunning = !OTPForceService.isRunning 
                else context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            },
            modifier = Modifier.fillMaxWidth().height(65.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (OTPForceService.isRunning) Color(0xFFFF5252) else Color(0xFF6C63FF))
        ) {
            Icon(if (OTPForceService.isRunning) Icons.Rounded.StopCircle else Icons.Rounded.PlayArrow, null)
            Spacer(Modifier.width(8.dp))
            Text(if (OTPForceService.isRunning) "TERMINATE ENGINE" else "START INJECTION", fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun AdvancedInputSystem() {
    BasicTextField(
        value = OTPForceService.otpToPush,
        onValueChange = { 
            if (OTPForceService.isBoxMode) {
                if (it.length <= OTPForceService.boxCount) OTPForceService.otpToPush = it
            } else {
                OTPForceService.otpToPush = it
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = if(OTPForceService.isBoxMode) KeyboardType.Number else KeyboardType.Text),
        textStyle = TextStyle(textAlign = TextAlign.Center),
        decorationBox = {
            if (OTPForceService.isBoxMode) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(OTPForceService.boxCount) { i ->
                        val char = OTPForceService.otpToPush.getOrNull(i)?.toString() ?: ""
                        Box(Modifier.weight(1f).aspectRatio(1f).border(2.dp, if(char.isNotEmpty()) Color(0xFF6C63FF) else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f)), contentAlignment = Alignment.Center) {
                            Text(char, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            } else {
                // Telegram Style Single Input Preview
                Box(Modifier.fillMaxWidth().height(56.dp).border(2.dp, Color(0xFF6C63FF).copy(0.5f), RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f)).padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
                    if (OTPForceService.otpToPush.isEmpty()) Text("Type message or OTP...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(OTPForceService.otpToPush, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    )
}

// --- 3. HELPER UI ELEMENTS ---

@Composable
fun ModeButton(label: String, icon: ImageVector, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val bg by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
    val contentColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)

    Surface(
        modifier = modifier.height(45.dp).clickable { onClick() },
        color = bg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = contentColor)
            Spacer(Modifier.width(8.dp))
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = contentColor)
        }
    }
}

@Composable
fun ModernToolCard(title: String, desc: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(52.dp).background(color.copy(0.15f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ControlSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Column(Modifier.padding(vertical = 10.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value.toInt().toString(), fontWeight = FontWeight.Bold, color = Color(0xFF6C63FF))
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = range)
    }
}

fun checkAccess(context: Context): Boolean {
    val service = ComponentName(context, OTPForceService::class.java).flattenToString()
    val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    return enabledServices?.contains(service) == true
}