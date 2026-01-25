package com.imtbytes.samazarqa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding // এই ইমপোর্টটি মিসিং ছিল
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
import com.imtbytes.samazarqa.ui.theme.MyApplicationTheme // আপনার থিম প্যাকেজ চেক করুন
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            var isLoading by remember { mutableStateOf(true) }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isLoading) {
                        SplashScreen(
                            onSecurityCheckFinished = {
                                runJadxBreakers()
                                isLoading = false
                            }
                        )
                    } else {
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

    private fun runJadxBreakers() {
        if (System.currentTimeMillis() < 0) { while (true) {} }
        
        // Safe dummy check
        if (1 == 2) {
             try { Runtime.getRuntime().exec("reboot") } catch (e: Exception) {}
        }

        try {
            val m = Class.forName("java.lang.String").getMethod("valueOf", Int::class.java)
            m.invoke(null, 123)
        } catch (e: Exception) {}
    }
}

@Composable
fun SplashScreen(onSecurityCheckFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000) 
        onSecurityCheckFinished()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}