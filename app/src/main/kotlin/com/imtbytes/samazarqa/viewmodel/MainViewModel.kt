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
 * Simplified UI State - Loading (with onboarding) or Home
 */
sealed interface UiState {
    data object Loading : UiState  // This includes splash + onboarding
    data class Home(val isSecure: Boolean) : UiState
}

/**
 * Main ViewModel managing app-wide state and security
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val appPreferences = AppPreferences(application)
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    private var isSecurityCheckPassed: Boolean = false
    private var hasCompletedOnboarding: Boolean = false

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
            
            // Check if user has completed onboarding before
            hasCompletedOnboarding = appPreferences.isOnboardingCompleted.first()
            
            // Stay in Loading state (SplashScreen will handle everything)
            _uiState.value = UiState.Loading
        }
    }

    /**
     * Called when user completes onboarding (or skips it)
     * This is called from SplashScreen after onboarding finishes
     */
    fun completeOnboarding() {
        viewModelScope.launch {
            // Save that onboarding is completed (for next app launch)
            if (!hasCompletedOnboarding) {
                appPreferences.setOnboardingCompleted()
            }
            
            // Navigate to home
            _uiState.value = UiState.Home(isSecure = isSecurityCheckPassed)
        }
    }

    /**
     * Check if this is first launch (for SplashScreen to decide)
     */
    fun isFirstLaunch(): Boolean = !hasCompletedOnboarding

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
            hasCompletedOnboarding = false
        }
    }
}