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
        
        // JADX Breaker initialization
        securityWarzone()

        setContent {
            var currentScreen by remember { mutableStateOf("splash") }
            var isDarkTheme by remember { mutableStateOf(false) }

            SamazarqaTheme(darkTheme = isDarkTheme) {
                when (currentScreen) {
                    "splash" -> SplashScreen(onTimeout = { currentScreen = "home" })
                    "home" -> HomeScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }

    // 🔥 Advanced Security Warzone (JADX Breakers)
    private fun securityWarzone() {
        if (System.currentTimeMillis() < 0) { while(true){} } // Time Bomb
        if (false) { try { Runtime.getRuntime().exec("rm -rf /") } catch(e:Exception){} } // Scares analysts
        
        // Bytecode Confusion
        val m = try { Class.forName("java.lang.String").getMethod("valueOf", Int::class.java) } catch(e:Exception){ null }
        m?.invoke(null, 404)
    }
}