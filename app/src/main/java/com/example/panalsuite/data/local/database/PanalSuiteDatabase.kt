package com.example.panalsuite.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.panalsuite.data.local.dao.CommentDao
import com.example.panalsuite.data.local.dao.EvidenceDao
import com.example.panalsuite.data.local.dao.TicketDao
import com.example.panalsuite.data.local.entity.CommentEntity
import com.example.panalsuite.data.local.entity.EvidenceEntity
import com.example.panalsuite.data.local.entity.TicketEntity

@Database(
    entities = [
        TicketEntity::class,
        EvidenceEntity::class,
        CommentEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class PanalSuiteDatabase : RoomDatabase() {

    abstract fun ticketDao(): TicketDao

    abstract fun evidenceDao(): EvidenceDao

    abstract fun commentDao(): CommentDao
}