package com.example.gestodeestacionamento.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestodeestacionamento.domain.model.PaymentMethodTotal
import com.example.gestodeestacionamento.domain.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val vehiclesInParkingLotCount: Int = 0,
    val paymentsByMethod: List<PaymentMethodTotal> = emptyList(),
    val totalPayments: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSessionClosing: Boolean = false
)

class HomeViewModel(
    private val vehicleRepository: VehicleRepository,
    private val paymentRepository: PaymentRepository,
    private val priceTableRepository: PriceTableRepository,
    private val syncRepository: SyncRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        syncData() // Sincronizar automaticamente ao entrar na tela
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            combine(
                vehicleRepository.getVehiclesInParkingLotCount(),
                paymentRepository.getPaymentsGroupedByMethod(),
                paymentRepository.getTotalPayments()
            ) { count, payments, total ->
                HomeUiState(
                    vehiclesInParkingLotCount = count,
                    paymentsByMethod = payments,
                    totalPayments = total,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun syncData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val user = authRepository.getCurrentUser()
            if (user == null || user.establishmentId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Usuário não autenticado"
                )
                return@launch
            }

            syncRepository.manualLoad(user.id, user.establishmentId, user.token)
                .fold(
                    onSuccess = { result ->
                        android.util.Log.d("HomeViewModel", "Sync success: priceTables=${result.priceTables.size}, paymentMethods=${result.paymentMethods.size}")
                        // Salvar dados localmente
                        try {
                            priceTableRepository.insertAllPriceTables(result.priceTables)
                            android.util.Log.d("HomeViewModel", "Price tables saved: ${result.priceTables.size}")
                            paymentRepository.insertAllPaymentMethods(result.paymentMethods)
                            android.util.Log.d("HomeViewModel", "Payment methods saved: ${result.paymentMethods.size}")
                            android.util.Log.d("HomeViewModel", "Data saved to database")
                        } catch (e: Exception) {
                            android.util.Log.e("HomeViewModel", "Error saving data to database", e)
                        }
                        // Salvar sessionId se recebido
                        result.sessionId?.let { sessionId ->
                            authRepository.saveSessionId(sessionId)
                        }
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    },
                    onFailure = { exception ->
                        android.util.Log.e("HomeViewModel", "Sync failed", exception)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erro ao sincronizar dados"
                        )
                    }
                )
        }
    }

    fun closeSession(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSessionClosing = true, errorMessage = null)
            
            val user = authRepository.getCurrentUser()
            if (user == null || user.establishmentId == null || user.sessionId == null) {
                _uiState.value = _uiState.value.copy(
                    isSessionClosing = false,
                    errorMessage = "Sessão não encontrada"
                )
                return@launch
            }

            syncRepository.closeSession(user.id, user.establishmentId, user.sessionId, user.token)
                .fold(
                    onSuccess = {
                        // Limpar dados locais
                        vehicleRepository.deleteAllVehicles()
                        paymentRepository.deleteAllPayments()
                        paymentRepository.deleteAllPaymentMethods()
                        authRepository.logout()
                        _uiState.value = _uiState.value.copy(isSessionClosing = false)
                        onSuccess()
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isSessionClosing = false,
                            errorMessage = exception.message ?: "Erro ao encerrar sessão"
                        )
                    }
                )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

