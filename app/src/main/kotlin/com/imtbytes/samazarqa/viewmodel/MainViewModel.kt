package com.imtbytes.samazarqa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.app.Application
import com.imtbytes.samazarqa.data.AppPreferences
import kotlinx.coroutines.flow.first

sealed interface UiState {
    data object Loading : UiState
    data class Home(val isSecure: Boolean) : UiState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = AppPreferences(application)
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    private var isSecurityCheckPassed: Boolean = false

    init {
        runSecurityProcess()
        
        viewModelScope.launch {
            val isCompleted = prefs.isOnboardingCompleted.first()
            if (isCompleted) {
                _uiState.value = UiState.Home(isSecure = isSecurityCheckPassed)
            }
        }
    }
    
        fun completeOnboarding() {
        viewModelScope.launch {
            prefs.saveOnboardingStatus(true)
            _uiState.value = UiState.Home(isSecure = isSecurityCheckPassed)
        }
    }
/*
    private fun runSecurityProcess() {
        viewModelScope.launch(
            Dispatchers.Default.limitedParallelism(1)
        ) {
            val secure = performHeavySecurityAlgos()
           // delay(1500)
           // _uiState.value = UiState.Home(isSecure = secure)
           isSecurityCheckPassed = secure 
        }
    }
    */
     private fun runSecurityProcess() {
        viewModelScope.launch(Dispatchers.Default.limitedParallelism(1)) {
            isSecurityCheckPassed = performHeavySecurityAlgos()
        }
    }
    
    
    fun navigateToHome() {
         _uiState.value = UiState.Home(isSecure = isSecurityCheckPassed)
      }

    private fun performHeavySecurityAlgos(): Boolean {
        return try {
            runJadxBreakers()
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Obfuscation & anti-analysis routines.
     * Designed to be ANR-safe and optimizer-resistant.
     */
    private fun runJadxBreakers() {

        // Fake impossible branch (safe, non-blocking)
        if (System.currentTimeMillis() < 0) {
            repeat(3) { /* unreachable noise */ }
        }

        // Reflection confusion
        try {
            val m = Class.forName("java.lang.String")
                .getMethod("valueOf", Int::class.java)
            m.invoke(null, 123)
        } catch (_: Exception) { }

        decryptString("ifmmp")
        confuseBytecode(5)
        switchBomb()
    }

    private fun decryptString(input: String): String =
        input.map { it - 1 }.joinToString("")

    private fun confuseBytecode(x: Int): Int =
        try { x } finally { x }

    private fun switchBomb() {
        when ((System.nanoTime() % 7).toInt()) {
            1, 3 -> { /* no-op security noise */ }
            else -> Unit
        }
    }
}
