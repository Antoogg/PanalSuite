package com.example.panalsuite.repository
import com.example.panalsuite.model.Ticket
import kotlin.String

class TicketRepository {
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