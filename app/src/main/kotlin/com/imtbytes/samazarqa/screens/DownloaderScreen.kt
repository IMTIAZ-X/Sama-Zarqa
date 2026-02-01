package com.imtbytes.samazarqa.screens

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.imtbytes.samazarqa.ui.theme.PrimaryBlue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern
import com.imtbytes.samazarqa.screens.sdk.NativeYoutubeSdk

// --- Data Models ---
data class MediaInfo(
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val downloadUrl: String, // আসল ভিডিও লিঙ্ক (Extract করা)
    val platformIcon: ImageVector,
    val isDirectVideo: Boolean = false
)

data class QualityOption(val label: String, val size: String, val isAudio: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloaderScreen(isDarkTheme: Boolean) {
    var urlText by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var currentMediaInfo by remember { mutableStateOf<MediaInfo?>(null) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // --- ১. অ্যাপ ওপেন হলেই পারমিশন চেক ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            Toast.makeText(context, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permission Denied! Download won't work.", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
            }
        }
    }

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
            Text(
                text = "Sama Zarqa Downloader",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Input Area
            InputSection(
                urlText = urlText,
                onUrlChange = { urlText = it },
                isAnalyzing = isAnalyzing,
                onAnalyzeClick = {
                    if (urlText.isNotEmpty()) {
                        isAnalyzing = true
                        scope.launch {
                            // লিঙ্ক এনালাইজ এবং আসল ভিডিও লিঙ্ক বের করার চেষ্টা
                            val info = extractVideoInfo(urlText)
                            isAnalyzing = false
                            
                            if (info != null) {
                                currentMediaInfo = info
                                showBottomSheet = true
                            } else {
                                Toast.makeText(context, "Could not fetch video info. Try another link.", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please paste a link first", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // History Header
            Text(
                text = "Downloads History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Downloads List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(2) { index ->
                    DownloadItem(
                        fileName = "Sama_Video_Demo_${index + 1}.mp4",
                        size = "15 MB",
                        isCompleted = true
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
                    onDownloadStart = { fileName ->
                        showBottomSheet = false
                        // ডাউনলোড শুরু
                        startSmartDownload(context, currentMediaInfo!!.downloadUrl, fileName)
                    }
                )
            }
        }
    }
}

// --- ২. Native Video Extractor (HTML থেকে MP4 খোঁজা) ---
suspend fun extractVideoInfo(urlStr: String): MediaInfo? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.connect()

            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            val html = sb.toString()

            // মেটাডাটা বের করা
            val titleRegex = "<title>(.*?)</title>".toRegex()
            val ogImageRegex = "meta property=\"og:image\" content=\"(.*?)\"".toRegex()
            val ogVideoRegex = "meta property=\"og:video\" content=\"(.*?)\"".toRegex() // ফেসবুকের জন্য
            val twitterPlayerRegex = "twitter:player:stream\" content=\"(.*?)\"".toRegex()

            var title = titleRegex.find(html)?.groupValues?.get(1) ?: "Sama Video Download"
            val thumbnail = ogImageRegex.find(html)?.groupValues?.get(1) ?: ""
            
            // আসল ভিডিও লিঙ্ক খোঁজা (সবচেয়ে গুরুত্বপূর্ণ পার্ট)
            var videoUrl = ogVideoRegex.find(html)?.groupValues?.get(1)
            if (videoUrl == null) {
                videoUrl = twitterPlayerRegex.find(html)?.groupValues?.get(1)
            }
            
            // যদি ভিডিও লিঙ্ক না পাওয়া যায়, তবুও আমরা ইউজারকে অপশন দিব (কিন্তু ওয়ার্নিং সহ)
            val finalDownloadUrl = videoUrl?.replace("&amp;", "&") ?: urlStr
            val isDirect = videoUrl != null

            // টাইটেল ক্লিন করা
            title = title.replace("&#39;", "'").replace("&amp;", "&").take(50)

            MediaInfo(
                title = title,
                description = if(isDirect) "Video found! Ready to download." else "Webpage detected. Real video might not download.",
                thumbnailUrl = thumbnail,
                downloadUrl = finalDownloadUrl,
                platformIcon = if(urlStr.contains("youtube")) Icons.Rounded.PlayArrow else Icons.Rounded.Link,
                isDirectVideo = isDirect
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

// --- ৩. Download Manager Logic (Correct Path) ---
fun startSmartDownload(context: Context, url: String, title: String) {
    // চেক: যদি লিঙ্ক ইউটিউবের হয় এবং ডাইরেক্ট লিঙ্ক না হয়
    if (url.contains("youtube.com") || url.contains("youtu.be")) {
         Toast.makeText(context, "Note: YouTube encrypts videos. This might download a small file only.", Toast.LENGTH_LONG).show()
    }

    try {
        val request = DownloadManager.Request(Uri.parse(url))
        
        val safeFileName = "SamaZarqa_" + title.replace("[^a-zA-Z0-9.-]".toRegex(), "_") + ".mp4"
        
        request.setTitle(title)
        request.setDescription("Downloading via Sama Zarqa...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        
        // --- Path Fix: /Download/SamaZarqa/ ---
        // Android 11+ এ সরাসরি রুটে (Storage/0/SamaZarqa) ফোল্ডার করা যায় না।
        // তাই স্ট্যান্ডার্ড Download ফোল্ডারের ভেতরে সাব-ফোল্ডার করা হচ্ছে।
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "SamaZarqa/$safeFileName")
        
        request.setAllowedOverMetered(true)
        request.setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(context, "Downloading to: Downloads/SamaZarqa", Toast.LENGTH_LONG).show()
        
    } catch (e: Exception) {
        Toast.makeText(context, "Download Error: ${e.message}", Toast.LENGTH_LONG).show()
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f)), 
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = urlText,
                onValueChange = onUrlChange,
                placeholder = { Text("Paste Link Here...", fontSize = 14.sp) },
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
                    Text("Searching Video...")
                } else {
                    Icon(Icons.Rounded.Search, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze & Download", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            // DownloaderScreen এর ভেতরে onAnalyzeClick এর লজিক:
onAnalyzeClick = {
    if (urlText.isNotEmpty()) {
        isAnalyzing = true
        scope.launch {
            // ১. Native SDK কল করা হচ্ছে
            val info = NativeYoutubeSdk.extractVideoInfo(urlText)
            isAnalyzing = false
            
            if (info != null) {
                currentMediaInfo = info
                showBottomSheet = true
                
                // ওয়ার্নিং টোস্ট যদি ভিডিও এনক্রিপ্টেড হয়
                if (info.isEncrypted) {
                    Toast.makeText(context, "Protected Video: Cannot download directly via Native code", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Failed to analyze link", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
        }
    }
}

// --- Composable: Bottom Sheet Content ---
@Composable
fun DownloadOptionsSheetContent(
    mediaInfo: MediaInfo,
    onDownloadStart: (String) -> Unit
) {
    var selectedQuality by remember { mutableStateOf(0) }
    
    // ডাইনামিক অপশন দেখানো
    val qualities = if(mediaInfo.isDirectVideo) {
        listOf(
            QualityOption("HD Video", "Unknown Size"),
            QualityOption("SD Video", "Low Data")
        )
    } else {
        listOf(
            QualityOption("Source File", "Unknown"),
        )
    }

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
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .width(120.dp)
                    .height(80.dp)
            ) {
                if(mediaInfo.thumbnailUrl.isNotEmpty()){
                    NativeNetworkImage(
                        url = mediaInfo.thumbnailUrl,
                        contentDescription = "Thumbnail",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Gray))
                }
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
                
                // ইউজারকে সতর্ক করা হচ্ছে
                if (!mediaInfo.isDirectVideo && (mediaInfo.downloadUrl.contains("youtube") || mediaInfo.downloadUrl.contains("youtu"))) {
                    Text(
                        text = "⚠️ YouTube links are encrypted. Might fail without API.",
                        fontSize = 11.sp,
                        color = Color.Red,
                        maxLines = 2
                    )
                } else {
                    Text(
                        text = mediaInfo.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(qualities.size) { index ->
                val option = qualities[index]
                val isSelected = selectedQuality == index
                
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedQuality = index },
                    label = { Text(text = "${option.label}") },
                    leadingIcon = {
                        Icon(Icons.Rounded.Videocam, null, modifier = Modifier.size(18.dp))
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

        Button(
            onClick = { onDownloadStart(mediaInfo.title) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Download Now", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Rounded.FileDownload, null)
        }
    }
}

// --- Helper: Native Network Image Loader ---
@Composable
fun NativeNetworkImage(url: String, contentDescription: String?, modifier: Modifier = Modifier) {
    var bitmap by remember(url) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = URL(url).openStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
    if (bitmap != null) {
        Image(bitmap = bitmap!!.asImageBitmap(), contentDescription = contentDescription, contentScale = ContentScale.Crop, modifier = modifier)
    } else {
        Box(modifier = modifier.background(Color.LightGray), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun DownloadItem(fileName: String, size: String, isCompleted: Boolean) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFE3F2FD)), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(fileName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Completed • $size", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}