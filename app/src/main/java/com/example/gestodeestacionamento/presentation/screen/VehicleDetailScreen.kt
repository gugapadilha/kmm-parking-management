package com.example.gestodeestacionamento.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestodeestacionamento.presentation.viewmodel.VehicleDetailViewModel
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.*

@Composable
fun VehicleDetailScreen(
    viewModel: VehicleDetailViewModel,
    vehicleId: Long,
    onNavigateBack: () -> Unit,
    onExitSuccess: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    var showExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(vehicleId) {
        viewModel.loadVehicle(vehicleId)
    }

    // Recalcular o valor a cada 30 segundos enquanto a tela estiver aberta
    LaunchedEffect(vehicleId) {
        while (true) {
            kotlinx.coroutines.delay(30000) // 30 segundos
            viewModel.recalculateAmount()
        }
    }

    LaunchedEffect(uiState.isExitSuccessful) {
        if (uiState.isExitSuccessful) {
            onExitSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 48.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Detalhes do Veículo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        uiState.vehicle?.let { vehicle ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Placa: ${vehicle.plate}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Modelo: ${vehicle.model}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cor: ${vehicle.color}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Entrada: ${viewModel.formatDateTime(vehicle.entryDateTime)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tabela: ${uiState.priceTable?.name ?: "Não encontrada"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (uiState.priceTable == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Valor Calculado",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currencyFormat.format(uiState.calculatedAmount),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Forma de Pagamento",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            uiState.paymentMethods.forEach { method ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = uiState.selectedPaymentMethodId == method.id,
                        onClick = { viewModel.selectPaymentMethod(method.id) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = method.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (uiState.paymentMethods.isEmpty()) {
                Text(
                    text = "Nenhuma forma de pagamento disponível",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showExitDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && 
                         uiState.selectedPaymentMethodId != null && 
                         uiState.calculatedAmount >= 0 &&
                         uiState.paymentMethods.isNotEmpty() &&
                         uiState.vehicle != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Dar Saída")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voltar")
            }
        } ?: run {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Text(
                    text = uiState.errorMessage ?: "Veículo não encontrado",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Confirmar Saída") },
            text = {
                Column {
                    Text("Tem certeza que deseja dar saída neste veículo?")
                    Spacer(modifier = Modifier.height(8.dp))
                    uiState.vehicle?.let { vehicle ->
                        Text(
                            text = "Placa: ${vehicle.plate}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Valor: ${currencyFormat.format(uiState.calculatedAmount)}",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.exitVehicle(onExitSuccess)
                        showExitDialog = false
                    }
                ) {
                    Text("Confirmar", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

