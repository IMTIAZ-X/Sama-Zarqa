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

@Composable
fun SettingScreen(isDarkTheme: Boolean) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Text(
                text = "Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Security Settings Group
        item {
            SettingsGroup(title = "Security & Privacy") {
                SettingItemToggle(
                    title = "Real-time Protection",
                    subtitle = "Monitor threats in background",
                    icon = Icons.Rounded.Shield,
                    initialState = true,
                    onToggle = { /* Handle logic */ }
                )
                SettingItemToggle(
                    title = "Biometric Login",
                    subtitle = "Use Fingerprint or FaceID",
                    icon = Icons.Rounded.Fingerprint,
                    initialState = false,
                    onToggle = { /* Handle logic */ }
                )
                SettingItemToggle(
                    title = "App Lock",
                    subtitle = "Lock sensitive apps",
                    icon = Icons.Rounded.Lock,
                    initialState = true,
                    onToggle = { /* Handle logic */ }
                )
            }
        }

        // General Settings Group
        item {
            SettingsGroup(title = "General") {
                SettingItemToggle(
                    title = "Notifications",
                    subtitle = "Security alerts & updates",
                    icon = Icons.Rounded.Notifications,
                    initialState = true,
                    onToggle = { /* Handle logic */ }
                )
                SettingItemArrow(
                    title = "Language",
                    subtitle = "English (US)",
                    icon = Icons.Rounded.Language,
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
                    onClick = { /* Navigate */ }
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
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
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
    var isChecked by remember { mutableStateOf(initialState) }

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
            if (subtitle != null) {
                Text(
                    text = subtitle,
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
            tint = MaterialTheme.colorScheme.onSurface,
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
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}