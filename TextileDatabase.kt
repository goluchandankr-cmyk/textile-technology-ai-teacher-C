package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SavedQuestionEntity::class,
        StudyNoteEntity::class,
        StudyHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TextileDatabase : RoomDatabase() {

    abstract fun textileDao(): TextileDao

    companion object {
        @Volatile
        private var INSTANCE: TextileDatabase? = null

        fun getDatabase(context: Context): TextileDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TextileDatabase::class.java,
                    "textile_study_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
