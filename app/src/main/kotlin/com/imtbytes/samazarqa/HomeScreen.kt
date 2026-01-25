package com.imtbytes.samazarqa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(isDarkTheme: Boolean, onThemeToggle: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("DASHBOARD", fontWeight = FontWeight.Black, fontSize = 18.sp) },
                actions = {
                    IconButton(onClick = onThemeToggle) {
                        Icon(if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            // Welcome Card
            item {
                WelcomeCard()
            }

            // Statistics or Quick Action Grid
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ActionBox("Secure", Icons.Default.VerifiedUser, Modifier.weight(1f))
                    ActionBox("Logs", Icons.Default.History, Modifier.weight(1f))
                }
            }

            item {
                Text("Management", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            // Modern List Items
            items(5) { index ->
                FeatureItem(title = "Security Module 0${index + 1}", icon = Icons.Default.Shield)
            }
        }
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(24.dp)) {
            Text("Welcome Back,", style = MaterialTheme.typography.bodyLarge)
            Text("Administrator", fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = {}, shape = RoundedCornerShape(12.dp)) {
                Text("View System Status")
            }
        }
    }
}

@Composable
fun ActionBox(label: String, icon: ImageVector, modifier: Modifier) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp))
            Text(label, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun FeatureItem(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
    }
}