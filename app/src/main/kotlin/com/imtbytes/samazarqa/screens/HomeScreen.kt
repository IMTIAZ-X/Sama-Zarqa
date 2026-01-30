package com.imtbytes.samazarqa.screens

import androidx.compose.animation.*
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
import androidx.compose.material.icons.rounded.*
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
import com.imtbytes.samazarqa.screens.ScannerScreen
import com.imtbytes.samazarqa.screens.DownloaderScreen
import com.imtbytes.samazarqa.screens.ProfileScreen
import com.imtbytes.samazarqa.screens.SettingScreen
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
    isSecure: Boolean
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // State for selected Navigation Item
    var selectedItem by remember { mutableStateOf(NavItem.Home) }
    
    // Search States
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

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
            // AppBar setup with search animation logic
            CenterAlignedTopAppBar(
                title = {
                    AnimatedContent(
                        targetState = isSearching,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(400)) + expandHorizontally() togetherWith 
                            fadeOut(animationSpec = tween(400)) + shrinkHorizontally()
                        }, label = "SearchAnimation"
                    ) { searching ->
                        if (searching && selectedItem == NavItem.Home) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search protection...", fontSize = 14.sp) },
                                modifier = Modifier.fillMaxWidth(0.95f).height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                singleLine = true
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "SAMAZARQA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    text = "Security Hub",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if(isDarkTheme) Color.White else Color.Black
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Only show Search Icon on HomeScreen
                    if (selectedItem == NavItem.Home) {
                        IconButton(onClick = { isSearching = !isSearching }) {
                            Icon(
                                imageVector = if (isSearching) Icons.Rounded.Close else Icons.Rounded.Search,
                                contentDescription = "Search",
                                tint = if (isSearching) Color.Red else PrimaryBlue
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                AnimatedContent(
                    targetState = selectedItem,
                    label = "ScreenTransition"
                ) { targetScreen ->
                    when (targetScreen) {
                        NavItem.Home -> {
                            HomeContent(
                                isDarkTheme = isDarkTheme,
                                isSecure = isSecure,
                                snackbarHostState = snackbarHostState,
                                scope = scope
                            )
                        }
                        NavItem.QR -> ScannerScreen(isDarkTheme)
                        NavItem.Downloader -> DownloaderScreen(isDarkTheme)
                        NavItem.Profile -> ProfileScreen(isDarkTheme)
                        NavItem.Settings -> SettingScreen(isDarkTheme)
                    }
                }
            }

            // Navigation Rail remains untouched as requested
            NavigationRail(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 16.dp, horizontal = 8.dp)
                    .clip(RoundedCornerShape(24.dp)),
                containerColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White,
                contentColor = PrimaryBlue,
                header = null
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    NavItem.entries.forEach { item ->
                        NavigationRailItem(
                            selected = selectedItem == item,
                            onClick = {
                                selectedItem = item
                                // Reset search when leaving Home
                                if (item != NavItem.Home) isSearching = false
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

@Composable
fun HomeContent(
    isDarkTheme: Boolean,
    isSecure: Boolean,
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
        item { SecurityStatusCard(isSecure) }

        item {
            Text(
                text = "Security Suite",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = if (isDarkTheme) Color.White else Color(0xFF1A1A1A)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ServiceCard(
                        title = "System Lock",
                        icon = Icons.Default.AdminPanelSettings,
                        modifier = Modifier.weight(1f)
                    ) {
                        scope.launch {
                            snackbarHostState.showSnackbar("System Hardened ✓")
                        }
                    }
                    ServiceCard(
                        title = "WiFi Scan",
                        icon = Icons.Default.WifiTethering,
                        modifier = Modifier.weight(1f)
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
                        modifier = Modifier.weight(1f)
                    ) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Vault Secured ✓")
                        }
                    }
                    ServiceCard(
                        title = "Log Wipe",
                        icon = Icons.Default.CleaningServices,
                        modifier = Modifier.weight(1f)
                    ) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Logs Purged ✓")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Protection Logs",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = if (isDarkTheme) Color.White else Color(0xFF1A1A1A)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LogItem("Encrypted Traffic", "AES-256 Enabled", Icons.Default.Security, Color(0xFF4CAF50))
                LogItem("Anti-Tamper", "Shielding Memory", Icons.Default.RemoveModerator, PrimaryBlue)
                LogItem("Firewall Active", "Blocking Threats", Icons.Default.GppGood, Color(0xFFFF9800))
            }
        }
    }
}

// --- CORE COMPONENTS MAINTAINED ---

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
        modifier = Modifier.fillMaxWidth().height(190.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSecure) PrimaryBlue else Color(0xFFD32F2F)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color.White.copy(alpha = shimmerAlpha),
                modifier = Modifier.size(220.dp).align(Alignment.BottomEnd).offset(x = 50.dp, y = 50.dp)
            )
            Column(modifier = Modifier.padding(28.dp).align(Alignment.CenterStart)) {
                Surface(shape = RoundedCornerShape(10.dp), color = Color.White.copy(alpha = 0.25f)) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                        Text(text = if (isSecure) "PROTECTED" else "DANGER", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = if (isSecure) "System is\nSecured" else "Security\nBreached!", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, lineHeight = 38.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = if (isSecure) "All protections active" else "Immediate action required", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}

@Composable
fun ServiceCard(title: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.94f else 1f, label = "cardScale")

    Surface(
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale }.height(120.dp)
            .clickable(interactionSource = interactionSource, indication = LocalIndication.current, onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isPressed) 2.dp else 6.dp
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, color = PrimaryBlue.copy(alpha = 0.12f), modifier = Modifier.size(56.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(imageVector = icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(26.dp)) }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun LogItem(title: String, status: String, icon: ImageVector, color: Color) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(color.copy(alpha = 0.12f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = status, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
        }
    }
}
