package com.example.gro.ui.screen.visit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gro.domain.model.Plant
import com.example.gro.domain.usecase.VisitGardenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VisitUiState(
    val friendAddress: String = "",
    val plants: List<Plant> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasVisited: Boolean = false,
)

@HiltViewModel
class VisitGardenViewModel @Inject constructor(
    private val visitGardenUseCase: VisitGardenUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VisitUiState())
    val uiState: StateFlow<VisitUiState> = _uiState

    fun updateAddress(address: String) {
        _uiState.update { it.copy(friendAddress = address) }
    }

    fun visitGarden() {
        val address = _uiState.value.friendAddress.trim()
        if (address.isBlank()) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val plants = visitGardenUseCase(address)
                _uiState.update {
                    it.copy(
                        plants = plants,
                        isLoading = false,
                        hasVisited = true,
                        error = if (plants.isEmpty()) "This garden is empty" else null,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Couldn't find that garden")
                }
            }
        }
    }
}
