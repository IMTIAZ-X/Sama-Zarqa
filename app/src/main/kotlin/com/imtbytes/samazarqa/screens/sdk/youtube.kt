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
import java.util.regex.Pattern

// --- Data Models ---
enum class MediaType { VIDEO, AUDIO, UNKNOWN }

data class MediaInfo(
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val downloadUrl: String,
    val platformIcon: ImageVector,
    val mediaType: MediaType = MediaType.VIDEO,
    val isEncrypted: Boolean = false
)

object NativeYoutubeSdk {

    /**
     * মেইন ফাংশন যা UI থেকে কল করা হবে
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
     * Native YouTube Parser (No Libraries)
     * এটি সোর্স কোড থেকে JSON খুঁজে বের করে এবং ডাইরেক্ট লিঙ্ক খোঁজে
     */
    private fun extractYoutubeNative(urlStr: String): MediaInfo? {
        val html = fetchHtml(urlStr) ?: return null

        // ১. টাইটেল এবং থাম্বনেইল বের করা
        val titleRegex = "\"title\":\"(.*?)\"".toRegex()
        val thumbRegex = "\"thumbnail\":\\{\"thumbnails\":\\[\\{\"url\":\"(.*?)\"".toRegex()
        
        val title = titleRegex.find(html)?.groupValues?.get(1) ?: "YouTube Video"
        val thumbnail = thumbRegex.find(html)?.groupValues?.get(1) ?: ""

        // ২. ytInitialPlayerResponse JSON খুঁজে বের করা
        // ইউটিউব সোর্স কোডে এই ভেরিয়েবলের ভেতর সব ভিডিও ডাটা থাকে
        val jsonPattern = Pattern.compile("var ytInitialPlayerResponse\\s*=\\s*(\\{.+?\\});")
        val matcher = jsonPattern.matcher(html)

        var directUrl: String? = null
        var isSignatureProtected = false

        if (matcher.find()) {
            try {
                val jsonString = matcher.group(1)
                val json = JSONObject(jsonString)

                // streamingData অবজেক্ট চেক করা
                if (json.has("streamingData")) {
                    val streamingData = json.getJSONObject("streamingData")
                    
                    // Formats (ভিডিও + অডিও মিক্স) চেক করা
                    if (streamingData.has("formats")) {
                        val formats = streamingData.getJSONArray("formats")
                        for (i in 0 until formats.length()) {
                            val format = formats.getJSONObject(i)
                            
                            // যদি ডাইরেক্ট 'url' থাকে, তাহলে এটি ডাউনলোড করা যাবে
                            if (format.has("url")) {
                                directUrl = format.getString("url")
                                break // প্রথম ভালো কোয়ালিটি পেলেই লুপ ব্রেক
                            } else if (format.has("signatureCipher")) {
                                isSignatureProtected = true
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // ৩. রেজাল্ট রিটার্ন করা
        return MediaInfo(
            title = title,
            description = if (directUrl != null) "Ready to download (Native)" else "Protected Content (Cannot decrypt natively)",
            thumbnailUrl = thumbnail,
            downloadUrl = directUrl ?: urlStr, // ডাইরেক্ট লিঙ্ক না পেলে সোর্স লিঙ্ক ফেরত দিব
            platformIcon = Icons.Rounded.PlayArrow,
            isEncrypted = directUrl == null && isSignatureProtected
        )
    }

    /**
     * Facebook, Instagram, TikTok (Generic Parser)
     * মেটা ট্যাগ (og:video) ব্যবহার করে
     */
    private fun extractGenericSocial(urlStr: String): MediaInfo? {
        val html = fetchHtml(urlStr) ?: return null

        val titleRegex = "<title>(.*?)</title>".toRegex()
        val ogVideoRegex = "meta property=\"og:video\" content=\"(.*?)\"".toRegex()
        val ogImageRegex = "meta property=\"og:image\" content=\"(.*?)\"".toRegex()

        val title = titleRegex.find(html)?.groupValues?.get(1) ?: "Social Video"
        val thumbnail = ogImageRegex.find(html)?.groupValues?.get(1) ?: ""
        
        // ডাইরেক্ট ভিডিও লিঙ্ক খোঁজা
        val videoUrl = ogVideoRegex.find(html)?.groupValues?.get(1)?.replace("&amp;", "&")

        return MediaInfo(
            title = title,
            description = if (videoUrl != null) "Video found" else "Webpage only",
            thumbnailUrl = thumbnail,
            downloadUrl = videoUrl ?: urlStr,
            platformIcon = if (urlStr.contains("facebook")) Icons.Rounded.VideoLibrary else Icons.Rounded.Link,
            isEncrypted = videoUrl == null
        )
    }

    // --- নেটওয়ার্ক হেল্পার ---
    private fun fetchHtml(urlStr: String): String? {
        return try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            // ব্রাউজারের মতো ইউজার এজেন্ট দেওয়া জরুরি
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
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