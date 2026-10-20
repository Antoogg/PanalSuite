
package com.example.panalsuite.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.panalsuite.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text("Seleccione una opción") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                ),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTicketScreen(
    viewModel: TicketViewModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    val currentUser by viewModel.currentUser.collectAsState()
    val canCreate = viewModel.canCreateTicket()

    val workflowOptions = listOf(
        "Mesa de Ayuda",
        "Gestión de Gastos",
        "Mantenimiento",
        "Solicitud de Recursos"
    )

    val categoryOptions = listOf(
        "Falla de Impresora",
        "Hardware",
        "Software",
        "Redes e Internet",
        "Accesos y Contraseñas",
        "Mantenimiento",
        "Otros"
    )

    val priorityOptions = listOf(
        "Baja",
        "Media",
        "Alta"
    )

    val locationOptions = listOf(
        "Sucursal Ahumada",
        "Tienda Alameda",
        "Sucursal Oma",
        "Sucursal Central",
        "Sucursal Norte",
        "Sucursal Sur",
        "Sucursal Oriente",
        "Casa Matriz",
        "Centro de Distribución"
    )

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
                title = {
                    Text(
                        text = "Nueva solicitud",
                        modifier = Modifier.padding(start = 8.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
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
            if (!canCreate) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Acceso Denegado: Su rol (${currentUser.role.name}) no tiene permisos para crear nuevas solicitudes.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver")
                }
            } else {
                TicketDropdown(
                    label = "Tipo de solicitud *",
                    selectedValue = workflowType,
                    options = workflowOptions,
                    onValueSelected = {
                        workflowType = it
                        errorMessage = ""
                    }
                )

                TicketDropdown(
                    label = "Categoría *",
                    selectedValue = category,
                    options = categoryOptions,
                    onValueSelected = {
                        category = it
                        errorMessage = ""
                    }
                )

                TicketDropdown(
                    label = "Prioridad *",
                    selectedValue = priority,
                    options = priorityOptions,
                    onValueSelected = {
                        priority = it
                        errorMessage = ""
                    }
                )

                TicketDropdown(
                    label = "Ubicación *",
                    selectedValue = location,
                    options = locationOptions,
                    onValueSelected = {
                        location = it
                        errorMessage = ""
                    }
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        errorMessage = ""
                    },
                    label = { Text("Descripción del problema *") },
                    placeholder = {
                        Text("Detalle la situación o requerimiento...")
                    },
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
                        when {
                            workflowType !in workflowOptions ||
                                    category !in categoryOptions ||
                                    priority !in priorityOptions ||
                                    location !in locationOptions -> {
                                errorMessage = "Debe seleccionar una opción válida en todos los campos."
                                successMessage = ""
                            }

                            description.trim().length < 10 ||
                                    !description.any { it.isLetter() } -> {
                                errorMessage = "La descripción debe tener al menos 10 caracteres e incluir letras."
                                successMessage = ""
                            }

                            else -> {
                                viewModel.createTicket(
                                    context = context,
                                    workflowType = workflowType,
                                    category = category,
                                    priority = priority,
                                    location = location,
                                    applicantRole = currentUser.role.name,
                                    resolverTeam = "Soporte TI",
                                    responsible = "Sin asignar",
                                    description = description.trim(),
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
                            }
                        }
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
}
