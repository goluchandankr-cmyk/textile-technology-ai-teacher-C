package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TextileDao {

    // Saved Questions / Bookmarks
    @Query("SELECT * FROM saved_questions ORDER BY timestamp DESC")
    fun getAllSavedQuestions(): Flow<List<SavedQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedQuestion(question: SavedQuestionEntity): Long

    @Delete
    suspend fun deleteSavedQuestion(question: SavedQuestionEntity)

    @Query("DELETE FROM saved_questions WHERE id = :id")
    suspend fun deleteSavedQuestionById(id: Long)

    // Study Notes
    @Query("SELECT * FROM study_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<StudyNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StudyNoteEntity): Long

    @Update
    suspend fun updateNote(note: StudyNoteEntity)

    @Delete
    suspend fun deleteNote(note: StudyNoteEntity)

    // Study History
    @Query("SELECT * FROM study_history ORDER BY timestamp DESC LIMIT 30")
    fun getRecentHistory(): Flow<List<StudyHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: StudyHistoryEntity)

    @Query("DELETE FROM study_history")
    suspend fun clearHistory()
}
