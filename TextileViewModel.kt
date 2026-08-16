package com.example.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.db.SavedQuestionEntity
import com.example.data.db.StudyHistoryEntity
import com.example.data.db.StudyNoteEntity
import com.example.data.repository.TextileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AskAiUiState(
    val query: String = "",
    val language: String = "HINDI_ENGLISH", // HINDI_ENGLISH, ENGLISH, HINGLISH
    val questionTypeFormat: String = "AUTO", // AUTO, DEFINITION, SHORT, LONG, DIFFERENCE, MACHINE, PRACTICAL
    val isLoading: Boolean = false,
    val currentAnswer: String? = null,
    val lastAskedQuestion: String? = null,
    val selectedImage: Bitmap? = null,
    val errorMessage: String? = null,
    val activeTab: Int = 0 // 0: Ask AI, 1: Exam Mode, 2: Practicals, 3: PDF Notes, 4: My Study
)

class TextileViewModel(private val repository: TextileRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AskAiUiState())
    val uiState: StateFlow<AskAiUiState> = _uiState.asStateFlow()

    val savedQuestions: StateFlow<List<SavedQuestionEntity>> = repository.savedQuestions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val studyNotes: StateFlow<List<StudyNoteEntity>> = repository.studyNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val studyHistory: StateFlow<List<StudyHistoryEntity>> = repository.studyHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateQuery(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
    }

    fun setLanguage(language: String) {
        _uiState.value = _uiState.value.copy(language = language)
    }

    fun setQuestionTypeFormat(format: String) {
        _uiState.value = _uiState.value.copy(questionTypeFormat = format)
    }

    fun setImage(bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(selectedImage = bitmap)
    }

    fun clearImage() {
        _uiState.value = _uiState.value.copy(selectedImage = null)
    }

    fun setActiveTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(activeTab = tabIndex)
    }

    fun askQuestion(customQuery: String? = null) {
        val promptToUse = customQuery ?: _uiState.value.query
        if (promptToUse.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                lastAskedQuestion = promptToUse
            )

            try {
                val answer = repository.askAi(
                    question = promptToUse,
                    language = _uiState.value.language,
                    questionTypeFormat = _uiState.value.questionTypeFormat,
                    imageBitmap = _uiState.value.selectedImage
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentAnswer = answer
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error generating answer: ${e.message}"
                )
            }
        }
    }

    fun saveCurrentAnswer(topic: String = "Textile Technology") {
        val q = _uiState.value.lastAskedQuestion ?: return
        val a = _uiState.value.currentAnswer ?: return

        viewModelScope.launch {
            repository.saveQuestion(
                question = q,
                answer = a,
                questionType = _uiState.value.questionTypeFormat,
                topic = topic
            )
        }
    }

    fun deleteSavedQuestion(id: Long) {
        viewModelScope.launch {
            repository.deleteSavedQuestion(id)
        }
    }

    fun addNote(title: String, content: String, topicCategory: String = "General") {
        viewModelScope.launch {
            repository.addNote(title, content, topicCategory)
        }
    }

    fun deleteNote(note: StudyNoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun analyzePdfText(pdfContentText: String, actionType: String) {
        if (pdfContentText.isBlank()) return

        val prompt = when (actionType) {
            "SUMMARY" -> "Provide a comprehensive Chapter Summary with key points and formulas from these Textile Notes:\n\n$pdfContentText"
            "IMPORTANT_QUESTIONS" -> "Extract the top 10 Important Exam Questions (1 Mark, 2 Marks, 5 Marks, 10 Marks) from these Textile Notes:\n\n$pdfContentText"
            "SHORT_NOTES" -> "Create crisp, revision-ready Short Notes with Exam Points from these Textile Notes:\n\n$pdfContentText"
            "MCQS" -> "Generate 10 Multiple Choice Questions (MCQs) with answers and brief explanations based on these Textile Notes:\n\n$pdfContentText"
            "VIVA" -> "List 10 Practical Viva Questions with simple answers from these Textile Notes:\n\n$pdfContentText"
            else -> "Analyze these Textile Notes and summarize key diploma exam points:\n\n$pdfContentText"
        }

        askQuestion(prompt)
    }

    class Factory(private val repository: TextileRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TextileViewModel::class.java)) {
                return TextileViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
