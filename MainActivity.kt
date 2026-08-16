package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.db.TextileDatabase
import com.example.data.repository.TextileRepository
import com.example.ui.components.HeaderBar
import com.example.ui.screens.AskAiScreen
import com.example.ui.screens.ExamModeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyStudyScreen
import com.example.ui.screens.PdfNotesScreen
import com.example.ui.screens.PracticalsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.TextileAiTheme
import com.example.ui.viewmodel.TextileViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = TextileDatabase.getDatabase(applicationContext)
        val repository = TextileRepository(db.textileDao())
        val viewModelFactory = TextileViewModel.Factory(repository)

        setContent {
            TextileAiTheme {
                val viewModel: TextileViewModel = viewModel(factory = viewModelFactory)
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

data class NavigationTabItem(
    val title: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun MainAppScreen(viewModel: TextileViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val savedQuestions by viewModel.savedQuestions.collectAsState()
    val studyNotes by viewModel.studyNotes.collectAsState()
    val studyHistory by viewModel.studyHistory.collectAsState()

    var selectedBottomTab by remember { mutableIntStateOf(0) }

    val navItems = listOf(
        NavigationTabItem("Home", Icons.Default.Home, "nav_home"),
        NavigationTabItem("Ask AI", Icons.Default.AutoAwesome, "nav_ask"),
        NavigationTabItem("Exam Prep", Icons.Default.Quiz, "nav_exam"),
        NavigationTabItem("Practicals", Icons.Default.Science, "nav_practicals"),
        NavigationTabItem("PDF Notes", Icons.Default.PictureAsPdf, "nav_pdf"),
        NavigationTabItem("My Study", Icons.Default.Folder, "nav_study"),
        NavigationTabItem("Search", Icons.Default.Search, "nav_search")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeaderBar(
                currentLanguage = uiState.language,
                onLanguageSelected = { lang -> viewModel.setLanguage(lang) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedBottomTab == index,
                        onClick = { selectedBottomTab = index },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = { Text(text = item.title, fontSize = 10.sp, maxLines = 1) },
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedBottomTab) {
                0 -> HomeScreen(
                    queryText = uiState.query,
                    onQueryChange = { viewModel.updateQuery(it) },
                    onAskQuestion = { q ->
                        viewModel.updateQuery(q)
                        viewModel.askQuestion(q)
                    },
                    onImageSelected = { bmp -> viewModel.setImage(bmp) },
                    onNavigateToTab = { tabIdx -> selectedBottomTab = tabIdx },
                    onCategoryClick = { cat ->
                        val sampleQ = cat.sampleQuestions.firstOrNull() ?: cat.titleHindi
                        viewModel.updateQuery(sampleQ)
                        viewModel.askQuestion(sampleQ)
                        selectedBottomTab = 1 // Switch to Ask AI
                    }
                )

                1 -> AskAiScreen(
                    queryText = uiState.query,
                    onQueryChange = { viewModel.updateQuery(it) },
                    selectedFormat = uiState.questionTypeFormat,
                    onFormatChange = { viewModel.setQuestionTypeFormat(it) },
                    isLoading = uiState.isLoading,
                    answerText = uiState.currentAnswer,
                    lastAskedQuestion = uiState.lastAskedQuestion,
                    selectedImage = uiState.selectedImage,
                    onImageSelected = { bmp -> viewModel.setImage(bmp) },
                    onAskQuestion = { viewModel.askQuestion() },
                    onSaveAnswer = { viewModel.saveCurrentAnswer() },
                    errorMessage = uiState.errorMessage
                )

                2 -> ExamModeScreen(
                    onAskQuestion = { q, fmt ->
                        viewModel.setQuestionTypeFormat(fmt)
                        viewModel.updateQuery(q)
                        viewModel.askQuestion(q)
                    },
                    onNavigateToAskTab = { selectedBottomTab = 1 }
                )

                3 -> PracticalsScreen(
                    onAskQuestion = { q, fmt ->
                        viewModel.setQuestionTypeFormat(fmt)
                        viewModel.updateQuery(q)
                        viewModel.askQuestion(q)
                    },
                    onNavigateToAskTab = { selectedBottomTab = 1 }
                )

                4 -> PdfNotesScreen(
                    onAnalyzePdfText = { text, action ->
                        viewModel.analyzePdfText(text, action)
                    },
                    onNavigateToAskTab = { selectedBottomTab = 1 }
                )

                5 -> MyStudyScreen(
                    savedQuestions = savedQuestions,
                    studyNotes = studyNotes,
                    studyHistory = studyHistory,
                    onDeleteSavedQuestion = { id -> viewModel.deleteSavedQuestion(id) },
                    onAddNote = { t, c, cat -> viewModel.addNote(t, c, cat) },
                    onDeleteNote = { note -> viewModel.deleteNote(note) },
                    onOpenSavedQuestion = { q, a ->
                        viewModel.updateQuery(q)
                        selectedBottomTab = 1
                    }
                )

                6 -> SearchScreen(
                    onAskQuestion = { q, fmt ->
                        viewModel.setQuestionTypeFormat(fmt)
                        viewModel.updateQuery(q)
                        viewModel.askQuestion(q)
                    },
                    onNavigateToAskTab = { selectedBottomTab = 1 }
                )
            }
        }
    }
}
