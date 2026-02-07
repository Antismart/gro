package com.example.gro.ui.screen.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gro.data.local.datastore.UserPreferences
import com.example.gro.domain.model.WalletState
import com.example.gro.domain.repository.WalletRepository
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentPage: Int = 0,
    val isConnecting: Boolean = false,
    val connectionError: String? = null,
    val isConnected: Boolean = false,
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    fun setPage(page: Int) {
        _uiState.update { it.copy(currentPage = page) }
    }

    fun connectWallet(sender: ActivityResultSender) {
        Log.d(TAG, "connectWallet() tapped")
        viewModelScope.launch {
            _uiState.update { it.copy(isConnecting = true, connectionError = null) }
            val result = walletRepository.connect(sender)
            Log.d(TAG, "connect result: $result")
            when (result) {
                is WalletState.Connected -> {
                    userPreferences.setOnboardingCompleted()
                    _uiState.update { it.copy(isConnecting = false, isConnected = true) }
                }
                is WalletState.Error -> {
                    Log.e(TAG, "Connection error: ${result.message}")
                    _uiState.update { it.copy(isConnecting = false, connectionError = result.message) }
                }
                else -> {
                    Log.w(TAG, "Unexpected result: $result")
                    _uiState.update { it.copy(isConnecting = false) }
                }
            }
        }
    }

    companion object {
        private const val TAG = "OnboardingVM"
    }

    fun dismissError() {
        _uiState.update { it.copy(connectionError = null) }
    }
}
