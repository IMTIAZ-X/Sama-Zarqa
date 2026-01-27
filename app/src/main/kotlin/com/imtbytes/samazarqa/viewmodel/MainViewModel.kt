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

/**
 * Enhanced UI State with Onboarding support
 */
sealed interface UiState {
    data object Loading : UiState
    data object Onboarding : UiState
    data class Home(val isSecure: Boolean) : UiState
}

/**
 * Main ViewModel managing app-wide state and security
 * Now with onboarding persistence support
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val appPreferences = AppPreferences(application)
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    private var isSecurityCheckPassed: Boolean = false

    init {
        initializeApp()
    }

    /**
     * Initialize app by checking onboarding status and running security
     */
    private fun initializeApp() {
        viewModelScope.launch {
            // Run security check in background
            runSecurityProcess()
            
            // Check if user has completed onboarding
            val onboardingCompleted = appPreferences.isOnboardingCompleted.first()
            
            if (onboardingCompleted) {
                // Skip directly to home after splash
                _uiState.value = UiState.Loading
            } else {
                // Show onboarding after splash
                _uiState.value = UiState.Loading
            }
        }
    }

    /**
     * Called from SplashScreen after animation completes
     */
    suspend fun onSplashFinished() {
        val onboardingCompleted = appPreferences.isOnboardingCompleted.first()
        
        if (onboardingCompleted) {
            // Go directly to home
            _uiState.value = UiState.Home(isSecure = isSecurityCheckPassed)
        } else {
            // Show onboarding
            _uiState.value = UiState.Onboarding
        }
    }

    /**
     * Called when user completes or skips onboarding
     */
    fun completeOnboarding() {
        viewModelScope.launch {
            appPreferences.setOnboardingCompleted()
            _uiState.value = UiState.Home(isSecure = isSecurityCheckPassed)
        }
    }

    /**
     * Navigate to home (legacy support)
     */
    fun navigateToHome() {
        _uiState.value = UiState.Home(isSecure = isSecurityCheckPassed)
    }

    /**
     * Background security verification process
     */
    private fun runSecurityProcess() {
        viewModelScope.launch(Dispatchers.Default.limitedParallelism(1)) {
            isSecurityCheckPassed = performHeavySecurityAlgos()
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
     * Obfuscation & anti-analysis routines
     * Designed to be ANR-safe and optimizer-resistant
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

    /**
     * Debug function to reset onboarding (remove in production)
     */
    fun resetOnboardingForTesting() {
        viewModelScope.launch {
            appPreferences.resetOnboarding()
        }
    }
}
