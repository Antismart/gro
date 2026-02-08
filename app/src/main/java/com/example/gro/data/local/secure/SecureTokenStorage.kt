package com.example.gro.data.local.secure

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureTokenStorage @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs: SharedPreferences = try {
        createEncryptedPrefs(context)
    } catch (e: Exception) {
        Log.e(TAG, "Encrypted prefs corrupted, clearing and retrying", e)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        try {
            context.deleteSharedPreferences(PREFS_NAME)
        } catch (_: Exception) { }
        createEncryptedPrefs(context)
    }

    private companion object {
        const val TAG = "SecureStorage"
        const val PREFS_NAME = "gro_secure_prefs"
        const val KEY_AUTH_TOKEN = "wallet_auth_token"

        fun createEncryptedPrefs(context: Context): SharedPreferences {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            return EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }
    }

    fun getAuthToken(): String? = prefs.getString(KEY_AUTH_TOKEN, null)

    fun setAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun clearAuthToken() {
        prefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }

}
