package com.example.ui.components

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExamBoxBgLight
import com.example.ui.theme.ExamBoxBorderLight
import java.util.Locale

@Composable
fun FormattedAnswerView(
    question: String?,
    answerText: String,
    onSaveAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isSaved by remember { mutableStateOf(false) }

    // Text To Speech Initialization
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeaking by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        var ttsEngine: TextToSpeech? = null
        ttsEngine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsEngine?.language = Locale.forLanguageTag("hi-IN")
            }
        }
        tts = ttsEngine
        onDispose {
            ttsEngine?.stop()
            ttsEngine?.shutdown()
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Exam-Oriented Answer",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Row {
                    // Audio Speak Button
                    IconButton(
                        onClick = {
                            tts?.let { engine ->
                                if (isSpeaking) {
                                    engine.stop()
                                    isSpeaking = false
                                } else {
                                    val cleanText = answerText
                                        .replace("#", "")
                                        .replace("*", "")
                                        .take(2000)
                                    engine.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "TextileAudio")
                                    isSpeaking = true
                                }
                            }
                        },
                        modifier = Modifier.testTag("tts_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak Answer",
                            tint = if (isSpeaking) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Copy Button
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(answerText))
                            Toast.makeText(context, "Answer copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("copy_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Answer",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Save / Bookmark Button
                    IconButton(
                        onClick = {
                            onSaveAnswer()
                            isSaved = true
                            Toast.makeText(context, "Saved to My Questions!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("save_button")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Answer",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (!question.isNullCalm()) {
                Text(
                    text = "Q: $question",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Parse & Render Sections
            val lines = answerText.split("\n")
            var inExamBox = false
            val examBoxLines = mutableListOf<String>()

            lines.forEach { line ->
                val trimmed = line.trim()

                when {
                    trimmed.startsWith("Exam Point:", ignoreCase = true) ||
                    trimmed.startsWith("परीक्षा बिंदु:", ignoreCase = true) -> {
                        if (examBoxLines.isNotEmpty()) {
                            ExamPointBox(examBoxLines.joinToString("\n"))
                            examBoxLines.clear()
                        }
                        examBoxLines.add(trimmed)
                        inExamBox = true
                    }
                    inExamBox && trimmed.isNotBlank() && !trimmed.startsWith("#") && !trimmed.startsWith("1.") && !trimmed.startsWith("2.") -> {
                        examBoxLines.add(trimmed)
                    }
                    else -> {
                        if (inExamBox) {
                            ExamPointBox(examBoxLines.joinToString("\n"))
                            examBoxLines.clear()
                            inExamBox = false
                        }

                        if (trimmed.startsWith("#") || trimmed.matches(Regex("^[1-9]\\.\\s.*"))) {
                            HeadingText(trimmed)
                        } else if (trimmed.startsWith("|") && trimmed.endsWith("|")) {
                            TableRowText(trimmed)
                        } else if (trimmed.startsWith("-") || trimmed.startsWith("•")) {
                            BulletPointText(trimmed)
                        } else if (trimmed.isNotBlank()) {
                            NormalAnswerText(trimmed)
                        }
                    }
                }
            }

            if (inExamBox && examBoxLines.isNotEmpty()) {
                ExamPointBox(examBoxLines.joinToString("\n"))
            }
        }
    }
}

private fun String?.isNullCalm(): Boolean = this == null || this.isBlank()

@Composable
private fun ExamPointBox(content: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(ExamBoxBgLight, RoundedCornerShape(12.dp))
            .border(1.5.dp, ExamBoxBorderLight, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = ExamBoxBorderLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Exam Quick Point",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ExamBoxBorderLight
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content.replace("Exam Point:", "").replace("परीक्षा बिंदु:", "").trim(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun HeadingText(text: String) {
    val clean = text.replace("#", "").trim()
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
    ) {
        Text(
            text = clean,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun TableRowText(text: String) {
    val cols = text.split("|").filter { it.isNotBlank() }
    if (cols.isEmpty() || text.contains("---")) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(vertical = 4.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        cols.forEach { col ->
            Text(
                text = col.trim(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
            )
        }
    }
}

@Composable
private fun BulletPointText(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = "• ",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 15.sp
        )
        Text(
            text = text.removePrefix("-").removePrefix("•").trim(),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun NormalAnswerText(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 20.sp,
        modifier = Modifier.padding(vertical = 3.dp)
    )
}
