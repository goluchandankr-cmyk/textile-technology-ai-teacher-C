package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_questions")
data class SavedQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val question: String,
    val answer: String,
    val questionType: String, // e.g., DEFINITION, SHORT, LONG, MACHINE, DIFFERENCE, PRACTICAL, EXAM
    val topic: String = "Textile Technology",
    val isBookmarked: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
