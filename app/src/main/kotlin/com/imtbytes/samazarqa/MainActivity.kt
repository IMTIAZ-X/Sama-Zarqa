package com.imtbytes.samazarqa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.imtbytes.samazarqa.ui.theme.SamazarqaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            var currentScreen by remember { mutableStateOf("splash") }
            var isDarkTheme by remember { mutableStateOf(false) }
            var isSystemSecure by remember { mutableStateOf(true) }

            SamazarqaTheme(darkTheme = isDarkTheme) {
                // MainActivity content must be inside a Composable
                if (currentScreen == "splash") {
                    SplashScreen(onTimeout = { 
                        // ব্যাকগ্রাউন্ডে চেক রান করা হচ্ছে
                        isSystemSecure = securityWarzone()
                        currentScreen = "home" 
                    })
                } else {
                    HomeScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme },
                        isSecure = isSystemSecure
                    )
                }
            }
        }
    }

    // 🔥 ব্যাকগ্রাউন্ড সিকিউরিটি চেক লজিক
    private fun securityWarzone(): Boolean {
        // ১. JADX Breakers
        if (System.currentTimeMillis() < 0) return false
        
        // ২. Fake reflection check
        return try {
            Class.forName("java.lang.String").getMethod("length")
            true // যদি সব ঠিক থাকে
        } catch (e: Exception) {
            false
        }
    }
}