package com.imtbytes.samazarqa.screens

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.widget.Toast
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
import com.imtbytes.samazarqa.ui.theme.PrimaryBlue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

// --- Data Models ---
data class MediaInfo(
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val downloadUrl: String, // আসল ভিডিও লিঙ্ক
    val platformIcon: ImageVector
)

data class QualityOption(val label: String, val size: String, val isAudio: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloaderScreen(isDarkTheme: Boolean) {
    var urlText by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    
    // ডাউনলোড এবং ভিডিও তথ্যের জন্য স্টেট
    var currentMediaInfo by remember { mutableStateOf<MediaInfo?>(null) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
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
            Text(
                text = "Secure Downloader",
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
                            // ১. লিঙ্ক এনালাইজ করা হচ্ছে (Native HTML Parsing)
                            val info = analyzeLinkNatively(urlText)
                            
                            isAnalyzing = false
                            if (info != null) {
                                currentMediaInfo = info
                                showBottomSheet = true
                            } else {
                                Toast.makeText(context, "Could not fetch info. Try a direct link.", Toast.LENGTH_SHORT).show()
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
                text = "Recent Downloads",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Downloads List (Static Demo)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(3) { index ->
                    DownloadItem(
                        fileName = "Sama_Video_${index + 1}.mp4",
                        size = "${(index + 2) * 5} MB",
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
                        // ২. আসল ডাউনলোড শুরু করা
                        startNativeDownload(context, currentMediaInfo!!.downloadUrl, fileName)
                    }
                )
            }
        }
    }
}

// --- Native Logic: Link Analyzer (No Library) ---
suspend fun analyzeLinkNatively(urlStr: String): MediaInfo? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "Mozilla/5.0") // ব্রাউজার হিসেবে ভান করা
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.connect()

            // HTML পড়া
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            val html = sb.toString()

            // ৩. Regex দিয়ে টাইটেল এবং ছবি বের করা (Native Parsing)
            val titleRegex = "<title>(.*?)</title>".toRegex()
            val ogImageRegex = "meta property=\"og:image\" content=\"(.*?)\"".toRegex()
            
            val title = titleRegex.find(html)?.groupValues?.get(1) ?: "Unknown Video"
            val thumbnail = ogImageRegex.find(html)?.groupValues?.get(1) ?: ""

            // *গুরুত্বপূর্ণ*: ইউটিউব/ফেসবুক থেকে সরাসরি ভিডিও লিঙ্ক বের করা লাইব্রেরি ছাড়া খুবই কঠিন।
            // তাই আমরা ডাউনলোড লিঙ্ক হিসেবে আসল ইউজার ইনপুটটাই রাখছি, অথবা একটি ডামি ডিরেক্ট লিঙ্ক দিচ্ছি।
            // বাস্তব অ্যাপে এখানে একটি API call লাগে।
            
            MediaInfo(
                title = title.replace("&#39;", "'").replace("&amp;", "&"),
                description = "Ready to download from source.",
                thumbnailUrl = thumbnail,
                downloadUrl = urlStr, // এই লিঙ্কটি ডাউনলোডারকে পাঠানো হবে
                platformIcon = if(urlStr.contains("youtube")) Icons.Rounded.PlayArrow else Icons.Rounded.Link
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

// --- Native Logic: Download Manager (Real Download) ---
fun startNativeDownload(context: Context, url: String, title: String) {
    try {
        val request = DownloadManager.Request(Uri.parse(url))
        
        // ফাইলের নাম ক্লিন করা
        val safeFileName = title.replace("[^a-zA-Z0-9.-]".toRegex(), "_") + ".mp4"
        
        request.setTitle(title)
        request.setDescription("Downloading video...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        
        // ৪. পাথ সেট করা: /Download/SamaZarqa/
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "SamaZarqa/$safeFileName")
        request.setAllowedOverMetered(true)
        request.setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(context, "Download Started! Check Notification.", Toast.LENGTH_LONG).show()
        
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.localizedMessage}. Note: YouTube encryption prevents direct downloads without API.", Toast.LENGTH_LONG).show()
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
                placeholder = { Text("Paste Link (e.g. Facebook, Direct MP4)", fontSize = 14.sp) },
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
                    Text("Analyzing...")
                } else {
                    Icon(Icons.Rounded.Search, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze Link", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
    
    // ডামি কোয়ালিটি অপশন (বাস্তবে ভিডিও সাইজ চেক করা কঠিন লাইব্রেরি ছাড়া)
    val qualities = listOf(
        QualityOption("Best Quality", "Unknown Size"),
        QualityOption("Data Saver", "Low Size"),
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
                            imageVector = Icons.Rounded.Videocam,
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
            onClick = { onDownloadStart(mediaInfo.title) },
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

// --- Helper: Native Network Image Loader ---
@Composable
fun NativeNetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    var bitmap by remember(url) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            try {
                val inputStream = URL(url).openStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
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
        Box(
            modifier = modifier.background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
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
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
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