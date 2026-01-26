package com.imtbytes.samazarqa.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imtbytes.samazarqa.ui.theme.*

@Composable
fun HomeScreen(isDarkTheme: Boolean, onThemeToggle: () -> Unit, isSecure: Boolean) {
    val context = LocalContext.current

    Scaffold(
        // ফিক্সড: Figma স্টাইল ব্যাকগ্রাউন্ড
        containerColor = if(isDarkTheme) Color(0xFF121212) else Color(0xFFF8F9FA),
        bottomBar = {
            // ফিউচার আপডেটে এখানে বটম নেভিগেশন যোগ করা যাবে
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
        ) {
            
            // ১. টপ হেডার
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Welcome Back,",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Text(
                            text = "Admin User",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = if(isDarkTheme) Color.White else Color.Black
                        )
                    }
                    // থিম টগল
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onThemeToggle() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                        }
                    }
                }
            }

            // ২. সার্চ বার
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = if(isDarkTheme) Color(0xFF1E1E1E) else Color.White, // ফিক্সড: Color.White
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Search security tools...", color = Color.Gray)
                    }
                }
            }

            // ৩. মেইন ব্যানার (SecurityStatusCard)
            item {
                SecurityStatusCard(isSecure)
            }

            // ৪. ক্যাটাগরি সেকশন
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Security Suite",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if(isDarkTheme) Color.White else Color.Black
                    )
                    Text(
                        "See All",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { }
                    )
                }
            }

            // ৫. গ্রিড আইটেম
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ServiceCard("App Lock", Icons.Default.Apps, Modifier.weight(1f))
                        ServiceCard("Network", Icons.Default.Wifi, Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ServiceCard("Vault", Icons.Default.FolderZip, Modifier.weight(1f))
                        ServiceCard("Scanner", Icons.Default.QrCodeScanner, Modifier.weight(1f))
                    }
                }
            }
            
            // ৬. রিসেন্ট অ্যাক্টিভিটি
            item {
                Text(
                    "Recent Activity",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 10.dp),
                    color = if(isDarkTheme) Color.White else Color.Black
                )
            }
            
            item {
                RecentActivityItem("System Scan", "Completed 2m ago", Icons.Default.CheckCircle, Color(0xFF4CAF50))
                Spacer(modifier = Modifier.height(10.dp))
                RecentActivityItem("Threat Detected", "Blocked 1h ago", Icons.Default.Warning, Color(0xFFFF5252))
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
            // ব্যাকগ্রাউন্ড ডেকোরেশন
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.1f), // ফিক্সড: Color.White
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
                    color = Color.White.copy(alpha = 0.2f) // ফিক্সড: Color.White
                ) {
                    Text(
                        text = if (isSecure) " PROTECTED " else " DANGER ",
                        color = Color.White, // ফিক্সড: Color.White
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isSecure) "System is\nSecured" else "Security\nBreached!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold, // ফিক্সড: FontWeight.White ছিল না, Bold হবে
                    color = Color.White, // ফিক্সড: Color.White
                    lineHeight = 34.sp
                )
            }
        }
    }
}

@Composable
fun ServiceCard(title: String, icon: ImageVector, modifier: Modifier) {
    Surface(
        modifier = modifier
            .height(110.dp)
            .clickable { },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
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
fun RecentActivityItem(title: String, subtitle: String, icon: ImageVector, iconColor: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = iconColor)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}