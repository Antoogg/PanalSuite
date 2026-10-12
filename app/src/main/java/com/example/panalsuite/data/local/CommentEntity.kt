package com.example.panalsuite.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val ticketId: String,
    val author: String,
    val text: String,
    val date: String
)