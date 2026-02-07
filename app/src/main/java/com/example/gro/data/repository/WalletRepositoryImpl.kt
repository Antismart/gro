package com.example.gro.data.repository

import android.content.Context
import android.os.PowerManager
import android.util.Log
import com.example.gro.data.local.datastore.UserPreferences
import com.example.gro.domain.model.WalletState
import com.example.gro.domain.repository.WalletRepository
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.TransactionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences,
    private val mobileWalletAdapter: MobileWalletAdapter,
    @ApplicationContext private val context: Context,
) : WalletRepository {

    private val _walletState = MutableStateFlow<WalletState>(WalletState.Disconnected)
    override val walletState: Flow<WalletState> = _walletState

    init {
        val prefs = runBlocking { userPreferences.userPreferencesFlow.first() }
        if (prefs.connectedWalletAddress != null && prefs.walletAuthToken != null) {
            _walletState.value = WalletState.Connected(
                publicKey = prefs.connectedWalletAddress,
                authToken = prefs.walletAuthToken,
            )
            mobileWalletAdapter.authToken = prefs.walletAuthToken
        }
    }

    override suspend fun connect(sender: ActivityResultSender): WalletState {
        Log.d(TAG, "connect() called")
        _walletState.value = WalletState.Connecting

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "gro:mwa-transact",
        )
        wakeLock.acquire(120_000L) // 2 minute max

        return try {
            Log.d(TAG, "Calling transact (wakeLock acquired)...")
            val result = mobileWalletAdapter.transact(sender) { authResult ->
                Log.d(TAG, "Inside transact block, authResult=$authResult")
                authResult
            }
            Log.d(TAG, "transact returned: $result")

            when (result) {
                is TransactionResult.Success -> {
                    val authResult = result.authResult
                    val publicKeyBytes = authResult.accounts.first().publicKey
                    val pubKey = org.sol4k.PublicKey(publicKeyBytes).toBase58()
                    val token = authResult.authToken
                    Log.d(TAG, "Connected: pubKey=$pubKey")
                    userPreferences.setWalletConnection(pubKey, token)
                    val state = WalletState.Connected(pubKey, token)
                    _walletState.value = state
                    state
                }
                is TransactionResult.NoWalletFound -> {
                    Log.w(TAG, "No wallet found: ${result.message}")
                    val state = WalletState.Error("No Solana wallet found. Please install Phantom.")
                    _walletState.value = state
                    state
                }
                is TransactionResult.Failure -> {
                    Log.e(TAG, "Transact failure: ${result.message}", result.e)
                    val state = WalletState.Error(result.message)
                    _walletState.value = state
                    state
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception connecting wallet", e)
            val state = WalletState.Error(e.message ?: "Unexpected error connecting wallet")
            _walletState.value = state
            state
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
                Log.d(TAG, "WakeLock released")
            }
        }
    }

    override suspend fun disconnect() {
        userPreferences.clearWalletConnection()
        mobileWalletAdapter.authToken = null
        _walletState.value = WalletState.Disconnected
    }

    override fun getConnectedAddress(): String? {
        return (_walletState.value as? WalletState.Connected)?.publicKey
    }

    companion object {
        private const val TAG = "WalletRepo"
    }
}
