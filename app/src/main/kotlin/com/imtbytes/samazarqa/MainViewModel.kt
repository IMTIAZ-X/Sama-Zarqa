package com.imtbytes.samazarqa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI-এর অবস্থা বোঝানোর জন্য একটি Sealed Interface
sealed interface UiState {
    data object Loading : UiState // স্প্ল্যাশ স্ক্রিন
    data class Home(val isSecure: Boolean) : UiState // হোম স্ক্রিন (সিকিউরিটি স্ট্যাটাস সহ)
}

class MainViewModel : ViewModel() {
    // শুরুতে অ্যাপ লোডিং অবস্থায় থাকবে
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        runSecurityCheck()
    }

    private fun runSecurityCheck() {
        // viewModelScope.launch মানে এটি ব্যাকগ্রাউন্ড থ্রেডে চলবে, UI আটকাবে না
        viewModelScope.launch(Dispatchers.Default) {
            // ১. ইউজারকে লোগো দেখানোর জন্য কৃত্রিম ডিলে (২ সেকেন্ড)
            delay(2000)

            // ২. ভারী সিকিউরিটি চেক রান করা (এখানে আপনার JADX ব্রেকারগুলো থাকবে)
            val isSecure = performHeavySecurityAlgos()

            // ৩. কাজ শেষ, এবার UI-কে হোম স্ক্রিনে পাঠাও
            _uiState.value = UiState.Home(isSecure = isSecure)
        }
    }

    // 🔥 আপনার JADX Breakers এবং সিকিউরিটি লজিক এখানে থাকবে
    // এটি ব্যাকগ্রাউন্ডে রান হবে তাই অ্যাপ ফ্রিজ হবে না
    private fun performHeavySecurityAlgos(): Boolean {
        // Time Bomb Check
        if (System.currentTimeMillis() < 0) return false

        // Fake Reflection Check to confuse decompilers
        try {
             val m = Class.forName("java.lang.String").getMethod("length")
             m.invoke("test")
        } catch (e: Exception) {
            // হ্যাকাররা কনফিউজড হবে, কিন্তু অ্যাপ ক্র্যাশ করবে না
        }

        // ধরলাম সব ঠিক আছে
        return true
    }
}