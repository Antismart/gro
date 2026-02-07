package com.example.gro.ui.screen.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gro.data.remote.SolanaRpcClient
import com.example.gro.data.remote.dto.TokenAccountInfo
import com.example.gro.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GardenUiState(
    val walletAddress: String? = null,
    val solBalance: Long = 0L,
    val tokenAccounts: List<TokenAccountInfo> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class GardenViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val solanaRpcClient: SolanaRpcClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GardenUiState())
    val uiState: StateFlow<GardenUiState> = _uiState

    init {
        loadGarden()
    }

    fun loadGarden() {
        val address = walletRepository.getConnectedAddress() ?: return
        _uiState.update { it.copy(walletAddress = address, isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val balance = solanaRpcClient.getBalance(address)
                val tokens = solanaRpcClient.getTokenAccounts(address)
                _uiState.update {
                    it.copy(
                        solBalance = balance,
                        tokenAccounts = tokens,
                        isLoading = false,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load balances",
                    )
                }
            }
        }
    }

    private val _disconnected = MutableStateFlow(false)
    val disconnected: StateFlow<Boolean> = _disconnected

    fun disconnect() {
        viewModelScope.launch {
            walletRepository.disconnect()
            _disconnected.value = true
        }
    }
}
