package com.example.gro.ui.screen.deposit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gro.domain.model.PlantSpecies
import com.example.gro.domain.repository.DepositResult
import com.example.gro.domain.repository.WalletRepository
import com.example.gro.domain.usecase.DepositUseCase
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DepositUiState(
    val amountSol: Double = 0.0,
    val amountLamports: Long = 0,
    val customAmountText: String = "",
    val isSubmitting: Boolean = false,
    val depositSuccess: Boolean = false,
    val error: String? = null,
    val showAnimation: Boolean = false,
    val selectedSpecies: PlantSpecies = PlantSpecies.SOL,
)

@HiltViewModel
class DepositViewModel @Inject constructor(
    private val depositUseCase: DepositUseCase,
    private val walletRepository: WalletRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DepositUiState())
    val uiState: StateFlow<DepositUiState> = _uiState

    val availableSpecies: List<PlantSpecies> = PlantSpecies.entries

    fun selectSpecies(species: PlantSpecies) {
        _uiState.update { it.copy(selectedSpecies = species) }
    }

    fun selectPresetAmount(sol: Double) {
        val lamports = (sol * 1_000_000_000).toLong()
        _uiState.update { it.copy(amountSol = sol, amountLamports = lamports, customAmountText = "") }
    }

    fun setCustomAmount(text: String) {
        val sol = text.toDoubleOrNull() ?: 0.0
        val lamports = (sol * 1_000_000_000).toLong()
        _uiState.update { it.copy(customAmountText = text, amountSol = sol, amountLamports = lamports) }
    }

    fun submitDeposit(sender: ActivityResultSender) {
        val address = walletRepository.getConnectedAddress() ?: return
        val lamports = _uiState.value.amountLamports
        val species = _uiState.value.selectedSpecies
        if (lamports <= 0) return

        _uiState.update { it.copy(isSubmitting = true, error = null) }

        viewModelScope.launch {
            val result = depositUseCase(sender, address, lamports, species)
            when (result) {
                is DepositResult.Success -> {
                    _uiState.update {
                        it.copy(isSubmitting = false, depositSuccess = true, showAnimation = true)
                    }
                }
                is DepositResult.Error -> {
                    _uiState.update {
                        it.copy(isSubmitting = false, error = result.message)
                    }
                }
            }
        }
    }

    fun dismissAnimation() {
        _uiState.update { it.copy(showAnimation = false) }
    }
}
