package com.imtbytes.samazarqa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UiState {
    data object Loading : UiState
    data class Home(val isSecure: Boolean) : UiState
}

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        runSecurityProcess()
    }

    private fun runSecurityProcess() {
        viewModelScope.launch(Dispatchers.Default) {
            // আপনার দেওয়া সব সিকিউরিটি লজিক রান হচ্ছে
            val result = performHeavySecurityAlgos()
            delay(2000) // ২ সেকেন্ড স্প্ল্যাশ ডিলে
            _uiState.value = UiState.Home(isSecure = result)
        }
    }

    private fun performHeavySecurityAlgos(): Boolean {
        return try {
            runJadxBreakers()
            true
        } catch (e: Exception) {
            false
        }
    }

    // 🔥 আপনার দেওয়া JADX BREAKERS (সবগুলো অক্ষত রাখা হয়েছে)
    private fun runJadxBreakers() {
        if (System.currentTimeMillis() < 0) {
            while (true) { /* Trap */ }
        }

        if (1 == 2) {
            try { Runtime.getRuntime().exec("restart") } catch (e: Exception) {}
        }

        try {
            val m = Class.forName("java.lang.String").getMethod("valueOf", Int::class.java)
            m.invoke(null, 123)
        } catch (e: Exception) {}

        decryptString("ifmmp")
        confuseBytecode(5)
        switchBomb()
    }

    private fun decryptString(s: String): String = s.map { it - 1 }.joinToString("")

    private fun confuseBytecode(x: Int): Int {
        return try { x } finally { return x }
    }

    private fun switchBomb() {
        when ((System.nanoTime() % 10).toInt()) {
            1 -> { /* logic */ }
            2 -> { /* logic */ }
            else -> {}
        }
    }
}
