package com.example.panalsuite.viewmodel

import androidx.lifecycle.ViewModel
import com.example.panalsuite.model.Ticket
import com.example.panalsuite.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TicketViewModel(
    private val repository: TicketRepository = TicketRepository()
) : ViewModel() {

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
}
