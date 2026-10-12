package com.example.panalsuite.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    @Volatile
    private var database: PanalSuiteDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `evidences` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `ticketId` TEXT NOT NULL,
                    `imagePath` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    fun getDatabase(context: Context): PanalSuiteDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                PanalSuiteDatabase::class.java,
                "panalsuite_database"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { database = it }
        }
    }
}