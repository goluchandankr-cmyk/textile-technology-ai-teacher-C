package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PdfNotesScreen(
    onAnalyzePdfText: (String, String) -> Unit,
    onNavigateToAskTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    var notesText by remember { mutableStateOf("") }

    val sampleNotes = """
        BLOW ROOM CHAPTER NOTES (Diploma Textile Technology):
        
        Blow room is the first department in yarn manufacturing where cotton bales are processed.
        Main Objectives:
        1. Opening: Breaking dense cotton bales into smaller tufts.
        2. Cleaning: Removing leaf fragments, seed coat, trash and sand.
        3. Blending/Mixing: Homogenizing fibres from different bales.
        4. Lap / Chute Formation: Feeding uniform fibre sheet to Carding machine.
        
        Key Machines:
        - Automatic Bale Opener (Unifloc)
        - Axi-flow Cleaner: Uses two spiked rollers rotating in opposite directions.
        - Step Cleaner: 6 spiked rollers mounted on inclined axis.
        - Kirschner Beater: 3-striker pinned lag beater for fine opening.
        - Scutcher & Chute Feed System.
    """.trimIndent()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "📄 PDF & Syllabus Notes Analyzer",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "Syllabus, book chapter या नोट्स पेस्ट करें और AI से तुरंत विश्लेषण प्राप्त करें",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Paste Notes or Chapter Content:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Button(
                        onClick = { notesText = sampleNotes },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Load Sample Notes", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    placeholder = { Text("अपने Textile Syllabus / Notes का टेक्स्ट यहाँ पेस्ट करें...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("pdf_text_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Choose AI Action:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val actions = listOf(
                    "SUMMARY" to "📖 Chapter Summary",
                    "IMPORTANT_QUESTIONS" to "🎯 Important Questions",
                    "SHORT_NOTES" to "📝 Short Notes",
                    "MCQS" to "❓ Generate MCQs",
                    "VIVA" to "🗣️ Viva Questions"
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    actions.chunked(2).forEach { rowActions ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowActions.forEach { (actionKey, actionTitle) ->
                                Button(
                                    onClick = {
                                        if (notesText.isNotBlank()) {
                                            onAnalyzePdfText(notesText, actionKey)
                                            onNavigateToAskTab()
                                        }
                                    },
                                    enabled = notesText.isNotBlank(),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("pdf_action_$actionKey")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = actionTitle, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
