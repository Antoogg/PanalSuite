package com.example.panalsuite.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.viewmodel.TicketUiState
import com.example.panalsuite.viewmodel.TicketViewModel

@Composable
fun HomeScreen(
    viewModel: TicketViewModel,
    onTicketClick: (String) -> Unit,
    onCreateTicketClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initializeDatabase(context)
    }

    HomeScreenContent(
        uiState = uiState,
        onTicketClick = onTicketClick,
        onCreateTicketClick = onCreateTicketClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: TicketUiState,
    onTicketClick: (String) -> Unit,
    onCreateTicketClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Bandeja de Entrada - PanalSuite")
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateTicketClick,
                icon = {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = { Text("Nueva solicitud") }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Crossfade(targetState = uiState, label = "HomeScreenStateCrossfade") { state ->
                when (state) {
                    is TicketUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is TicketUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    is TicketUiState.Success -> {
                        if (state.tickets.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No hay solicitudes registradas.")
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.tickets) { ticket ->
                                    TicketItemCard(
                                        ticket = ticket,
                                        onClick = { onTicketClick(ticket.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TicketItemCard(
    ticket: Ticket,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = ticket.id,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = ticket.priority,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${ticket.category} (${ticket.location})")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Estado: ${ticket.state}",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(
        uiState = TicketUiState.Success(
            listOf(
                Ticket(
                    id = "TK-2001",
                    workflowType = "Mesa de Ayuda",
                    category = "Impresora",
                    priority = "Alta",
                    state = "Pendiente",
                    creationDate = "2026-09-01",
                    updateDate = "2026-09-02",
                    location = "Centro",
                    applicantRole = "Solicitante",
                    resolverTeam = "Soporte TI",
                    responsible = "Carlos Pérez",
                    description = "Falla de impresora"
                )
            )
        ),
        onTicketClick = {}
    )
}