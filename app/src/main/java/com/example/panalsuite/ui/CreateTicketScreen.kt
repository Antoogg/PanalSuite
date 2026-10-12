package com.example.panalsuite.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.panalsuite.data.local.TicketDao
import com.example.panalsuite.data.local.TicketEntity
import com.example.panalsuite.repository.TicketRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun CreateTicketScreen(
    dao: TicketDao,
    onBack: () -> Unit ={}
) {
    var workflowType by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var errorsMessage by remember { mutableStateOf("") }
    var successMessage by remember {mutableStateOf("")}
    val repository = remember { TicketRepository() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Crear solicitud")

        OutlinedTextField(
            value = workflowType,
            onValueChange = { workflowType = it },
            label = { Text("Tipo de solicitud") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = priority,
            onValueChange = { priority = it },
            label = { Text("Prioridad") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Ubicación") },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorsMessage.isNotEmpty()) {
            Text(
                text = errorsMessage,
                color = MaterialTheme.colorScheme.error
            )
        }


        Button(
            onClick = {
                if (
                    workflowType.isBlank() ||
                    category.isBlank() ||
                    priority.isBlank() ||
                    location.isBlank()
                ) {
                    errorsMessage = "Debe completar todos los campos"
                    successMessage = ""
                } else {
                    errorsMessage = ""

                    val today = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(Date())

                    val ticket = TicketEntity(
                        id = UUID.randomUUID().toString(),
                        workflowType = workflowType.trim(),
                        category = category.trim(),
                        priority = priority.trim(),
                        state = "Pendiente",
                        creationDate = today,
                        updateDate = today,
                        location = location.trim(),
                        applicantRole = "Solicitante",
                        resolverTeam = "Sin asignar",
                        responsible = "Sin asignar"
                    )

                    coroutineScope.launch {
                        try {
                            repository.saveTicketToDatabase(dao, ticket)
                            successMessage = "Solicitud creada correctamente"

                            workflowType = ""
                            category = ""
                            priority = ""
                            location = ""
                        } catch (_: Exception) {
                            errorsMessage = "No se pudo guardar la solicitud"
                            successMessage = ""
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear solicitud")
        }

        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = MaterialTheme.colorScheme.primary
            )
        }
        TextButton(
            onClick = onBack
        ) {
            Text("Volver")
        }


    }
}
