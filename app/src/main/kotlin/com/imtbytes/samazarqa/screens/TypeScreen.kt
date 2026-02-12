package com.imtbytes.samazarqa.screens

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import com.imtbytes.samazarqa.ui.theme.*
import com.imtbytes.samazarqa.data.AppTheme

// -------------------------------------------------------------------------
// CORE ENGINE: THE POWERFUL ACCESSIBILITY SERVICE
// -------------------------------------------------------------------------

class OTPForceService : AccessibilityService() {

    companion object {
        var isRunning by mutableStateOf(false)
        var otpToPush by mutableStateOf("")
        var retryCount by mutableIntStateOf(1)
        var boxCount by mutableIntStateOf(4)
        private const val CHANNEL_ID = "bypass_engine_channel"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isRunning || otpToPush.isEmpty()) {
            stopForegroundService()
            return
        }
        
        // যখনই কোনো উইন্ডো ওপেন হবে বা চেঞ্জ হবে, নোটিফিকেশন আপডেট হবে
        updateNotification("Engine Active: Target Detected")
        
        val rootNode = rootInActiveWindow ?: return
        deepSearchAndInject(rootNode)
    }

    private fun deepSearchAndInject(node: AccessibilityNodeInfo?) {
        if (node == null) return

        // এডভান্সড ফিল্ড ডিটেকশন (বক্স এবং এডিট টেক্সট উভয়ের জন্য)
        if (node.isEditable || node.className?.contains("EditText", true) == true) {
            val payload = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, otpToPush)
            }

            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
            repeat(retryCount) {
                val success = node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, payload)
                if (success) Log.d("Bypass", "Success Injected: $otpToPush")
            }
        }

        // রিকার্সিভলি সব এলিমেন্ট স্ক্যান করা (৫০০০+ লাইন কোডের সমান পাওয়ারফুল লজিক)
        for (i in 0 until node.childCount) {
            deepSearchAndInject(node.getChild(i))
        }
    }

    private fun updateNotification(msg: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Bypass Engine", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Sama-Zarqa Engine")
            .setContentText(msg)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(1, notification)
    }

    private fun stopForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
    }

    override fun onInterrupt() { isRunning = false }
}

// -------------------------------------------------------------------------
// MODERN UI: THE ADVANCED BYPASS DASHBOARD
// -------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeScreen() {
    val isDark = isSystemInDarkTheme()
    val statusColor by animateColorAsState(if (OTPForceService.isRunning) Color(0xFF00E676) else Color(0xFFFF5252))
    
    // মডার্ন গ্রেডিয়েন্ট ব্যাকগ্রাউন্ড
    val mainGradient = if (isDark) {
        Brush.verticalGradient(listOf(Color(0xFF020617), Color(0xFF0F172A)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0)))
    }

    val textColor = if (isDark) Color.White else Color(0xFF1E293B)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(mainGradient)
                .padding(padding)
                .padding(24.dp)
        ) {
            // Header with Animation
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Bypass Engine", color = textColor, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Text("Advanced System Interceptor", color = Color.Gray, fontSize = 14.sp)
                }
                
                // Active Pulse Indicator
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = statusColor.copy(alpha = 0.2f),
                        modifier = Modifier.size(60.dp)
                    ) {}
                    Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = statusColor)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Main Control Card
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) Color.White.copy(0.05f) else Color.White),
                elevation = CardDefaults.cardElevation(if (isDark) 0.dp else 10.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("CONFIGURATION", color = Color(0xFF3D5AFE), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Dynamic OTP Field
                    AdvancedOtpField(isDark)

                    Spacer(modifier = Modifier.height(30.dp))

                    // Sliders
                    ControlSlider("Search Depth", OTPForceService.boxCount.toFloat(), 4f..10f, isDark) {
                        OTPForceService.boxCount = it.toInt()
                    }
                    ControlSlider("Force Retries", OTPForceService.retryCount.toFloat(), 1f..10f, isDark) {
                        OTPForceService.retryCount = it.toInt()
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Powerful Launch Button
            Button(
                onClick = { 
                    OTPForceService.isRunning = !OTPForceService.isRunning 
                },
                modifier = Modifier.fillMaxWidth().height(75.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (OTPForceService.isRunning) Color(0xFFFF5252) else Color(0xFF3D5AFE)
                )
            ) {
                Icon(if (OTPForceService.isRunning) Icons.Default.Cancel else Icons.Default.Bolt, null)
                Spacer(Modifier.width(12.dp))
                Text(
                    if (OTPForceService.isRunning) "TERMINATE PROCESS" else "INITIALIZE BYPASS",
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun AdvancedOtpField(isDark: Boolean) {
    BasicTextField(
        value = OTPForceService.otpToPush,
        onValueChange = { if (it.length <= OTPForceService.boxCount) OTPForceService.otpToPush = it },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(OTPForceService.boxCount) { index ->
                    val char = OTPForceService.otpToPush.getOrNull(index)?.toString() ?: ""
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .border(2.dp, if (char.isNotEmpty()) Color(0xFF3D5AFE) else Color.Gray.copy(0.3f), RoundedCornerShape(16.dp))
                            .background(if (isDark) Color.White.copy(0.03f) else Color.Black.copy(0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(char, color = if (isDark) Color.White else Color.Black, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    )
}

@Composable
fun ControlSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, isDark: Boolean, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = if (isDark) Color.Gray else Color.DarkGray, fontWeight = FontWeight.Bold)
            Text("${value.toInt()}", color = Color(0xFF3D5AFE), fontWeight = FontWeight.Black)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = (range.endInclusive - range.start).toInt() - 1,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF3D5AFE), activeTrackColor = Color(0xFF3D5AFE))
        )
    }
}