package com.example.gestodeestacionamento.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gestodeestacionamento.presentation.viewmodel.VehicleEntryViewModel

@Composable
fun VehicleEntryScreen(
    viewModel: VehicleEntryViewModel,
    onNavigateBack: () -> Unit,
    onEntrySuccess: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 48.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Entrada de Veículo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.plate,
            onValueChange = viewModel::updatePlate,
            label = { Text("Placa") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.model,
            onValueChange = viewModel::updateModel,
            label = { Text("Modelo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.color,
            onValueChange = viewModel::updateColor,
            label = { Text("Cor") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tabela de Preços",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        uiState.priceTables.forEach { table ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = uiState.selectedPriceTableId == table.id,
                    onClick = { viewModel.selectPriceTable(table.id) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = table.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        if (uiState.priceTables.isEmpty()) {
            Text(
                text = "Nenhuma tabela de preços disponível",
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
            onClick = {
                viewModel.registerVehicle(onEntrySuccess)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading && 
                     uiState.priceTables.isNotEmpty() && 
                     uiState.selectedPriceTableId != null &&
                     uiState.plate.isNotBlank() &&
                     uiState.model.isNotBlank() &&
                     uiState.color.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Cadastrar Entrada")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
        }
    }
}

