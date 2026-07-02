package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey val questionId: Int,
    val timestamp: Long = System.currentTimeMillis()
)
