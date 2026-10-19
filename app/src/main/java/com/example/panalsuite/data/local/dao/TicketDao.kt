package com.example.panalsuite.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.panalsuite.data.local.entity.TicketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {

    @Query("SELECT * FROM tickets")
    fun getAllTickets(): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE id = :ticketId LIMIT 1")
    suspend fun getTicketById(ticketId: String): TicketEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: TicketEntity)

    @Query("DELETE FROM tickets WHERE id = :ticketId")
    suspend fun deleteTicket(ticketId: String)

    @Query(
        "UPDATE tickets SET state = :newStatus, " +
                "updateDate = :updateDate WHERE id = :ticketId"
    )
    suspend fun updateTicketStatus(
        ticketId: String,
        newStatus: String,
        updateDate: String
    )

    @Query(
        "UPDATE tickets SET responsible = :responsible, " +
                "updateDate = :updateDate WHERE id = :ticketId"
    )
    suspend fun updateTicketResponsible(
        ticketId: String,
        responsible: String,
        updateDate: String
    )
}