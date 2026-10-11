package com.example.panalsuite.repository
import com.example.panalsuite.data.local.TicketDao
import com.example.panalsuite.data.local.TicketEntity
import com.example.panalsuite.model.Ticket
import kotlinx.coroutines.flow.Flow
import kotlin.String

class TicketRepository {

    suspend fun getTicketFromDatabase(
        dao: TicketDao,
        ticketId: String
    ): TicketEntity? {
        return dao.getTicketById(ticketId)
    }

    fun getTicketsFromDatabase(dao: TicketDao): Flow<List<TicketEntity>> {
        return dao.getAllTickets()
    }

    suspend fun saveTicketToDatabase(
        dao: TicketDao,
        ticketEntity: TicketEntity
    ){
        dao.insertTicket(ticketEntity)
    }

    suspend fun deleteTicketFromDatabase(
        dao: TicketDao,
        ticketId: String
    ) {
        dao.deleteTicket(ticketId)
    }
    fun getTickets(): List<Ticket>{
        return listOf(
            Ticket(
                id = "TK-2001",
                workflowType="Mesa de Ayuda",
                category="Falla de Impresora",
                priority="Alta",
                state="Pendiente",
                creationDate="2026-09-01",
                updateDate="2026-09-02",
                location="Sucursal Farmacia Ahumada Centro",
                applicantRole="Solicitante",
                resolverTeam="Soporte TI",
                responsible="Carlos Pérez"
            ),
            Ticket(
                id = "TK-2002",
                workflowType="Gestión de Gastos",
                category="Reembolso Movilidad",
                priority="Media",
                state="En Proceso",
                creationDate="2026-09-02",
                updateDate="2026-09-03",
                location="Tienda Retail Sede Alameda",
                applicantRole="Solicitante",
                resolverTeam="Finanzas",
                responsible="Ana Gómez"
            )
        )
    }
}