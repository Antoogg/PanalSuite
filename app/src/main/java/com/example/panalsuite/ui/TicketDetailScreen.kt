package com.example.panalsuite.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.panalsuite.viewmodel.TicketViewModel


@Composable
fun TicketDetailScreen(
    ticketId: String,
    viewModel: TicketViewModel = viewModel(),
    onBack: () -> Unit = {}
){
    val ticket = viewModel.getTicketById(ticketId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp )

    ){
        Text(text = "Detalle de solicitud")

        if (ticket == null) {
            Text(text = "Solicitud no encontrada")
        } else {
            Text(text = "Código: ${ticket.id}")
            Text(text = "Categoría: ${ticket.category}")
            Text(text = "Estado actual: ${ticket.state}")

            Button(
                onClick = {
                    viewModel.updateTicketStatus(
                        ticketId,
                        "En proceso"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ){
                Text("Cambiar proceso")
            }
            Button(
                onClick = {
                    viewModel.updateTicketStatus(
                        ticketId,
                        "Resuelto"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ){
                Text("Marcar como Resuelto")
            }
        }
        TextButton(onClick = onBack) {
            Text("Volver")
        }
    }
}