package com.example.panalsuite.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.viewmodel.TicketViewModel

@Composable
fun HomeScreen(
    viewModel: TicketViewModel,
    onTicketClick: (String) -> Unit
) {
    val tickets by viewModel.tickets.collectAsState()

    HomeScreenContent(
        tickets = tickets,
        onTicketClick = onTicketClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    tickets: List<Ticket>,
    onTicketClick: (String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Bandeja de Entrada - PanalSuite") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tickets) { ticket ->
                TicketItemCard(ticket = ticket, onClick = { onTicketClick(ticket.id) })
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
            Text(
                text = "${ticket.id} - ${ticket.priority}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
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
        tickets = listOf(
            Ticket("TK-2001", "Mesa de Ayuda", "Impresora", "Alta", "Pendiente", "", "", "Centro", "", "", "")
        ),
        onTicketClick = {}
    )
}