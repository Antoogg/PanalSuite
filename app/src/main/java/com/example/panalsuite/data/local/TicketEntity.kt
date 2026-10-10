package com.example.panalsuite.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey
    val id: String,
    val workflowType: String,
    val category: String,
    val priority: String,
    val state: String,
    val creationDate: String,
    val updateDate: String,
    val location: String,
    val applicationRole: String,
    val resolverTeam: String,
    val responsible: String

)