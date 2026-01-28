package com.imtbytes.samazarqa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imtbytes.samazarqa.data.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// FIX: Updated UiState to include isFirstLaunch flag
sealed interface UiState {
    data object Loading : UiState
    data class Home(
        val isSecure: Boolean,
        val isFirstLaunch: Boolean // FIX: Added to track first launch
    ) : UiState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // FIX: Added AppPreferences for DataStore
    private val appPreferences = AppPreferences(application)

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    private var isSecurityCheckPassed: Boolean = false

    init {
        runSecurityProcess()
    }

    private fun runSecurityProcess() {
        viewModelScope.launch(Dispatchers.Default.limitedParallelism(1)) {
            val secure = performHeavySecurityAlgos()
            isSecurityCheckPassed = secure
            
            // FIX: Check DataStore for first launch status
            val isFirstLaunch = !appPreferences.isOnboardingCompleted.first()
            
            // FIX: Pass isFirstLaunch to Home state
            _uiState.value = UiState.Home(
                isSecure = secure,
                isFirstLaunch = isFirstLaunch
            )
        }
    }
    
    // FIX: This is called when onboarding is finished
    fun navigateToHome() {
        viewModelScope.launch {
            // FIX: Save that onboarding is completed
            appPreferences.setOnboardingCompleted()
            
            // FIX: Update state with isFirstLaunch = false
            _uiState.value = UiState.Home(
                isSecure = isSecurityCheckPassed,
                isFirstLaunch = false
            )
        }
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
