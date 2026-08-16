package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.api.GeminiTextileService
import com.example.data.db.SavedQuestionEntity
import com.example.data.db.StudyHistoryEntity
import com.example.data.db.StudyNoteEntity
import com.example.data.db.TextileDao
import kotlinx.coroutines.flow.Flow

class TextileRepository(
    private val dao: TextileDao,
    private val geminiService: GeminiTextileService = GeminiTextileService()
) {

    val savedQuestions: Flow<List<SavedQuestionEntity>> = dao.getAllSavedQuestions()
    val studyNotes: Flow<List<StudyNoteEntity>> = dao.getAllNotes()
    val studyHistory: Flow<List<StudyHistoryEntity>> = dao.getRecentHistory()

    suspend fun askAi(
        question: String,
        language: String = "HINDI_ENGLISH",
        questionTypeFormat: String = "AUTO",
        imageBitmap: Bitmap? = null
    ): String {
        val answer = geminiService.askTextileQuestion(
            prompt = question,
            language = language,
            questionTypeFormat = questionTypeFormat,
            imageBitmap = imageBitmap
        )

        // Save into study history
        val snippet = if (answer.length > 80) answer.take(80) + "..." else answer
        dao.insertHistory(
            StudyHistoryEntity(
                query = question,
                answerSnippet = snippet
            )
        )

        return answer
    }

    suspend fun saveQuestion(
        question: String,
        answer: String,
        questionType: String = "AUTO",
        topic: String = "Textile Technology"
    ): Long {
        return dao.insertSavedQuestion(
            SavedQuestionEntity(
                question = question,
                answer = answer,
                questionType = questionType,
                topic = topic,
                isBookmarked = true
            )
        )
    }

    suspend fun deleteSavedQuestion(id: Long) {
        dao.deleteSavedQuestionById(id)
    }

    suspend fun addNote(title: String, content: String, topicCategory: String = "General"): Long {
        return dao.insertNote(
            StudyNoteEntity(
                title = title,
                content = content,
                topicCategory = topicCategory
            )
        )
    }

    suspend fun updateNote(note: StudyNoteEntity) {
        dao.updateNote(note)
    }

    suspend fun deleteNote(note: StudyNoteEntity) {
        dao.deleteNote(note)
    }

    suspend fun clearHistory() {
        dao.clearHistory()
    }
}
