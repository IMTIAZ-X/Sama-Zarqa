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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat

// --- IMPORT YOUR THEME CLASSES ---
import com.imtbytes.samazarqa.ui.theme.*
import com.imtbytes.samazarqa.data.AppTheme

// -------------------------------------------------------------------------
// 1. Logic Engine: The Most Powerful Accessibility Service
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
            // Android 13+ fix for stopForeground
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
// 2. UI: The Ultra Modern 5K Advance TypeScreen
// -------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeScreen(isDarkTheme: Boolean) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme() || isDarkTheme
    val isEnabled = remember { mutableStateOf(checkAccess(context)) }

    // রিফ্রেশ লজিক: যখন ইউজার সেটিংস থেকে ফিরে আসবে তখন চেক করবে
    DisposableEffect(Unit) {
        isEnabled.value = checkAccess(context)
        onDispose {}
    }

    val statusColor by animateColorAsState(if (OTPForceService.isRunning) Color(0xFF00E676) else Color(0xFFFF5252))
    val bgGradient = if (isDark) Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF020617))) 
                     else Brush.verticalGradient(listOf(Color(0xFFF8FAFC), Color(0xFFCBD5E1)))

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
            
            // --- HEADER ---
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Bypass Engine", color = if (isDark) Color.White else Color.Black, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Text("Advanced OTP Injection Engine", color = Color.Gray, fontSize = 14.sp)
                }
                Surface(shape = RoundedCornerShape(50), color = statusColor.copy(alpha = 0.15f), border = BorderStroke(1.dp, statusColor)) {
                    Text(if (OTPForceService.isRunning) "RUNNING" else "STANDBY", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = statusColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- PERMISSION DIALOG (FIXED) ---
            if (!isEnabled.value) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF5252)),
                    modifier = Modifier.fillMaxWidth().clickable {
                        // সরাসরি আপনার অ্যাপের সেটিংস পেজে নিয়ে যাবে
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    }
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, "Error", tint = Color(0xFFFF5252))
                        Spacer(Modifier.width(12.dp))
                        Text("Sama-Zarqa Service is OFF\nClick to Enable", color = if(isDark) Color.White else Color.Black, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- CONTROL PANEL ---
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color.White.copy(0.05f) else Color.White),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if(isDark) 0.dp else 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("CONTROL PARAMETERS", color = Color.Cyan, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    AdvancedOtpPreview()
                    Spacer(modifier = Modifier.height(30.dp))
                    CustomSlider("Bypass Box Limit", OTPForceService.boxCount.toFloat(), 4f..10f) { OTPForceService.boxCount = it.toInt() }
                    CustomSlider("Injection Intensity", OTPForceService.retryCount.toFloat(), 1f..10f) { OTPForceService.retryCount = it.toInt() }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- MASTER LAUNCH BUTTON ---
            Button(
                onClick = { 
                    if (checkAccess(context)) {
                        OTPForceService.isRunning = !OTPForceService.isRunning 
                    } else {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(70.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (OTPForceService.isRunning) Color(0xFFFF5252) else Color(0xFF3D5AFE))
            ) {
                Icon(if (OTPForceService.isRunning) Icons.Default.Stop else Icons.Default.Bolt, null, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Text(if (OTPForceService.isRunning) "TERMINATE SERVICE" else "INITIALIZE BYPASS", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

// ... (AdvancedOtpPreview এবং CustomSlider আগের মতোই থাকবে) ...

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

// --- POWERFUL PERMISSION CHECKER (FIXED) ---
fun checkAccess(context: Context): Boolean {
    val service = ComponentName(context, OTPForceService::class.java).flattenToString()
    val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    return enabledServices?.contains(service) == true
}