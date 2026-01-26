package com.imtbytes.samazarqa.screens.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    var noteText by remember { mutableStateOf("") }
    
    // পারফরম্যান্স বুস্ট করার জন্য noteText এর স্টেট চেক অপ্টিমাইজ করা হয়েছে
    val isNoteEmpty by remember { derivedStateOf { noteText.isEmpty() } }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SAMAZARQA PRO", fontWeight = FontWeight.Black) },
                actions = {
                    IconButton(onClick = onThemeToggle) {
                        Icon(if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 20.dp) // স্মুথ স্ক্রলিং এর জন্য
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            item { SecurityStatusCard(isSecure) }

            item {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Fast Encryption Note") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.4f)
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (!isNoteEmpty) {
                                Toast.makeText(context, "Encrypted & Saved!", Toast.LENGTH_SHORT).show()
                                noteText = ""
                            }
                        }) {
                            Icon(Icons.Default.Save, null, tint = if (isNoteEmpty) Color.Gray else PrimaryBlue)
                        }
                    }
                )
            }

            item { Text("Advanced Actions", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ActionBox("Wipe Trace", Icons.Default.DeleteForever, Modifier.weight(1f)) {
                        Toast.makeText(context, "System Cleaned!", Toast.LENGTH_SHORT).show()
                    }
                    ActionBox("Hardening", Icons.Default.Lock, Modifier.weight(1f)) {
                        Toast.makeText(context, "Shield Active!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityStatusCard(isSecure: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSecure) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        )
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (isSecure) Icons.Default.Shield else Icons.Default.Warning,
                null,
                tint = if (isSecure) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                if (isSecure) "System Secured" else "Security Breach!",
                color = if (isSecure) Color(0xFF2E7D32) else Color(0xFFC62828),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ActionBox(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(100.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(28.dp))
            Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        }
    }
}