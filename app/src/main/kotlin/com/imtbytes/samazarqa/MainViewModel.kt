package com.imtbytes.samazarqa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UiState {
    data object Loading : UiState
    data class Home(val isSecure: Boolean) : UiState
}

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        runSecurityCheck()
    }

    private fun runSecurityCheck() {
        viewModelScope.launch(Dispatchers.Default) {
            // ১. ব্যাকগ্রাউন্ডে সিকিউরিটি লজিক রান করা
            val isSecure = performHeavySecurityAlgos()
            
            // ২. মিনিমাম ডিলে যাতে ইউজার ব্র্যান্ডিং দেখতে পারে
            delay(1500) 

            // ৩. স্টেট আপডেট (এটি UI-তে অ্যানিমেশন ট্রিগার করবে)
            _uiState.value = UiState.Home(isSecure = isSecure)
        }
    }

    private fun performHeavySecurityAlgos(): Boolean {
        return try {
            runJadxBreakers() // আপনার দেওয়া সিকিউরিটি লজিক
            true
        } catch (e: Exception) {
            false
        }
    }

    // =========================================================
    // 🔥 JADX BREAKERS & ANTI-DECOMPILE LOGIC
    // =========================================================
    private fun runJadxBreakers() {
        // 1. Time Bomb Trap
        if (System.currentTimeMillis() < 0) {
            while (true) { /* Emulator Trap */ }
        }

        // 2. Scary Logic (Safe but confuses analysts)
        if (1 == 2) {
            try { Runtime.getRuntime().exec("restart") } catch (e: Exception) {}
        }

        // 3. Reflection Invoke
        try {
            val m = Class.forName("java.lang.String").getMethod("valueOf", Int::class.java)
            m.invoke(null, 123)
        } catch (e: Exception) {}

        // 4. Decrypt Strings
        decryptString("ifmmp") 
        
        // 5. Bytecode Confusion
        confuseBytecode(5)
        
        // 6. Switch Bomb
        switchBomb()
    }

    private fun decryptString(s: String): String = s.map { it - 1 }.joinToString("")

    private fun confuseBytecode(x: Int): Int {
        return try { x } finally { return x }
    }

    private fun switchBomb() {
        when ((System.nanoTime() % 10).toInt()) {
            1 -> { delayDummy() }
            2 -> { delayDummy() }
            else -> {}
        }
    }
    
    private fun delayDummy() { /* Small noise */ }
}