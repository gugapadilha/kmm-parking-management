package com.example.gestodeestacionamento.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestodeestacionamento.presentation.viewmodel.HomeViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToVehicleEntry: () -> Unit,
    onNavigateToVehicleList: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    var showCloseSessionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 48.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Jump Park",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = { viewModel.syncData() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Sincronizar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Card de veículos no pátio
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
                    text = "Veículos no Pátio",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${uiState.vehiclesInParkingLotCount}",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card de pagamentos por forma de pagamento
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Pagamentos por Forma",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                uiState.paymentsByMethod.forEach { payment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = payment.paymentMethodName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = currencyFormat.format(payment.total),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                if (uiState.paymentsByMethod.isEmpty()) {
                    Text(
                        text = "Nenhum pagamento registrado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Divider()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currencyFormat.format(uiState.totalPayments),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botões de ação
        Button(
            onClick = onNavigateToVehicleEntry,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Entrada de Veículo")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onNavigateToVehicleList,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lista de Veículos no Pátio")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { showCloseSessionDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Encerrar Sessão")
        }

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    if (showCloseSessionDialog) {
        AlertDialog(
            onDismissRequest = { showCloseSessionDialog = false },
            title = { Text("Encerrar Sessão") },
            text = { Text("Tem certeza que deseja encerrar a sessão? Todos os dados locais serão apagados.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.closeSession(onLogout)
                        showCloseSessionDialog = false
                    }
                ) {
                    Text("Confirmar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloseSessionDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

