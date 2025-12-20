package com.example.gestodeestacionamento.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestodeestacionamento.domain.model.Vehicle
import com.example.gestodeestacionamento.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VehicleListUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class VehicleListViewModel(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehicleListUiState())
    val uiState: StateFlow<VehicleListUiState> = _uiState.asStateFlow()

    init {
        loadVehicles()
    }

    private fun loadVehicles() {
        viewModelScope.launch {
            vehicleRepository.getVehiclesInParkingLot().collect { vehicles ->
                _uiState.value = _uiState.value.copy(vehicles = vehicles)
            }
        }
    }
}

