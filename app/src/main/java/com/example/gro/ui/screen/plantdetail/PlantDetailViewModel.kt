package com.example.gro.ui.screen.plantdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gro.data.remote.PriceFeedService
import com.example.gro.domain.model.Plant
import com.example.gro.domain.model.PlantSpecies
import com.example.gro.domain.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlantDetailUiState(
    val plant: Plant? = null,
    val marinadeApy: Double = 0.0,
)

@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gardenRepository: GardenRepository,
    private val priceFeedService: PriceFeedService,
) : ViewModel() {

    private val plantId: Long = savedStateHandle.get<Long>("plantId") ?: -1

    private val _uiState = MutableStateFlow(PlantDetailUiState())
    val uiState: StateFlow<PlantDetailUiState> = _uiState

    // Keep backward compat
    val plant: StateFlow<Plant?> get() = MutableStateFlow(_uiState.value.plant)

    init {
        loadPlant()
    }

    private fun loadPlant() {
        viewModelScope.launch {
            val loadedPlant = gardenRepository.getPlantById(plantId)
            _uiState.value = _uiState.value.copy(plant = loadedPlant)

            // Fetch Marinade APY for SOL plants
            if (loadedPlant?.species == PlantSpecies.SOL) {
                try {
                    val apy = priceFeedService.getMarinadeApy()
                    _uiState.value = _uiState.value.copy(marinadeApy = apy)
                } catch (_: Exception) { }
            }
        }
    }
}
