package com.example.gro.ui.screen.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gro.data.remote.SolanaRpcClient
import com.example.gro.domain.model.Plant
import com.example.gro.domain.repository.WalletRepository
import com.example.gro.domain.usecase.ObserveGardenUseCase
import com.example.gro.domain.usecase.SyncGardenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GardenUiState(
    val walletAddress: String? = null,
    val solBalance: Long = 0L,
    val plants: List<Plant> = emptyList(),
    val totalPortfolioValue: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedPlant: Plant? = null,
    val greeting: String = "Good morning",
)

@HiltViewModel
class GardenViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val solanaRpcClient: SolanaRpcClient,
    private val observeGardenUseCase: ObserveGardenUseCase,
    private val syncGardenUseCase: SyncGardenUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GardenUiState())
    val uiState: StateFlow<GardenUiState> = _uiState

    private val _disconnected = MutableStateFlow(false)
    val disconnected: StateFlow<Boolean> = _disconnected

    init {
        loadGarden()
    }

    fun loadGarden() {
        val address = walletRepository.getConnectedAddress() ?: return
        _uiState.update {
            it.copy(
                walletAddress = address,
                isLoading = true,
                error = null,
                greeting = timeBasedGreeting(),
            )
        }

        viewModelScope.launch {
            try {
                val balance = solanaRpcClient.getBalance(address)
                syncGardenUseCase(address)

                observeGardenUseCase(address).collect { plants ->
                    val portfolioValue = balance / 1_000_000_000.0
                    _uiState.update {
                        it.copy(
                            solBalance = balance,
                            plants = plants,
                            totalPortfolioValue = portfolioValue,
                            isLoading = false,
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load garden")
                }
            }
        }
    }

    fun selectPlant(plant: Plant) {
        _uiState.update { it.copy(selectedPlant = plant) }
    }

    fun dismissPlantDetail() {
        _uiState.update { it.copy(selectedPlant = null) }
    }

    fun disconnect() {
        viewModelScope.launch {
            walletRepository.disconnect()
            _disconnected.value = true
        }
    }

    private fun timeBasedGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }
}
