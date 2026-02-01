package com.imtbytes.samazarqa.screens.sdk

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.regex.Pattern

// --- Data Models (DownloaderScreen এর সাথে মিল রেখে) ---
data class MediaInfo(
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val downloadUrl: String,
    val platformIcon: ImageVector,
    val isDirectVideo: Boolean = false,
    val isEncrypted: Boolean = false
)

object NativeYoutubeSdk {

    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36"
    
    // Regex Patterns for Parsing
    private val patPlayerResponse = Pattern.compile("var ytInitialPlayerResponse\\s*=\\s*(\\{.+?\\})\\s*;")
    private val patTitle = Pattern.compile("<title>(.*?)</title>")
    private val patOgVideo = Pattern.compile("meta property=\"og:video\" content=\"(.*?)\"")
    
    /**
     * মেইন এক্সট্রাকশন ফাংশন
     */
    suspend fun extractVideoInfo(urlStr: String): MediaInfo? {
        return withContext(Dispatchers.IO) {
            try {
                if (urlStr.contains("youtube.com") || urlStr.contains("youtu.be")) {
                    extractYoutubeNative(urlStr)
                } else {
                    extractGenericSocial(urlStr)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * YouTube Native Parsing Logic
     * এটি আপনার দেওয়া KotlinYouTubeExtractor এর লজিক অনুসরণ করে তৈরি।
     */
    private fun extractYoutubeNative(urlStr: String): MediaInfo? {
        val html = fetchHtml(urlStr) ?: return null

        // ১. টাইটেল এবং থাম্বনেইল বের করা
        var title = "YouTube Video"
        val titleMatcher = patTitle.matcher(html)
        if (titleMatcher.find()) {
            title = titleMatcher.group(1)?.replace(" - YouTube", "") ?: "YouTube Video"
        }

        // ২. ytInitialPlayerResponse JSON খুঁজে বের করা
        val matcher = patPlayerResponse.matcher(html)
        var directUrl: String? = null
        var isSignatureProtected = false
        var thumbnailUrl = ""

        if (matcher.find()) {
            try {
                val jsonString = matcher.group(1)
                val json = JSONObject(jsonString)

                // ভিডিও ডিটেইলস থেকে থাম্বনেইল এবং টাইটেল নিশ্চিত করা
                if (json.has("videoDetails")) {
                    val details = json.getJSONObject("videoDetails")
                    if (details.has("title")) title = details.getString("title")
                    if (details.has("thumbnail")) {
                        val thumbnails = details.getJSONObject("thumbnail").getJSONArray("thumbnails")
                        if (thumbnails.length() > 0) {
                            thumbnailUrl = thumbnails.getJSONObject(thumbnails.length() - 1).getString("url")
                        }
                    }
                }

                // Streaming Data পার্স করা (Formats & AdaptiveFormats)
                if (json.has("streamingData")) {
                    val streamingData = json.getJSONObject("streamingData")
                    
                    // Priority 1: Formats (Video + Audio combined) - সাধারণত itag 18, 22
                    if (streamingData.has("formats")) {
                        val formats = streamingData.getJSONArray("formats")
                        for (i in 0 until formats.length()) {
                            val format = formats.getJSONObject(i)
                            if (format.has("url")) {
                                directUrl = format.getString("url").replace("\\u0026", "&")
                                // mp4 ফরম্যাট অগ্রাধিকার দেওয়া
                                if (format.has("mimeType") && format.getString("mimeType").contains("mp4")) {
                                    break 
                                }
                            } else if (format.has("signatureCipher") || format.has("cipher")) {
                                isSignatureProtected = true
                            }
                        }
                    }

                    // Priority 2: AdaptiveFormats (যদি Formats এ ডাইরেক্ট লিঙ্ক না পাওয়া যায়)
                    if (directUrl == null && streamingData.has("adaptiveFormats")) {
                        val adaptiveFormats = streamingData.getJSONArray("adaptiveFormats")
                        for (i in 0 until adaptiveFormats.length()) {
                            val format = adaptiveFormats.getJSONObject(i)
                            // শুধুমাত্র ভিডিও এবং mp4 খোঁজা হচ্ছে
                            if (format.has("url") && format.has("mimeType") && format.getString("mimeType").contains("video/mp4")) {
                                directUrl = format.getString("url").replace("\\u0026", "&")
                                // 720p বা তার নিচে খোঁজা (বেশি হাই কোয়ালিটি অনেক সময় অডিও ছাড়া থাকে)
                                if (format.has("height") && format.getInt("height") <= 720) {
                                    break
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // HTML Entity Clean up
        title = title.replace("&quot;", "\"").replace("&#39;", "'").replace("&amp;", "&")

        // রেজাল্ট রিটার্ন
        return MediaInfo(
            title = title,
            description = if (directUrl != null) "Ready to Download (Direct)" else if(isSignatureProtected) "Encrypted/Protected Video (Requires Decipher)" else "Could not fetch stream",
            thumbnailUrl = thumbnailUrl,
            downloadUrl = directUrl ?: urlStr,
            platformIcon = Icons.Rounded.PlayArrow,
            isDirectVideo = directUrl != null,
            isEncrypted = directUrl == null && isSignatureProtected
        )
    }

    /**
     * Generic Social Media Extractor (Facebook, Instagram, etc.)
     */
    private fun extractGenericSocial(urlStr: String): MediaInfo? {
        val html = fetchHtml(urlStr) ?: return null

        var title = "Social Video"
        val titleMatcher = patTitle.matcher(html)
        if (titleMatcher.find()) title = titleMatcher.group(1) ?: "Social Video"

        val ogVideoMatcher = patOgVideo.matcher(html)
        var videoUrl = if (ogVideoMatcher.find()) ogVideoMatcher.group(1) else null
        
        // Facebook specific fix
        if (videoUrl != null) {
            videoUrl = videoUrl.replace("&amp;", "&")
        }

        // Thumbnail extraction
        val ogImageRegex = "meta property=\"og:image\" content=\"(.*?)\"".toRegex()
        val thumbnail = ogImageRegex.find(html)?.groupValues?.get(1) ?: ""

        return MediaInfo(
            title = title,
            description = if (videoUrl != null) "Direct Video Found" else "Webpage Link Only",
            thumbnailUrl = thumbnail,
            downloadUrl = videoUrl ?: urlStr,
            platformIcon = if(urlStr.contains("facebook")) Icons.Rounded.VideoLibrary else Icons.Rounded.Link,
            isDirectVideo = videoUrl != null,
            isEncrypted = false
        )
    }

    /**
     * Network Helper utilizing HttpURLConnection
     */
    private fun fetchHtml(urlStr: String): String? {
        return try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", USER_AGENT)
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.connect()

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            reader.close()
            sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}