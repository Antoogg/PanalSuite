package com.example.panalsuite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val canAssignResponsible = viewModel.canAssignResponsible()

    var showResponsibleMenu by remember { mutableStateOf(false) }
    var selectedResponsible by remember(ticket.id) {
        mutableStateOf(
            ticket.responsible.takeUnless {
                it.isBlank() || it == "Sin asignar"
            } ?: ""
        )
    }
    var assignmentMessage by remember { mutableStateOf("") }

    val responsibleOptions = listOf(
        "Usuario Resolutor"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle de solicitud",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 40.dp
                ),
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
                    Text(text = "Equipo resolutor: ${ticket.resolverTeam}")
                    Text(text = "Responsable asignado: ${ticket.responsible}")
                    Text(text = "Rol solicitante: ${ticket.applicantRole}")
                    Text(text = "Fecha de creación: ${ticket.creationDate}")
                    Text(text = "Última actualización: ${ticket.updateDate}")
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
                text = "Acciones disponibles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )


            if (canAssignResponsible) {

                Text(
                    text = "Asignar responsable",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                ExposedDropdownMenuBox(
                    expanded = showResponsibleMenu,
                    onExpandedChange = {
                        showResponsibleMenu = !showResponsibleMenu
                    }
                ) {
                    OutlinedTextField(
                        value = selectedResponsible,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Responsable") },
                        placeholder = { Text("Selecciona un responsable") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = showResponsibleMenu
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = showResponsibleMenu,
                        onDismissRequest = {
                            showResponsibleMenu = false
                        }
                    ) {
                        responsibleOptions.forEach { responsible ->
                            DropdownMenuItem(
                                text = { Text(responsible) },
                                onClick = {
                                    selectedResponsible = responsible
                                    showResponsibleMenu = false
                                    assignmentMessage = ""
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        viewModel.assignTicketResponsible(
                            context = context,
                            ticketId = ticket.id,
                            responsible = selectedResponsible,
                            onSuccess = {
                                assignmentMessage =
                                    "Responsable asignado correctamente."
                            },
                            onError = { error ->
                                assignmentMessage = error
                            }
                        )
                    },
                    enabled = selectedResponsible.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar responsable")
                }

                if (assignmentMessage.isNotBlank()) {
                    Text(
                        text = assignmentMessage,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }


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

            var showAllComments by remember { mutableStateOf(false) }
            if (comments.size > 3) {
                TextButton(
                    onClick = { showAllComments = !showAllComments },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (showAllComments) {
                            "Ver menos comentarios"
                        } else {
                            "Ver todos los comentarios (${comments.size})"
                        }
                    )
                }
            }

            if (comments.isEmpty()) {
                Text("Todavía no hay comentarios en esta solicitud.")
            } else {


                val visibleComments = if (showAllComments) {
                    comments.asReversed()
                } else {
                    comments.asReversed().take(3)
                }

                visibleComments.forEach { comment ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 8.dp
                            )
                        ) {
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



            Spacer(modifier = Modifier.height(8.dp))


        }
    }
}