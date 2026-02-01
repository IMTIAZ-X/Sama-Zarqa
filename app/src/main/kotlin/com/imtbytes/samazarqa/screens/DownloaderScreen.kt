package com.imtbytes.samazarqa.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imtbytes.samazarqa.ui.theme.PrimaryBlue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

// --- Data Models ---
data class MediaInfo(
    val title: String,
    val description: String,
    val thumbnailUrl: String, // For native loading
    val platformIcon: androidx.compose.ui.graphics.vector.ImageVector
)

data class QualityOption(val label: String, val size: String, val isAudio: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloaderScreen(isDarkTheme: Boolean) {
    var urlText by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    
    // Mock Data Holder for the Sheet
    var currentMediaInfo by remember { mutableStateOf<MediaInfo?>(null) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Main Layout
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Secure Downloader",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Input Area
            InputSection(
                urlText = urlText,
                onUrlChange = { urlText = it },
                isAnalyzing = isAnalyzing,
                onAnalyzeClick = {
                    if (urlText.isNotEmpty()) {
                        isAnalyzing = true
                        // Simulate Network Analysis (Mocking Native Behavior)
                        scope.launch {
                            kotlinx.coroutines.delay(1500) // Fake loading
                            currentMediaInfo = MediaInfo(
                                title = "Amazing Nature 4K - Relaxing Music",
                                description = "Enjoy the beautiful scenery of nature with calming music. Best for relaxation and study.",
                                thumbnailUrl = "https://picsum.photos/600/350", // Random image for demo
                                platformIcon = Icons.Rounded.PlayCircle
                            )
                            isAnalyzing = false
                            showBottomSheet = true
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // History Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Activity",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { /* Clear All logic */ }) {
                    Text("Clear All", color = PrimaryBlue)
                }
            }

            // Downloads List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(3) { index ->
                    DownloadItem(
                        fileName = "Social_Media_Clip_${index + 1}.mp4",
                        size = "${(index + 2) * 5} MB",
                        isCompleted = index != 0
                    )
                }
            }
        }

        // --- Bottom Sheet Logic ---
        if (showBottomSheet && currentMediaInfo != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                DownloadOptionsSheetContent(
                    mediaInfo = currentMediaInfo!!,
                    onDownloadStart = {
                        showBottomSheet = false
                        // Handle download logic here
                    }
                )
            }
        }
    }
}

// --- Composable: Input Section ---
@Composable
fun InputSection(
    urlText: String,
    onUrlChange: (String) -> Unit,
    isAnalyzing: Boolean,
    onAnalyzeClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f), RoundedCornerShape(24.dp)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = urlText,
                onValueChange = onUrlChange,
                placeholder = { Text("Paste YouTube, TikTok link here...", fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                ),
                leadingIcon = { 
                    Icon(Icons.Rounded.Link, contentDescription = null, tint = PrimaryBlue) 
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onAnalyzeClick,
                enabled = !isAnalyzing && urlText.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = PrimaryBlue.copy(alpha = 0.6f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Analyzing Link...")
                } else {
                    Icon(Icons.Rounded.Download, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze & Download", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- Composable: Bottom Sheet Content ---
@Composable
fun DownloadOptionsSheetContent(
    mediaInfo: MediaInfo,
    onDownloadStart: () -> Unit
) {
    var selectedQuality by remember { mutableStateOf(0) }
    val qualities = listOf(
        QualityOption("1080p", "45 MB"),
        QualityOption("720p", "22 MB"),
        QualityOption("480p", "12 MB"),
        QualityOption("MP3 Audio", "4.5 MB", true)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Handle Bar
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.outlineVariant)
                .align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // Thumbnail & Info
        Row(modifier = Modifier.fillMaxWidth()) {
            // Native Image Loader (No Library)
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(120.dp)
                    .height(80.dp)
            ) {
                NativeNetworkImage(
                    url = mediaInfo.thumbnailUrl,
                    contentDescription = "Thumbnail",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = mediaInfo.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mediaInfo.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Select Quality",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        // Quality Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(qualities.size) { index ->
                val option = qualities[index]
                val isSelected = selectedQuality == index
                
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedQuality = index },
                    label = { 
                        Text(text = "${option.label} • ${option.size}") 
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if(option.isAudio) Icons.Rounded.Audiotrack else Icons.Rounded.Videocam,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue.copy(alpha = 0.1f),
                        selectedLabelColor = PrimaryBlue,
                        selectedLeadingIconColor = PrimaryBlue
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Final Download Action
        Button(
            onClick = onDownloadStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Download Now", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Rounded.FileDownload, null)
        }
    }
}

// --- Helper: Native Network Image Loader (NO 3rd Party Library) ---
@Composable
fun NativeNetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    var bitmap by remember(url) { mutableStateOf<Bitmap?>(null) }

    // Fetch image in background thread natively
    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = URL(url).openStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error or set fallback
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        // Loading / Placeholder State
        Box(
            modifier = modifier.background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = Color.Gray
            )
        }
    }
}

// --- Existing DownloadItem with Minor Polish ---
@Composable
fun DownloadItem(
    fileName: String,
    size: String,
    isCompleted: Boolean
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp, // Slight elevation for depth
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFE3F2FD)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if(isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.Downloading,
                    contentDescription = null,
                    tint = if(isCompleted) Color(0xFF4CAF50) else PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fileName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if(isCompleted) "Completed" else "Downloading...",
                        fontSize = 12.sp,
                        color = if(isCompleted) Color(0xFF4CAF50) else PrimaryBlue,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " • $size",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            if(!isCompleted) {
                CircularProgressIndicator(
                    progress = { 0.45f },
                    modifier = Modifier.size(28.dp),
                    color = PrimaryBlue,
                    trackColor = PrimaryBlue.copy(alpha = 0.2f),
                    strokeWidth = 3.dp,
                )
            } else {
                IconButton(onClick = { /* Open file */ }) {
                    Icon(
                        Icons.Rounded.FolderOpen, 
                        contentDescription = "Open",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}