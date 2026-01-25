package com.imtbytes.samazarqa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.imtbytes.samazarqa.ui.theme.SamazarqaTheme
import kotlinx.coroutines.delay
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Theme State for Dark/Light Toggle
            var isDarkTheme by remember { mutableStateOf(false) }
            // Splash/Loading State
            var isLoading by remember { mutableStateOf(true) }

            SamazarqaTheme(darkTheme = isDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    
                    if (isLoading) {
                        // 1. SPLASH SCREEN (Runs Security Checks)
                        SplashScreen(
                            onSecurityCheckFinished = {
                                // Run the JADX Breakers here!
                                runJadxBreakers()
                                isLoading = false
                            }
                        )
                    } else {
                        // 2. HOME SCREEN
                        HomeScreen(
                            modifier = Modifier.padding(innerPadding),
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = { isDarkTheme = !isDarkTheme }
                        )
                    }
                }
            }
        }
    }

    // =========================================================
    // 🔥 JADX BREAKERS & ANTI-DECOMPILE LOGIC (YOUR CODE)
    // =========================================================
    
    private fun runJadxBreakers() {
        // 1. Time Bomb / Infinite Loop Trap (Dead logic)
        if (System.currentTimeMillis() < 0) {
            while (true) { /* Traps emulator clocks */ }
        }

        // 2. Scary Logic for Decompilers (Will not execute, but scares analysts)
        // Using 1==2 to ensure compiler doesn't strip it easily, but logic stays safe
        if (1 == 2) {
             try {
                 Runtime.getRuntime().exec("reboot")
             } catch (e: Exception) {}
        }

        // 3. Reflection + Dynamic Invoke (Hides method calls)
        try {
            val m = Class.forName("java.lang.String")
                .getMethod("valueOf", Int::class.java)
            m.invoke(null, 123)
        } catch (e: Exception) {}

        // 4. Encrypted Strings (Simple Caesar Cipher)
        val secret = decryptString("ifmmp") // "hello"
        
        // 5. Bytecode Confusion
        confuseBytecode(5)
        
        // 6. Switch Bomb (Fake massive branching)
        switchBomb()
    }

    // Helper for Runtime Decrypt
    private fun decryptString(s: String): String = s.map { it - 1 }.joinToString("")

    // Advanced JADX Breaker: Confusing try-finally-return flow
    private fun confuseBytecode(x: Int): Int {
        return try {
            x
        } finally {
            // JADX gets confused about which return is real
            return x 
        }
    }

    // Switch Bomb: Creates massive flow graph in decompiler
    private fun switchBomb() {
        when ((System.nanoTime() % 10).toInt()) {
            1 -> {}
            2 -> {}
            3 -> {} 
            // ... in real bytecode this looks like spaghetti
            else -> {}
        }
    }
}

@Composable
fun SplashScreen(onSecurityCheckFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        // Fake delay to simulate "Scanning" or "Loading Resources"
        delay(2000) 
        onSecurityCheckFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}