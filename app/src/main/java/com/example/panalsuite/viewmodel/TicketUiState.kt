package com.example.panalsuite.viewmodel

import com.example.panalsuite.model.Ticket

sealed interface TicketUiState {
    object Loading : TicketUiState
    data class Success(val tickets: List<Ticket>) : TicketUiState
    data class Error(val message: String) : TicketUiState
}