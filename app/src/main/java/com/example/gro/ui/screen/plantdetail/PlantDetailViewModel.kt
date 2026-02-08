package com.example.gro.ui.screen.plantdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gro.domain.model.Plant
import com.example.gro.domain.repository.GardenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlantDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gardenRepository: GardenRepository,
) : ViewModel() {

    private val plantId: Long = savedStateHandle.get<Long>("plantId") ?: -1

    private val _plant = MutableStateFlow<Plant?>(null)
    val plant: StateFlow<Plant?> = _plant

    init {
        loadPlant()
    }

    private fun loadPlant() {
        viewModelScope.launch {
            _plant.value = gardenRepository.getPlantById(plantId)
        }
    }
}
