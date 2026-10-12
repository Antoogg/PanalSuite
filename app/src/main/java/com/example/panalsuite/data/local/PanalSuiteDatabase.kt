package com.example.panalsuite.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        TicketEntity::class,
        EvidenceEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PanalSuiteDatabase : RoomDatabase() {

    abstract fun ticketDao(): TicketDao

    abstract fun evidenceDao(): EvidenceDao
}
