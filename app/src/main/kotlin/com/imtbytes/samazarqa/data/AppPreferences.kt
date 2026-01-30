package com.imtbytes.samazarqa.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Modern DataStore-based preferences manager
 * Handles onboarding completion state with reactive flows
 */
class AppPreferences(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = "samazarqa_preferences"
        )
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        
        private val APP_THEME = stringPreferencesKey("app_theme")
    }

    /**
     * Reactive flow for onboarding completion state
     * Emits true if user has completed onboarding, false otherwise
     */
    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ONBOARDING_COMPLETED] ?: false
        }

    /**
     * Mark onboarding as completed
     * Called when user finishes or skips onboarding
     */
    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = true
        }
    }
    
    
    val appTheme: Flow<AppTheme> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[APP_THEME] ?: AppTheme.SYSTEM.name
            try {
                AppTheme.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                AppTheme.SYSTEM
            }
        }
   
    suspend fun setAppTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[APP_THEME] = theme.name
        }
    }

    /**
     * Reset onboarding state (for testing/debugging)
     */
    
    suspend fun resetOnboarding() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = false
        }
    }
}
