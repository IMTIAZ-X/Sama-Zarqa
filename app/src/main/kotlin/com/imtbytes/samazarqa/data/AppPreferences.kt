package com.imtbytes.samazarqa.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ডাটাস্টোর ইনিশিয়ালাইজেশন
private val Context.dataStore by preferencesDataStore(name = "settings")

class AppPreferences(private val context: Context) {
    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    // অনবোর্ডিং হয়েছে কি না তা চেক করার ফ্লো
    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }

    // অনবোর্ডিং শেষ হলে ট্রু (true) সেভ করার ফাংশন
    suspend fun saveOnboardingStatus(isCompleted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = isCompleted
        }
    }
}
