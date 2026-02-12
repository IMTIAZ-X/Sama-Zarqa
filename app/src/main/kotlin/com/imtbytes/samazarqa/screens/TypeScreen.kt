package com.imtbytes.samazarqa.screens

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Stop
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
import com.imtbytes.samazarqa.ui.theme.*

// -------------------------------------------------------------------------
// 1. Logic Engine: The Accessibility Service
// -------------------------------------------------------------------------

class OTPForceService : AccessibilityService() {

    companion object {
        var isRunning by mutableStateOf(false)
        var otpToPush by mutableStateOf("")
        var retryCount by mutableIntStateOf(1)
        var boxCount by mutableIntStateOf(4)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!isRunning || otpToPush.isEmpty()) return

        // যখনই উইন্ডোর কন্টেন্ট পরিবর্তন হবে (যেমন নতুন অ্যাপ ওপেন হওয়া)
        val rootNode = rootInActiveWindow ?: return
        
        // ডিপ ইনজেকশন লজিক শুরু
        findAndForceInject(rootNode)
    }

    private fun findAndForceInject(node: AccessibilityNodeInfo) {
        // যদি এটি এডিটেবল ফিল্ড হয় (EditText)
        if (node.isEditable || node.className?.contains("EditText") == true) {
            
            val payload = Bundle().apply {
                putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, 
                    otpToPush
                )
            }

            // পাওয়ারফুল কমান্ড: আগে ফোকাস করো, তারপর ডেটা ইনজেক্ট করো
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
            
            repeat(retryCount) {
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, payload)
            }
            Log.d("ForceEngine", "Payload Injected: $otpToPush")
        }

        // রিকার্সিভলি সব সাব-ভিউ চেক করা
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { findAndForceInject(it) }
        }
    }

    override fun onInterrupt() {
        isRunning = false
    }
}

// -------------------------------------------------------------------------
// 2. Modern UI: The TypeScreen Dashboard
// -------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeScreen() {
    val statusColor by animateColorAsState(
        if (OTPForceService.isRunning) Color(0xFF00E676) else Color(0xFFFF5252),
        label = "statusAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))))
            .padding(24.dp)
    ) {
        // Header Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "System Bypass",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Advanced OTP Injection Engine",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            
            // Status Indicator Badge
            Surface(
                shape = RoundedCornerShape(50),
                color = statusColor.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor)
            ) {
                Text(
                    text = if (OTPForceService.isRunning) "RUNNING" else "STANDBY",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Configuration Panel
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "CONTROL PARAMETERS",
                    color = Color.Cyan,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                // OTP Preview Input
                OtpPreviewField(
                    value = OTPForceService.otpToPush,
                    count = OTPForceService.boxCount
                ) {
                    OTPForceService.otpToPush = it
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Slider: Logic Boxes
                CustomControlSlider(
                    label = "Bypass Box Limit",
                    value = OTPForceService.boxCount.toFloat(),
                    range = 4f..10f
                ) {
                    OTPForceService.boxCount = it.toInt()
                }

                // Slider: Force Retries
                CustomControlSlider(
                    label = "Injection Retries",
                    value = OTPForceService.retryCount.toFloat(),
                    range = 1f..10f
                ) {
                    OTPForceService.retryCount = it.toInt()
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Master Launch Button
        Button(
            onClick = { OTPForceService.isRunning = !OTPForceService.isRunning },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (OTPForceService.isRunning) Color(0xFFFF5252) else Color(0xFF3D5AFE)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Icon(
                imageVector = if (OTPForceService.isRunning) Icons.Default.Stop else Icons.Default.Bolt,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = if (OTPForceService.isRunning) "TERMINATE SERVICE" else "INITIALIZE BYPASS",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Ensure Accessibility Permission is GRANTED",
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = Color.Gray,
            fontSize = 11.sp
        )
    }
}

@Composable
fun OtpPreviewField(value: String, count: Int, onValueChange: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = { if (it.length <= count) onValueChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                repeat(count) { index ->
                    val char = value.getOrNull(index)?.toString() ?: ""
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.9f)
                            .border(1.dp, if (char.isNotEmpty()) Color.Cyan else Color.DarkGray, RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(char, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}

@Composable
fun CustomControlSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.LightGray, fontSize = 14.sp)
            Text("${value.toInt()}", color = Color.Cyan, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = (range.endInclusive - range.start).toInt() - 1,
            colors = SliderDefaults.colors(
                thumbColor = Color.Cyan,
                activeTrackColor = Color.Cyan,
                inactiveTrackColor = Color.DarkGray
            )
        )
    }
}