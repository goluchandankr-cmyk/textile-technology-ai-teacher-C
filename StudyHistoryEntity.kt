package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_history")
data class StudyHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val query: String,
    val answerSnippet: String,
    val timestamp: Long = System.currentTimeMillis()
)
