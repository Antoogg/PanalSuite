package com.example.panalsuite.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TicketEntity::class],
    version = 1,
    exportSchema = false
)

abstract class PanalSuiteDatabase : RoomDatabase() {

    abstract fun ticketDao(): TicketDao
}