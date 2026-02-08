package com.example.gro.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.gro.data.local.secure.SecureTokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gro_preferences")

data class UserPreferencesData(
    val hasCompletedOnboarding: Boolean = false,
    val connectedWalletAddress: String? = null,
    val walletAuthToken: String? = null,
    val notificationsEnabled: Boolean = true,
)

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val secureTokenStorage: SecureTokenStorage,
) {
    private companion object {
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val CONNECTED_WALLET_ADDRESS = stringPreferencesKey("connected_wallet_address")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val userPreferencesFlow: Flow<UserPreferencesData> = dataStore.data.map { prefs ->
        UserPreferencesData(
            hasCompletedOnboarding = prefs[HAS_COMPLETED_ONBOARDING] ?: false,
            connectedWalletAddress = prefs[CONNECTED_WALLET_ADDRESS],
            walletAuthToken = secureTokenStorage.getAuthToken(),
            notificationsEnabled = prefs[NOTIFICATIONS_ENABLED] ?: true,
        )
    }

    suspend fun setOnboardingCompleted() {
        dataStore.edit { prefs ->
            prefs[HAS_COMPLETED_ONBOARDING] = true
        }
    }

    suspend fun setWalletConnection(address: String, authToken: String) {
        secureTokenStorage.setAuthToken(authToken)
        dataStore.edit { prefs ->
            prefs[CONNECTED_WALLET_ADDRESS] = address
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun clearWalletConnection() {
        secureTokenStorage.clearAuthToken()
        dataStore.edit { prefs ->
            prefs.remove(CONNECTED_WALLET_ADDRESS)
        }
    }
}
