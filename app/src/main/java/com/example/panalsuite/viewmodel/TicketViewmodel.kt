package com.example.panalsuite.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.panalsuite.data.local.DatabaseProvider
import com.example.panalsuite.data.local.TicketDao
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketViewModel(
    private val repository: TicketRepository = TicketRepository()
) : ViewModel() {

    private var ticketDao: TicketDao? = null

    fun initializeDatabase(context: Context) {
        ticketDao = DatabaseProvider.getDatabase(context).ticketDao()
        loadTicketsFromDatabase()
    }

    fun loadTicketsFromDatabase() {
        val dao = ticketDao ?: return

        viewModelScope.launch {
            repository.getTicketsFromDatabase(dao).collect { entities ->
                _tickets.value = entities.map { entity ->
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
                        responsible = entity.responsible
                    )
                }

            }
        }
    }

    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()

    init {
        loadTickets()
    }

    private fun loadTickets() {
        _tickets.value = repository.getTickets()
    }

    fun getTicketById(id: String): Ticket? {
        return _tickets.value.find { it.id == id }
    }

    fun updateTicketStatus(ticketId: String, newStatus: String) {
        _tickets.value = tickets.value.map { ticket ->
            if (ticket.id == ticketId) {
                ticket.copy(state = newStatus)
            } else {
                ticket
            }

        }
    }
}
