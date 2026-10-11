package com.example.panalsuite

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.panalsuite.data.local.PanalSuiteDatabase
import com.example.panalsuite.data.local.TicketEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.content.Context
import com.example.panalsuite.viewmodel.TicketViewModel

@RunWith(AndroidJUnit4::class)
class TicketDatabaseTest {

    private lateinit var database: PanalSuiteDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            PanalSuiteDatabase::class.java
        ).build()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun insertAndReadTicket() = runBlocking {
        val ticket = TicketEntity(
            id = "TK-TEST-001",
            workflowType = "Mesa de Ayuda",
            category = "Soporte TI",
            priority = "Alta",
            state = "Pendiente",
            creationDate = "2026-10-10",
            updateDate = "2026-10-10",
            location = "Sucursal Centro",
            applicantRole = "Solicitante",
            resolverTeam = "Soporte TI",
            responsible = "Sin asignar"
        )

        database.ticketDao().insertTicket(ticket)

        val savedTicket = database.ticketDao()
            .getTicketById("TK-TEST-001")

        assertNotNull(savedTicket)
        assertEquals("TK-TEST-001", savedTicket?.id)
        assertEquals("Pendiente", savedTicket?.state)
    }

    //Segunda prueba
    @Test
    fun ticketPersistsAfterReopeningDatabase() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val databaseName = "ticket_persistence_test"

        context.deleteDatabase(databaseName)

        try {
            val ticket = TicketEntity(
                id = "TK-TEST-002",
                workflowType = "Mesa de Ayuda",
                category = "Soporte TI",
                priority = "Alta",
                state = "Pendiente",
                creationDate = "2026-10-10",
                updateDate = "2026-10-10",
                location = "Sucursal Centro",
                applicantRole = "Solicitante",
                resolverTeam = "Soporte TI",
                responsible = "Sin asignar"
            )

            val firstDatabase = Room.databaseBuilder(
                context,
                PanalSuiteDatabase::class.java,
                databaseName
            ).build()

            firstDatabase.ticketDao().insertTicket(ticket)
            firstDatabase.close()

            val reopenedDatabase = Room.databaseBuilder(
                context,
                PanalSuiteDatabase::class.java,
                databaseName
            ).build()

            try {
                val savedTicket = reopenedDatabase.ticketDao()
                    .getTicketById("TK-TEST-002")

                assertNotNull(savedTicket)
                assertEquals("TK-TEST-002", savedTicket?.id)
                assertEquals("Pendiente", savedTicket?.state)
            } finally {
                reopenedDatabase.close()
            }
        } finally {
            context.deleteDatabase(databaseName)
        }
    }

    @Test
    fun updateTicketStatusChangesState(){
        val viewModel = TicketViewModel()
        val ticket = viewModel.tickets.value.first()

        viewModel.updateTicketStatus(ticket.id, "Resuelto")

        val updateTicket = viewModel.getTicketById(ticket.id)

        assertEquals("Resuelto", updateTicket?.state)
    }
    @Test
    fun deleteTicketRemovesItFromDatabase() = runBlocking {
        val ticket = TicketEntity(
            id = "TK-TEST-003",
            workflowType = "Mesa de Ayuda",
            category = "Soporte TI",
            priority = "Alta",
            state = "Pendiente",
            creationDate = "2026-10-11",
            updateDate = "2026-10-11",
            location = "Sucursal Centro",
            applicantRole = "Solicitante",
            resolverTeam = "Soporte TI",
            responsible = "Sin asignar"
        )

        val repository = com.example.panalsuite.repository.TicketRepository()
        val dao = database.ticketDao()

        repository.saveTicketToDatabase(dao, ticket)

        assertNotNull(dao.getTicketById("TK-TEST-003"))

        repository.deleteTicketFromDatabase(dao, "TK-TEST-003")

        val deletedTicket = dao.getTicketById("TK-TEST-003")

        org.junit.Assert.assertNull(deletedTicket)
    }


    @Test
    fun createTicketSavesToDatabase() = runBlocking {
        val repository = com.example.panalsuite.repository.TicketRepository()
        val dao = database.ticketDao()

        val ticket = TicketEntity(
            id = "TK-CREATE-001",
            workflowType = "Mesa de Ayuda",
            category = "Falla de impresora",
            priority = "Alta",
            state = "Pendiente",
            creationDate = "2026-10-11",
            updateDate = "2026-10-11",
            location = "Sucursal Centro",
            applicantRole = "Solicitante",
            resolverTeam = "Sin asignar",
            responsible = "Sin asignar"
        )

        repository.saveTicketToDatabase(dao, ticket)

        val savedTicket = dao.getTicketById("TK-CREATE-001")

        assertNotNull(savedTicket)
        assertEquals("Mesa de Ayuda", savedTicket?.workflowType)
        assertEquals("Falla de impresora", savedTicket?.category)
        assertEquals("Pendiente", savedTicket?.state)
    }

}

