package com.example.panalsuite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticket: Ticket,
    viewModel: TicketViewModel,
    onBackClick: () -> Unit,
    onAddEvidenceClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val comments by viewModel
        .getCommentsByTicket(context, ticket.id)
        .collectAsState(initial = emptyList())

    val currentUser by viewModel.currentUser.collectAsState()
    val canChangeStatus = viewModel.canChangeStatus()
    val canAddEvidence = viewModel.canAddEvidence()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle (${currentUser.role.name})") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ID: ${ticket.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Tipo: ${ticket.workflowType}")
                    Text(text = "Categoría: ${ticket.category}")
                    Text(text = "Prioridad: ${ticket.priority}")
                    Text(text = "Ubicación: ${ticket.location}")
                    Text(
                        text = "Estado actual: ${ticket.state}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Descripción:", fontWeight = FontWeight.Bold)
                    Text(text = ticket.description)
                }
            }
            Text(
                text = "Comentarios:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            var commentText by remember { mutableStateOf("") }

            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = { Text("Escribe un comentario") },
                modifier = Modifier.fillMaxWidth(),
                enabled = currentUser.role == UserRole.RESOLVER,
                minLines = 2,
                maxLines = 4
            )
            Button(
                onClick = {
                    viewModel.addComment(
                        context = context,
                        ticketId = ticket.id,
                        text = commentText
                    )
                    commentText = ""
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = commentText.isNotBlank() && currentUser.role == UserRole.RESOLVER
            ) {
                Text("Guardar comentario")
            }

            if (comments.isEmpty()) {
                Text("Todavía no hay comentarios en esta solicitud.")
            } else {
                comments.forEach { comment ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = comment.author,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = comment.text)
                            Text(
                                text = comment.date,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Text(
                text = "Acciones por Rol:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (canChangeStatus) {
                val nextStatus = when (ticket.state) {
                    "Pendiente" -> "En Proceso"
                    "En Proceso" -> "Resuelto"
                    else -> null
                }

                if (nextStatus != null) {
                    Button(
                        onClick = {
                            viewModel.updateTicketStatus(
                                context = context,
                                ticketId = ticket.id,
                                newStatus = nextStatus
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Marcar como '$nextStatus'")
                    }
                }


            if (canAddEvidence) {
                OutlinedButton(
                    onClick = onAddEvidenceClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Adjuntar Evidencia / Foto")
                }
            }
           }

            if (!canChangeStatus && !canAddEvidence) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "Modo Lectura: Tu rol (${currentUser.role.name}) no tiene permisos para realizar acciones en esta solicitud.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver atrás")
            }
        }
    }
}