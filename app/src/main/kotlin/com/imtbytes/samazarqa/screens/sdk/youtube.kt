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

/**
 * SDK এর নিজস্ব Data Model।
 * DownloaderScreen এর সাথে ডাটা আদান-প্রদানের জন্য এটি ব্যবহার হবে।
 */
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

    /**
     * এই ফাংশনটি URL চেক করে সঠিক মেথড কল করবে।
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
     * YouTube এর সোর্স কোড থেকে JSON পার্স করে ডাইরেক্ট ভিডিও লিঙ্ক বের করার লজিক।
     */
    private fun extractYoutubeNative(urlStr: String): MediaInfo? {
        val html = fetchHtml(urlStr) ?: return null

        // টাইটেল এবং থাম্বনেইল বের করা (Regex ব্যবহার করে)
        val titleRegex = "\"title\":\"(.*?)\"".toRegex()
        val thumbRegex = "\"thumbnail\":\\{\"thumbnails\":\\[\\{\"url\":\"(.*?)\"".toRegex()
        
        val title = titleRegex.find(html)?.groupValues?.get(1) ?: "YouTube Video"
        val thumbnail = thumbRegex.find(html)?.groupValues?.get(1) ?: ""

        // ytInitialPlayerResponse ভেরিয়েবল খোঁজা যা ভিডিওর সব ডাটা ধারণ করে
        val jsonPattern = Pattern.compile("var ytInitialPlayerResponse\\s*=\\s*(\\{.+?\\});")
        val matcher = jsonPattern.matcher(html)

        var directUrl: String? = null
        var isSignatureProtected = false

        if (matcher.find()) {
            try {
                val jsonString = matcher.group(1)
                val json = JSONObject(jsonString)

                // streamingData চেক করা
                if (json.has("streamingData")) {
                    val streamingData = json.getJSONObject("streamingData")
                    
                    // Formats (ভিডিও+অডিও) চেক করা
                    if (streamingData.has("formats")) {
                        val formats = streamingData.getJSONArray("formats")
                        for (i in 0 until formats.length()) {
                            val format = formats.getJSONObject(i)
                            
                            // যদি সরাসরি url থাকে
                            if (format.has("url")) {
                                directUrl = format.getString("url")
                                break // প্রথম ভালো কোয়ালিটি পেলেই লুপ ব্রেক
                            } else if (format.has("signatureCipher") || format.has("cipher")) {
                                // যদি এনক্রিপ্টেড সিগনেচার থাকে
                                isSignatureProtected = true
                            }
                        }
                    }
                    
                    // যদি formats এ না পাওয়া যায়, adaptiveFormats চেক করা (সাধারণত আলাদা অডিও/ভিডিও)
                    if (directUrl == null && streamingData.has("adaptiveFormats")) {
                        val adaptiveFormats = streamingData.getJSONArray("adaptiveFormats")
                        for (i in 0 until adaptiveFormats.length()) {
                            val format = adaptiveFormats.getJSONObject(i)
                            // mp4 ভিডিও ফরম্যাট খোঁজা
                            if (format.has("url") && format.has("mimeType") && format.getString("mimeType").contains("video/mp4")) {
                                directUrl = format.getString("url")
                                break
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return MediaInfo(
            title = title,
            description = if (directUrl != null) "Ready to download (Native)" else "Protected Content / Encrypted",
            thumbnailUrl = thumbnail,
            downloadUrl = directUrl ?: urlStr,
            platformIcon = Icons.Rounded.PlayArrow,
            isDirectVideo = directUrl != null,
            isEncrypted = directUrl == null && isSignatureProtected
        )
    }

    /**
     * অন্যান্য সাইট (Facebook, Instagram etc) এর জন্য জেনেরিক মেটা ট্যাগ পার্সার।
     */
    private fun extractGenericSocial(urlStr: String): MediaInfo? {
        val html = fetchHtml(urlStr) ?: return null

        val titleRegex = "<title>(.*?)</title>".toRegex()
        val ogVideoRegex = "meta property=\"og:video\" content=\"(.*?)\"".toRegex()
        val ogImageRegex = "meta property=\"og:image\" content=\"(.*?)\"".toRegex()
        val twitterPlayerRegex = "twitter:player:stream\" content=\"(.*?)\"".toRegex()

        var title = titleRegex.find(html)?.groupValues?.get(1) ?: "Social Video"
        val thumbnail = ogImageRegex.find(html)?.groupValues?.get(1) ?: ""
        
        // ডাইরেক্ট ভিডিও লিঙ্ক খোঁজা (OpenGraph অথবা Twitter Card)
        var videoUrl = ogVideoRegex.find(html)?.groupValues?.get(1)
        if (videoUrl == null) {
            videoUrl = twitterPlayerRegex.find(html)?.groupValues?.get(1)
        }

        // HTML Entity (&amp;) ক্লিন করা
        videoUrl = videoUrl?.replace("&amp;", "&")
        title = title.replace("&#39;", "'").replace("&amp;", "&")

        return MediaInfo(
            title = title,
            description = if (videoUrl != null) "Direct video found" else "Webpage detected",
            thumbnailUrl = thumbnail,
            downloadUrl = videoUrl ?: urlStr,
            platformIcon = if (urlStr.contains("facebook")) Icons.Rounded.VideoLibrary else Icons.Rounded.Link,
            isDirectVideo = videoUrl != null,
            isEncrypted = false
        )
    }

    /**
     * নেটওয়ার্ক কল হ্যান্ডেলার (HttpURLConnection)
     */
    private fun fetchHtml(urlStr: String): String? {
        return try {
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            
            // ব্রাউজারের মতো আচরণ করার জন্য User-Agent সেট করা জরুরি
            connection.setRequestProperty(
                "User-Agent", 
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
            )
            connection.connectTimeout = 15000 // 15 seconds
            connection.readTimeout = 15000
            connection.connect()

            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
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