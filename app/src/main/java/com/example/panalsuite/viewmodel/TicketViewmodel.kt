package com.example.panalsuite.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TicketViewModel : ViewModel() {

    // Lista base de demostración para visualización inicial
    private val initialTickets = mutableListOf(
        Ticket(
            id = "TK-1001",
            workflowType = "Mesa de Ayuda",
            category = "Impresora",
            priority = "Alta",
            state = "Pendiente",
            creationDate = "2026-10-10",
            updateDate = "2026-10-10",
            location = "Sucursal Central",
            applicantRole = "Solicitante",
            resolverTeam = "Soporte TI",
            responsible = "Carlos Pérez",
            description = "Falla al imprimir documentos a color."
        ),
        Ticket(
            id = "TK-1002",
            workflowType = "Mesa de Ayuda",
            category = "Red",
            priority = "Media",
            state = "En Proceso",
            creationDate = "2026-10-11",
            updateDate = "2026-10-12",
            location = "Sucursal Norte",
            applicantRole = "Solicitante",
            resolverTeam = "Redes e Infraestructura",
            responsible = "María Gómez",
            description = "Cortes intermitentes de conexión a Internet."
        )
    )

    private val _uiState = MutableStateFlow<TicketUiState>(TicketUiState.Success(initialTickets))
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow(
        User(
            id = "USR-001",
            name = "Carlos López",
            email = "carlos@empresa.com",
            role = UserRole.ADMINISTRATOR
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Método para cambiar el rol activamente durante las pruebas
    fun setCurrentUserRole(role: UserRole) {
        _currentUser.value = _currentUser.value.copy(role = role)
    }

    fun canCreateTicket(): Boolean {
        return when (_currentUser.value.role) {
            UserRole.APPLICANT, UserRole.SUPERVISOR, UserRole.ADMINISTRATOR -> true
            UserRole.RESOLVER, UserRole.MANAGEMENT -> false
        }
    }

    fun canChangeStatus(): Boolean {
        return when (_currentUser.value.role) {
            UserRole.RESOLVER, UserRole.SUPERVISOR, UserRole.MANAGEMENT, UserRole.ADMINISTRATOR -> true
            UserRole.APPLICANT -> false
        }
    }

    fun canAddEvidence(): Boolean {
        return when (_currentUser.value.role) {
            UserRole.RESOLVER, UserRole.SUPERVISOR, UserRole.ADMINISTRATOR -> true
            UserRole.APPLICANT, UserRole.MANAGEMENT -> false
        }
    }

    fun createTicket(
        workflowType: String,
        category: String,
        priority: String,
        location: String,
        applicantRole: String,
        resolverTeam: String,
        responsible: String,
        description: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (workflowType.isBlank() || category.isBlank() || priority.isBlank() || location.isBlank() || description.isBlank()) {
            onError("Por favor completa todos los campos obligatorios.")
            return
        }

        try {
            val newTicket = Ticket(
                id = "TK-${(1000..9999).random()}",
                workflowType = workflowType,
                category = category,
                priority = priority,
                state = "Pendiente",
                creationDate = "2026-10-12",
                updateDate = "2026-10-12",
                location = location,
                applicantRole = applicantRole,
                resolverTeam = resolverTeam,
                responsible = responsible,
                description = description
            )

            val currentList = (_uiState.value as? TicketUiState.Success)?.tickets ?: emptyList()
            _uiState.value = TicketUiState.Success(currentList + newTicket)

            onSuccess()
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "Error al guardar la solicitud")
        }
    }

    fun updateTicketStatus(ticketId: String, newStatus: String) {
        val currentList = (_uiState.value as? TicketUiState.Success)?.tickets ?: return
        val updatedList = currentList.map { ticket ->
            if (ticket.id == ticketId) {
                ticket.copy(state = newStatus, updateDate = "2026-10-12")
            } else {
                ticket
            }
        }
        _uiState.value = TicketUiState.Success(updatedList)
    }

    fun initializeDatabase(context: Context) {
        if (_uiState.value is TicketUiState.Loading) {
            _uiState.value = TicketUiState.Success(initialTickets)
        }
    }
}