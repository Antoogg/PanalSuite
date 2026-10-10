package com.example.panalsuite.data.local

import android.content.Context
import androidx.room.Room


object DatabaseProvider {

    @Volatile
    private var database: PanalSuiteDatabase? = null


    fun getDatabase(context: Context): PanalSuiteDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                PanalSuiteDatabase::class.java,
                "panalsuite_database"
            ).build().also {
                database = it
            }
        }
    }
}