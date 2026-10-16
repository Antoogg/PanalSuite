package com.example.panalsuite.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.notification.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.example.panalsuite.data.local.DatabaseProvider
import com.example.panalsuite.data.local.TicketEntity
import kotlinx.coroutines.launch


class TicketViewModel : ViewModel() {

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

    fun setCurrentUserRole(role: UserRole) {
        _currentUser.value = _currentUser.value.copy(role = role)
    }
    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    fun logout() {
        _currentUser.value = User(
            id = "",
            name = "",
            email = "",
            role = UserRole.APPLICANT
        )
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
        context: Context,
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
            val newId = "TK-${(1000..9999).random()}"
            val newTicket = Ticket(
                id = newId,
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

            val ticketDao = DatabaseProvider.getDatabase(context).ticketDao()

            viewModelScope.launch {
                try {
                    ticketDao.insertTicket(
                        TicketEntity(
                            id = newTicket.id,
                            workflowType = newTicket.workflowType,
                            category = newTicket.category,
                            priority = newTicket.priority,
                            state = newTicket.state,
                            creationDate = newTicket.creationDate,
                            updateDate = newTicket.updateDate,
                            location = newTicket.location,
                            applicantRole = newTicket.applicantRole,
                            resolverTeam = newTicket.resolverTeam,
                            responsible = newTicket.responsible,
                            description = newTicket.description
                        )
                    )

                    NotificationHelper(context).sendNotification(
                        title = "Solicitud Creada",
                        message = "Se ha registrado con éxito la solicitud $newId ($category)."
                    )

                    onSuccess()
                } catch (e: Exception) {
                    onError(e.localizedMessage ?: "Error al guardar la solicitud")
                }
            }



        } catch (e: Exception) {
            onError(e.localizedMessage ?: "Error al guardar la solicitud")
        }
    }

    fun updateTicketStatus(context: Context, ticketId: String, newStatus: String) {
        if (!canChangeStatus()) return

        viewModelScope.launch {
            try {
                val ticketDao = DatabaseProvider.getDatabase(context).ticketDao()

                val ticket = ticketDao.getTicketById(ticketId) ?: return@launch

                val updateDate = java.text.SimpleDateFormat(
                    "yyyy-MM-dd",
                    java.util.Locale.getDefault()
                ).format(java.util.Date())

                ticketDao.updateTicketStatus(
                    ticketId = ticket.id,
                    newStatus = newStatus,
                    updateDate = updateDate
                )

                NotificationHelper(context).sendNotification(
                    title = "Estado Actualizado",
                    message = "La solicitud $ticketId cambió su estado a: '$newStatus'."
                )
            } catch (e: Exception) {
                android.util.Log.e(
                    "TicketViewModel",
                    "Error al actualizar la solicitud $ticketId",
                    e
                )
            }
        }
    }

    fun initializeDatabase(context: Context) {
        val ticketDao = DatabaseProvider.getDatabase(context).ticketDao()

        viewModelScope.launch {
            ticketDao.getAllTickets().collect { entities ->
                val tickets = entities.map { entity ->
                    Ticket(
                        id = entity.id,
                        workflowType = entity.workflowType,
                        category = entity.category,
                        priority = entity.priority,
                        state = entity.state,
                        creationDate = entity.creationDate,
                        updateDate = entity.updateDate,
                        location = entity.location,
                        applicantRole = entity.applicantRole,
                        resolverTeam = entity.resolverTeam,
                        responsible = entity.responsible,
                        description = entity.description
                    )
                }

                _uiState.value = TicketUiState.Success(tickets)
            }
        }
    }
    }