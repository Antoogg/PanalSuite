
package com.example.panalsuite.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panalsuite.data.local.entity.CommentEntity
import com.example.panalsuite.data.local.database.DatabaseProvider
import com.example.panalsuite.data.local.entity.TicketEntity
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole
import com.example.panalsuite.notification.NotificationHelper
import com.example.panalsuite.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

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

    private val _uiState = MutableStateFlow<TicketUiState>(
        TicketUiState.Success(initialTickets)
    )
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
            UserRole.APPLICANT,
            UserRole.SUPERVISOR,
            UserRole.ADMINISTRATOR -> true

            UserRole.RESOLVER,
            UserRole.MANAGEMENT -> false
        }
    }

    fun canChangeStatus(): Boolean {
        return when (_currentUser.value.role) {
            UserRole.RESOLVER,
            UserRole.SUPERVISOR,
            UserRole.ADMINISTRATOR -> true

            UserRole.APPLICANT,
            UserRole.MANAGEMENT -> false
        }
    }

    fun canAddEvidence(): Boolean {
        return when (_currentUser.value.role) {
            UserRole.RESOLVER,
            UserRole.SUPERVISOR,
            UserRole.ADMINISTRATOR -> true

            UserRole.APPLICANT,
            UserRole.MANAGEMENT -> false
        }
    }

    fun canAssignResponsible(): Boolean {
        return when (_currentUser.value.role) {
            UserRole.SUPERVISOR,
            UserRole.ADMINISTRATOR -> true

            UserRole.APPLICANT,
            UserRole.RESOLVER,
            UserRole.MANAGEMENT -> false
        }
    }

    fun getCommentsByTicket(
        context: Context,
        ticketId: String
    ): Flow<List<CommentEntity>> {
        return DatabaseProvider
            .getDatabase(context)
            .commentDao()
            .getCommentsByTicket(ticketId)
    }

    fun addComment(
        context: Context,
        ticketId: String,
        text: String
    ) {
        if (text.isBlank()) return
        if (_currentUser.value.role != UserRole.RESOLVER) return

        val comment = CommentEntity(
            id = UUID.randomUUID().toString(),
            ticketId = ticketId,
            author = _currentUser.value.name,
            text = text.trim(),
            date = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            ).format(Date())
        )

        viewModelScope.launch {
            DatabaseProvider.getDatabase(context)
                .commentDao()
                .insertComment(comment)
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
        if (!canCreateTicket()) {
            onError("No tienes permisos para crear solicitudes.")
            return
        }

        if (
            workflowType.isBlank() ||
            category.isBlank() ||
            priority.isBlank() ||
            location.isBlank() ||
            description.isBlank()
        ) {
            onError("Por favor completa todos los campos obligatorios.")
            return
        }

        val applicantEmail = _currentUser.value.email.trim()

        if (applicantEmail.isBlank()) {
            onError("Debes iniciar sesión para crear una solicitud.")
            return
        }

        try {
            val newId = "TK-${(1000..9999).random()}"
            val currentDate = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())

            val newTicket = Ticket(
                id = newId,
                workflowType = workflowType,
                category = category,
                priority = priority,
                state = "Pendiente",
                creationDate = currentDate,
                updateDate = currentDate,
                location = location,
                applicantRole = applicantRole,
                resolverTeam = resolverTeam,
                responsible = responsible,
                description = description,
                applicantEmail = applicantEmail
            )

            val ticketDao = DatabaseProvider
                .getDatabase(context)
                .ticketDao()

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
                            description = newTicket.description,
                            applicantEmail = newTicket.applicantEmail
                        )
                    )

                    NotificationHelper(context).sendNotification(
                        title = "Solicitud Creada",
                        message = "Se ha registrado con éxito la solicitud $newId ($category)."
                    )

                    onSuccess()
                } catch (e: Exception) {
                    onError(
                        e.localizedMessage
                            ?: "Error al guardar la solicitud"
                    )
                }
            }
        } catch (e: Exception) {
            onError(
                e.localizedMessage
                    ?: "Error al guardar la solicitud"
            )
        }
    }

    fun updateTicketStatus(
        context: Context,
        ticketId: String,
        newStatus: String
    ) {
        if (!canChangeStatus()) return

        viewModelScope.launch {
            try {
                val ticketDao = DatabaseProvider
                    .getDatabase(context)
                    .ticketDao()

                val ticket = ticketDao.getTicketById(ticketId)
                    ?: return@launch

                val updateDate = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Date())

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
                Log.e(
                    "TicketViewModel",
                    "Error al actualizar la solicitud $ticketId",
                    e
                )
            }
        }
    }

    fun assignTicketResponsible(
        context: Context,
        ticketId: String,
        responsible: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (!canAssignResponsible()) {
            onError("No tienes permisos para asignar responsables.")
            return
        }

        if (responsible.isBlank()) {
            onError("Debes ingresar un responsable.")
            return
        }

        viewModelScope.launch {
            try {
                val ticketDao = DatabaseProvider
                    .getDatabase(context)
                    .ticketDao()

                val ticket = ticketDao.getTicketById(ticketId)

                if (ticket == null) {
                    onError("No se encontró la solicitud.")
                    return@launch
                }

                val updateDate = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Date())

                ticketDao.updateTicketResponsible(
                    ticketId = ticketId,
                    responsible = responsible.trim(),
                    updateDate = updateDate
                )

                onSuccess()
            } catch (e: Exception) {
                onError(
                    e.localizedMessage
                        ?: "No se pudo asignar el responsable."
                )
            }
        }
    }

    fun initializeDatabase(context: Context) {
        val ticketDao = DatabaseProvider
            .getDatabase(context)
            .ticketDao()

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
                        description = entity.description,
                        applicantEmail = entity.applicantEmail
                    )
                }

                _uiState.value = TicketUiState.Success(tickets)
            }
        }
    }

    fun testApiConnection() {
        viewModelScope.launch {
            try {
                val posts = TicketRepository().getPostsFromApi()

                Log.d(
                    "PANAL_API",
                    "Conexión desde ViewModel exitosa. Registros: ${posts.size}"
                )
            } catch (e: Exception) {
                Log.e(
                    "PANAL_API",
                    "Error de conexión desde ViewModel",
                    e
                )
            }
        }
    }
}
