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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imtbytes.samazarqa.ui.theme.*
import kotlinx.coroutines.launch

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

    var selectedItem by remember { mutableStateOf(NavItem.Home) }
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
            CenterAlignedTopAppBar(
                title = {
                    if (selectedItem == NavItem.Home) {
                        AnimatedContent(
                            targetState = isSearching,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(400)) + expandHorizontally() togetherWith 
                                fadeOut(animationSpec = tween(400)) + shrinkHorizontally()
                            }, label = "SearchAnimation"
                        ) { searching ->
                            if (searching) {
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
                    }
                },
                actions = {
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Row(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                AnimatedContent(targetState = selectedItem, label = "ScreenTransition") { target ->
                    when (target) {
                        NavItem.Home -> HomeContent(isDarkTheme, isSecure, snackbarHostState, scope)
                        NavItem.QR -> ScannerScreen(isDarkTheme)
                        NavItem.Downloader -> DownloaderScreen(isDarkTheme)
                        NavItem.Profile -> ProfileScreen(isDarkTheme)
                        NavItem.Settings -> SettingScreen(isDarkTheme)
                    }
                }
            }

            NavigationRail(
                modifier = Modifier.fillMaxHeight().padding(vertical = 16.dp, horizontal = 8.dp).clip(RoundedCornerShape(24.dp)),
                containerColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White,
                contentColor = PrimaryBlue
            ) {
                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                    NavItem.entries.forEach { item ->
                        NavigationRailItem(
                            selected = selectedItem == item,
                            onClick = { 
                                selectedItem = item
                                if (item != NavItem.Home) isSearching = false
                            },
                            icon = { Icon(item.icon, item.label, modifier = Modifier.size(24.dp)) },
                            label = { Text(item.label, fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = Color.White,
                                indicatorColor = PrimaryBlue,
                                unselectedIconColor = if (isDarkTheme) Color.Gray else Color.DarkGray
                            )
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
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item { SecurityStatusCard(isSecure) }
        item {
            Text(text = "Security Suite", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = if (isDarkTheme) Color.White else Color(0xFF1A1A1A))
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ServiceCard("System Lock", Icons.Default.AdminPanelSettings, Modifier.weight(1f)) { scope.launch { snackbarHostState.showSnackbar("System Hardened ✓") } }
                    ServiceCard("WiFi Scan", Icons.Default.WifiTethering, Modifier.weight(1f)) { scope.launch { snackbarHostState.showSnackbar("Scanning Network...") } }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ServiceCard("Vault Pro", Icons.Default.EnhancedEncryption, Modifier.weight(1f)) { scope.launch { snackbarHostState.showSnackbar("Vault Secured ✓") } }
                    ServiceCard("Log Wipe", Icons.Default.CleaningServices, Modifier.weight(1f)) { scope.launch { snackbarHostState.showSnackbar("Logs Purged ✓") } }
                }
            }
        }
        item {
            Text(text = "Protection Logs", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = if (isDarkTheme) Color.White else Color(0xFF1A1A1A))
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

@Composable
fun SecurityStatusCard(isSecure: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.05f, targetValue = 0.15f,
        animationSpec = infiniteRepeatable(animation = tween(2000), repeatMode = RepeatMode.Reverse), label = ""
    )
    Card(
        modifier = Modifier.fillMaxWidth().height(190.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSecure) PrimaryBlue else Color(0xFFD32F2F))
    ) {
        Box {
            Icon(Icons.Default.Shield, null, tint = Color.White.copy(alpha = shimmerAlpha), modifier = Modifier.size(220.dp).align(Alignment.BottomEnd).offset(50.dp, 50.dp))
            Column(modifier = Modifier.padding(28.dp)) {
                Surface(shape = RoundedCornerShape(10.dp), color = Color.White.copy(alpha = 0.25f)) {
                    Text(if (isSecure) "PROTECTED" else "DANGER", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                Text(if (isSecure) "System is\nSecured" else "Security\nBreached!", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, lineHeight = 38.sp)
            }
        }
    }
}

@Composable
fun ServiceCard(title: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, label = "")
    Surface(
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale }.height(120.dp).clickable(interactionSource, null, onClick = onClick),
        shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(26.dp))
            Spacer(Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

@Composable
fun LogItem(title: String, status: String, icon: ImageVector, color: Color) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(color.copy(0.12f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(status, color = MaterialTheme.colorScheme.onSurface.copy(0.6f), fontSize = 13.sp)
            }
        }
    }
}
