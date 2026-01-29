package com.imtbytes.samazarqa.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imtbytes.samazarqa.ui.theme.PrimaryBlue

/**
 * Samazarqa Settings Screen
 * সংশোধিত: onThemeToggle প্যারামিটার যোগ করা হয়েছে যাতে Dark Mode কাজ করে।
 */
@Composable
fun SettingScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header Section
        item {
            Text(
                text = "Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Appearance Group
        item {
            SettingsGroup(title = "Appearance") {
                SettingItemToggle(
                    title = "Dark Mode",
                    subtitle = "Adjust app visual experience",
                    icon = if (isDarkTheme) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                    initialState = isDarkTheme,
                    onToggle = { onThemeToggle() }
                )
            }
        }

        // Security Group
        item {
            SettingsGroup(title = "Security & Privacy") {
                SettingItemToggle(
                    title = "Real-time Protection",
                    subtitle = "Monitor threats in background",
                    icon = Icons.Rounded.Shield,
                    initialState = true,
                    onToggle = { /* Handle logic here */ }
                )
                SettingItemToggle(
                    title = "Biometric Login",
                    subtitle = "Use Fingerprint or FaceID",
                    icon = Icons.Rounded.Fingerprint,
                    initialState = false,
                    onToggle = { /* Handle logic here */ }
                )
            }
        }

        // General Group
        item {
            SettingsGroup(title = "General") {
                SettingItemArrow(
                    title = "Language",
                    subtitle = "English (US)",
                    icon = Icons.Rounded.Language,
                    onClick = { /* Navigate to Language Screen */ }
                )
                SettingItemArrow(
                    title = "Notifications",
                    subtitle = "Manage alerts",
                    icon = Icons.Rounded.Notifications,
                    onClick = { /* Navigate */ }
                )
            }
        }

        // Support Group
        item {
            SettingsGroup(title = "Support") {
                SettingItemArrow(
                    title = "Privacy Policy",
                    subtitle = null,
                    icon = Icons.Rounded.PrivacyTip,
                    onClick = { /* Open URL */ }
                )
                SettingItemArrow(
                    title = "About Samazarqa",
                    subtitle = "v1.0.0 (Production)",
                    icon = Icons.Rounded.Info,
                    onClick = { /* Navigate */ }
                )
            }
        }
    }
}

// --- Helper UI Components ---

@Composable
fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingItemToggle(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    initialState: Boolean,
    onToggle: (Boolean) -> Unit
) {
    var isChecked by remember(initialState) { mutableStateOf(initialState) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                isChecked = !isChecked
                onToggle(isChecked)
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                onToggle(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryBlue
            )
        )
    }
}

@Composable
fun SettingItemArrow(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}