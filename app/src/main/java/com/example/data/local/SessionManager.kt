package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore Extension on Context
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "resync_session_prefs")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_SCANS_COUNT = intPreferencesKey("scans_count")
        private val KEY_HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        private val KEY_THEME_PREFERENCE = stringPreferencesKey("theme_preference")
        private val KEY_PUSH_NOTIFICATIONS = booleanPreferencesKey("push_notifications")
        private val KEY_EMAIL_ALERTS = booleanPreferencesKey("email_alerts")
        private val KEY_USER_INSTITUTION = stringPreferencesKey("user_institution")
        private val KEY_USER_ROLE = stringPreferencesKey("user_role")
        private val KEY_USER_BIO = stringPreferencesKey("user_bio")
    }

    // Theme Preference Flow ("system", "light", "dark")
    val themePreference: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME_PREFERENCE] ?: "system"
    }

    suspend fun setThemePreference(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_PREFERENCE] = theme
        }
    }

    // Push Notifications Flow
    val pushNotificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_PUSH_NOTIFICATIONS] ?: true
    }

    suspend fun setPushNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PUSH_NOTIFICATIONS] = enabled
        }
    }

    // Email Alerts Flow
    val emailAlertsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_EMAIL_ALERTS] ?: true
    }

    suspend fun setEmailAlertsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_EMAIL_ALERTS] = enabled
        }
    }

    // Onboarding State Flow
    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_HAS_COMPLETED_ONBOARDING] ?: false
    }

    // Save Onboarding Status
    suspend fun setCompletedOnboarding(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_HAS_COMPLETED_ONBOARDING] = completed
        }
    }

    // Auth Token Flow
    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_AUTH_TOKEN]
    }

    // User Email Flow
    val userEmail: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USER_EMAIL] ?: "noelhenrymier@gmail.com" // Default to user email from metadata
    }

    // User Name Flow
    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USER_NAME] ?: "Noel Henry"
    }

    // Login State Flow
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_IS_LOGGED_IN] ?: false
    }

    // Total Scans Completed Flow
    val scansCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_SCANS_COUNT] ?: 0
    }

    // User Academic Institution Flow
    val userInstitution: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USER_INSTITUTION] ?: "Stanford University"
    }

    // User Academic Role Flow
    val userRole: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USER_ROLE] ?: "Principal Investigator"
    }

    // User Bio Flow
    val userBio: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[KEY_USER_BIO] ?: "Specializing in distributed systems coherence and academic reference validation systems."
    }

    // Save Session Details
    suspend fun saveSession(
        token: String,
        email: String,
        name: String,
        institution: String? = null,
        role: String? = null,
        bio: String? = null
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = token
            preferences[KEY_USER_EMAIL] = email
            preferences[KEY_USER_NAME] = name
            if (institution != null) preferences[KEY_USER_INSTITUTION] = institution
            if (role != null) preferences[KEY_USER_ROLE] = role
            if (bio != null) preferences[KEY_USER_BIO] = bio
            preferences[KEY_IS_LOGGED_IN] = true
        }
    }

    // Increment Scans Completed
    suspend fun incrementScansCount() {
        context.dataStore.edit { preferences ->
            val current = preferences[KEY_SCANS_COUNT] ?: 0
            preferences[KEY_SCANS_COUNT] = current + 1
        }
    }

    // Clear Session
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = ""
            preferences[KEY_USER_EMAIL] = ""
            preferences[KEY_USER_NAME] = ""
            preferences[KEY_USER_INSTITUTION] = ""
            preferences[KEY_USER_ROLE] = ""
            preferences[KEY_USER_BIO] = ""
            preferences[KEY_IS_LOGGED_IN] = false
            preferences[KEY_SCANS_COUNT] = 0
        }
    }
}
