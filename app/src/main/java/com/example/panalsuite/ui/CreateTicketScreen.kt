package com.example.panalsuite.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.panalsuite.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketScreen(
    viewModel: TicketViewModel,
    onBack: () -> Unit = {}
) {
    var workflowType by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Solicitud") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = workflowType,
                onValueChange = {
                    workflowType = it
                    errorMessage = ""
                },
                label = { Text("Tipo de solicitud *") },
                placeholder = { Text("Ej: Mesa de Ayuda, Gestión de Gastos") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = category,
                onValueChange = {
                    category = it
                    errorMessage = ""
                },
                label = { Text("Categoría *") },
                placeholder = { Text("Ej: Falla de Impresora, Hardware") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = priority,
                onValueChange = {
                    priority = it
                    errorMessage = ""
                },
                label = { Text("Prioridad *") },
                placeholder = { Text("Ej: Alta, Media, Baja") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = location,
                onValueChange = {
                    location = it
                    errorMessage = ""
                },
                label = { Text("Ubicación *") },
                placeholder = { Text("Ej: Sucursal Ahumada, Tienda Alameda") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    errorMessage = ""
                },
                label = { Text("Descripción del problema *") },
                placeholder = { Text("Detalle la situación o requerimiento...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (successMessage.isNotEmpty()) {
                Text(
                    text = successMessage,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    viewModel.createTicket(
                        workflowType = workflowType,
                        category = category,
                        priority = priority,
                        location = location,
                        applicantRole = "Solicitante",
                        resolverTeam = "Soporte TI",
                        responsible = "Sin asignar",
                        description = description,
                        onSuccess = {
                            successMessage = "¡Solicitud creada exitosamente!"
                            errorMessage = ""
                            workflowType = ""
                            category = ""
                            priority = ""
                            location = ""
                            description = ""
                        },
                        onError = { err ->
                            errorMessage = err
                            successMessage = ""
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Solicitud")
            }

            TextButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}