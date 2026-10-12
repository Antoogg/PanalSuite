package com.example.panalsuite.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panalsuite.data.local.DatabaseProvider
import com.example.panalsuite.data.local.TicketDao
import com.example.panalsuite.data.local.TicketEntity
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketViewModel(
    private val repository: TicketRepository = TicketRepository()
) : ViewModel() {

    private var ticketDao: TicketDao? = null

    private val _uiState = MutableStateFlow<TicketUiState>(TicketUiState.Loading)
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()

    init {
        loadTickets()
    }

    fun initializeDatabase(context: Context) {
        ticketDao = DatabaseProvider.getDatabase(context).ticketDao()
        loadTicketsFromDatabase()
    }

    fun loadTicketsFromDatabase() {
        val dao = ticketDao ?: return

        viewModelScope.launch {
            _uiState.value = TicketUiState.Loading
            try {
                repository.getTicketsFromDatabase(dao).collect { entities ->
                    val ticketList = entities.map { entity ->
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
                    _tickets.value = ticketList
                    _uiState.value = TicketUiState.Success(ticketList)
                }
            } catch (e: Exception) {
                _uiState.value = TicketUiState.Error("Error al cargar tickets desde la base de datos.")
            }
        }
    }

    private fun loadTickets() {
        val list = repository.getTickets()
        _tickets.value = list
        _uiState.value = TicketUiState.Success(list)
    }

    fun getTicketById(id: String): Ticket? {
        return _tickets.value.find { it.id == id }
    }

    fun updateTicketStatus(ticketId: String, newStatus: String) {
        val dao = ticketDao

        viewModelScope.launch {
            if (dao != null) {
                repository.updateTicketStatusInDatabase(dao, ticketId, newStatus)
            } else {
                val updatedList = _tickets.value.map { ticket ->
                    if (ticket.id == ticketId) {
                        ticket.copy(state = newStatus)
                    } else {
                        ticket
                    }
                }
                _tickets.value = updatedList
                _uiState.value = TicketUiState.Success(updatedList)
            }
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
        if (category.isBlank() || priority.isBlank() || location.isBlank() || description.isBlank()) {
            onError("Por favor completa los campos requeridos (*).")
            return
        }

        val dao = ticketDao
        val newId = "TK-${System.currentTimeMillis().toString().takeLast(4)}"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val newEntity = TicketEntity(
            id = newId,
            workflowType = workflowType.ifBlank { "Mesa de Ayuda" },
            category = category,
            priority = priority,
            state = "Pendiente",
            creationDate = currentDate,
            updateDate = currentDate,
            location = location,
            applicantRole = applicantRole.ifBlank { "Solicitante" },
            resolverTeam = resolverTeam.ifBlank { "Soporte TI" },
            responsible = responsible.ifBlank { "Sin asignar" },
            description = description
        )

        viewModelScope.launch {
            if (dao != null) {
                repository.saveTicketToDatabase(dao, newEntity)
            } else {
                val newTicket = Ticket(
                    id = newEntity.id,
                    workflowType = newEntity.workflowType,
                    category = newEntity.category,
                    priority = newEntity.priority,
                    state = newEntity.state,
                    creationDate = newEntity.creationDate,
                    updateDate = newEntity.updateDate,
                    location = newEntity.location,
                    applicantRole = newEntity.applicantRole,
                    resolverTeam = newEntity.resolverTeam,
                    responsible = newEntity.responsible,
                    description = newEntity.description
                )
                val updatedList = _tickets.value + newTicket
                _tickets.value = updatedList
                _uiState.value = TicketUiState.Success(updatedList)
            }
            onSuccess()
        }
    }
}