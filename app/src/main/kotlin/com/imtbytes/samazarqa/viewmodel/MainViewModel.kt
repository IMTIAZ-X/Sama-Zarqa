package com.imtbytes.samazarqa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.imtbytes.samazarqa.data.AppPreferences
import com.imtbytes.samazarqa.data.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/**
 * UI State - শুধু isFirstLaunch flag যোগ করা হয়েছে
 */
sealed interface UiState {
    data object Loading : UiState
    data class Home(
        val isSecure: Boolean,
        val isFirstLaunch: Boolean
    ) : UiState
}

/**
 * Main ViewModel managing app-wide state and security
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val appPreferences = AppPreferences(application)
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    // থিম স্টেট এক্সপোজ করা হচ্ছে
    val appTheme = appPreferences.appTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppTheme.SYSTEM
    )
    
    private var isSecurityCheckPassed: Boolean = false

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            // Run security check in background
            launch(Dispatchers.Default) {
                isSecurityCheckPassed = performHeavySecurityAlgos()
            }
            
            val isFirstLaunch = !appPreferences.isOnboardingCompleted.first()
            _uiState.value = UiState.Home(
                isSecure = isSecurityCheckPassed,
                isFirstLaunch = isFirstLaunch
            )
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            appPreferences.setOnboardingCompleted()
            _uiState.value = UiState.Home(
                isSecure = isSecurityCheckPassed,
                isFirstLaunch = false
            )
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            appPreferences.setAppTheme(theme)
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

    private fun runJadxBreakers() {
        if (System.currentTimeMillis() < 0) {
            repeat(3) { /* unreachable noise */ }
        }
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
            // AppPreferences এ এই ফাংশনটি আনকমেন্ট করা হয়েছে
            appPreferences.resetOnboarding()
        }
    }
}