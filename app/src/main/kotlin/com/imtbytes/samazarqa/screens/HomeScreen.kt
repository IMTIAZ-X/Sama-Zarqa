package com.imtbytes.samazarqa.screens

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imtbytes.samazarqa.ui.theme.*
import com.imtbytes.samazarqa.screens.*
import kotlinx.coroutines.launch

// Navigation Items Enum
enum class NavItem(val icon: ImageVector, val label: String) {
    Home(Icons.Default.Home, "Home"),
    QR(Icons.Default.QrCodeScanner, "QR"),
    Downloader(Icons.Default.Download, "Download"),
    Profile(Icons.Default.Person, "Profile"),
    Settings(Icons.Default.Settings, "Setting")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    isSecure: Boolean
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // State for selected Navigation Item
    var selectedItem by remember { mutableStateOf(NavItem.Home) }

    // Haptic feedback helper
    val triggerHaptic: () -> Unit = {
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(35)
                }
            }
        } catch (e: Exception) { /* Ignore */ }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFF323232),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = if (isDarkTheme) Color(0xFF0D0D0D) else Color(0xFFF8F9FA),
        topBar = {
            // Only show TopBar on Home Screen
            if (selectedItem == NavItem.Home) {
                LargeTopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "SAMAZARQA",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Dashboard",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDarkTheme) Color.White else Color(0xFF1A1A1A)
                            )
                        }
                    },
                    actions = {
                        Surface(
                            onClick = {
                                triggerHaptic()
                                onThemeToggle()
                            },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle theme",
                                tint = PrimaryBlue,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            }
        }
    ) { padding ->
        // Main Row Layout: Content on Left, NavigationRail on Right
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. Content Area (Takes remaining space)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // Smooth transition between screens
                AnimatedContent(
                    targetState = selectedItem,
                    label = "ScreenTransition"
                ) { targetScreen ->
                    when (targetScreen) {
                        NavItem.Home -> {
                            HomeContent(
                                isDarkTheme = isDarkTheme,
                                isSecure = isSecure,
                                triggerHaptic = triggerHaptic,
                                snackbarHostState = snackbarHostState,
                                scope = scope
                            )
                        }
                        // Integration of Real Screens
                        NavItem.QR -> ScannerScreen(isDarkTheme)
                        NavItem.Downloader -> DownloaderScreen(isDarkTheme)
                        NavItem.Profile -> ProfileScreen(isDarkTheme)
                        NavItem.Settings -> SettingScreen(isDarkTheme)
                    }
                }
            }

            // 2. Navigation Rail (Right Side)
            NavigationRail(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 16.dp, horizontal = 8.dp)
                    .clip(RoundedCornerShape(24.dp)),
                containerColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White,
                contentColor = PrimaryBlue,
                header = null
            ) {
                // Centering the items vertically
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    NavItem.entries.forEach { item ->
                        NavigationRailItem(
                            selected = selectedItem == item,
                            onClick = {
                                triggerHaptic()
                                selectedItem = item
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = PrimaryBlue,
                                indicatorColor = PrimaryBlue,
                                unselectedIconColor = if (isDarkTheme) Color.Gray else Color.DarkGray,
                                unselectedTextColor = if (isDarkTheme) Color.Gray else Color.DarkGray
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Extracted Home Content to keep logic clean and work with Navigation
 */
@Composable
fun HomeContent(
    isDarkTheme: Boolean,
    isSecure: Boolean,
    triggerHaptic: () -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Security Status Card
        item {
            SecurityStatusCard(isSecure)
        }

        // Section Header
        item {
            Text(
                text = "Security Suite",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = if (isDarkTheme) Color.White else Color(0xFF1A1A1A)
            )
        }

        // Service Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ServiceCard(
                        title = "System Lock",
                        icon = Icons.Default.AdminPanelSettings,
                        modifier = Modifier.weight(1f),
                        onHaptic = triggerHaptic
                    ) {
                        scope.launch {
                            snackbarHostState.showSnackbar("System Hardened ✓")
                        }
                    }
                    ServiceCard(
                        title = "WiFi Scan",
                        icon = Icons.Default.WifiTethering,
                        modifier = Modifier.weight(1f),
                        onHaptic = triggerHaptic
                    ) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Scanning Network...")
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ServiceCard(
                        title = "Vault Pro",
                        icon = Icons.Default.EnhancedEncryption,
                        modifier = Modifier.weight(1f),
                        onHaptic = triggerHaptic
                    ) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Vault Secured ✓")
                        }
                    }
                    ServiceCard(
                        title = "Log Wipe",
                        icon = Icons.Default.CleaningServices,
                        modifier = Modifier.weight(1f),
                        onHaptic = triggerHaptic
                    ) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Logs Purged ✓")
                        }
                    }
                }
            }
        }

        // Section Header
        item {
            Text(
                text = "Protection Logs",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = if (isDarkTheme) Color.White else Color(0xFF1A1A1A)
            )
        }

        // Protection Logs
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LogItem(
                    title = "Encrypted Traffic",
                    status = "AES-256 Enabled",
                    icon = Icons.Default.Security,
                    color = Color(0xFF4CAF50)
                )
                LogItem(
                    title = "Anti-Tamper",
                    status = "Shielding Memory",
                    icon = Icons.Default.RemoveModerator,
                    color = PrimaryBlue
                )
                LogItem(
                    title = "Firewall Active",
                    status = "Blocking Threats",
                    icon = Icons.Default.GppGood,
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

// --- CORE COMPONENTS (DO NOT REMOVE) ---

/**
 * Security Status Card - Shows current protection status
 * DO NOT REMOVE OR CHANGE (as requested)
 */
@Composable
fun SecurityStatusCard(isSecure: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSecure) PrimaryBlue else Color(0xFFD32F2F)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Decorative background icon
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color.White.copy(alpha = shimmerAlpha),
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 50.dp, y = 50.dp)
            )

            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .align(Alignment.CenterStart)
            ) {
                // Status Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.25f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Text(
                            text = if (isSecure) "PROTECTED" else "DANGER",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (isSecure) "System is\nSecured" else "Security\nBreached!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 38.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = if (isSecure) 
                        "All protections active" 
                    else 
                        "Immediate action required",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

/**
 * Service Card - Interactive card for security features
 * DO NOT REMOVE OR CHANGE (as requested)
 */
@Composable
fun ServiceCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier,
    onHaptic: () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(120.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = {
                    onHaptic()
                    onClick()
                }
            ),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isPressed) 2.dp else 6.dp,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = PrimaryBlue.copy(alpha = 0.12f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Log Item - Shows security activity logs
 */
@Composable
fun LogItem(
    title: String,
    status: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = status,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color, CircleShape)
            )
        }
    }
}
