package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamQuestionItem
import com.example.data.model.TextileSyllabusData

@Composable
fun ExamModeScreen(
    onAskQuestion: (String, String) -> Unit, // prompt, format
    onNavigateToAskTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("MCQ") }

    val categories = listOf(
        "MCQ" to "MCQ Practice",
        "1M" to "1 Mark",
        "2M" to "2 Marks",
        "5M" to "5 Marks",
        "10M" to "10 Marks",
        "VIVA" to "Viva Questions"
    )

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
                imageVector = Icons.Default.Quiz,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "📝 Diploma Exam Preparation Mode",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    text = "1M, 2M, 5M, 10M & Viva Questions directly structured for board exams",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Category Filter Tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            items(categories) { (key, title) ->
                val isSelected = selectedCategory == key
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .testTag("exam_cat_$key")
                        .clickable { selectedCategory = key }
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        if (selectedCategory == "MCQ") {
            McqPracticeSection()
        } else {
            ExamQuestionsList(
                categoryKey = selectedCategory,
                onAskAiDetail = { q ->
                    val format = when (selectedCategory) {
                        "1M", "2M" -> "SHORT"
                        "5M", "10M" -> "LONG"
                        "VIVA" -> "DEFINITION"
                        else -> "AUTO"
                    }
                    onAskQuestion(q, format)
                    onNavigateToAskTab()
                }
            )
        }
    }
}

@Composable
private fun McqPracticeSection() {
    val mcqs = TextileSyllabusData.sampleMcqs
    var currentMcqIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }

    val item = mcqs[currentMcqIndex]

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "Question ${currentMcqIndex + 1} of ${mcqs.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "Score: $score",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = item.question,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    item.options?.forEachIndexed { index, option ->
                        val isCorrect = item.correctOptionIndex == index
                        val isUserSelected = selectedOptionIndex == index

                        val bgColor = when {
                            selectedOptionIndex == null -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            isUserSelected && isCorrect -> Color(0xFFDCFCE7)
                            isUserSelected && !isCorrect -> Color(0xFFFEE2E2)
                            isCorrect -> Color(0xFFDCFCE7)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        }

                        val borderColor = when {
                            selectedOptionIndex == null -> Color.Transparent
                            isCorrect -> Color(0xFF16A34A)
                            isUserSelected && !isCorrect -> Color(0xFFDC2626)
                            else -> Color.Transparent
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = bgColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(enabled = selectedOptionIndex == null) {
                                    selectedOptionIndex = index
                                    if (index == item.correctOptionIndex) {
                                        score++
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${('A' + index)}. ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = option,
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                if (selectedOptionIndex != null) {
                                    if (isCorrect) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Correct",
                                            tint = Color(0xFF16A34A),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else if (isUserSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Wrong",
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (selectedOptionIndex != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "💡 Explanation: ${item.answer}",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                selectedOptionIndex = null
                                currentMcqIndex = (currentMcqIndex + 1) % mcqs.size
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Next Question")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExamQuestionsList(
    categoryKey: String,
    onAskAiDetail: (String) -> Unit
) {
    val questions = when (categoryKey) {
        "1M", "2M" -> listOf(
            "Kirschner Beater ka main function kya hai?",
            "Sizing process ka primary objective likho.",
            "Shuttle speed kitni hoti hai?",
            "Count (Ne) ki definition batao."
        )
        "5M" -> listOf(
            "Rapier loom ke working mechanism aur features explain karo.",
            "Blow room line ke main objectives aur machines ki list banao.",
            "Feed Roller aur Pedal Roller mein difference explain karo.",
            "Uster Evenness Tester ke principle aur working ki व्याख्या करो।"
        )
        "10M" -> listOf(
            "Explain complete Spinning process from Cotton Bales to Ring Spinning Frame with machine diagrams.",
            "Explain Modern Shuttleless Looms (Air Jet, Water Jet, Rapier, Projectile) with full comparative analysis.",
            "Weaving Preparatory processes (Warping, Sizing, Drawing-in, Denting) detail mein samjhao."
        )
        "VIVA" -> listOf(
            "Blow room mein trash removal efficiency kitni hoti hai?",
            "CV% aur U% mein kya relation hota hai?",
            "Rapier loom mein Giver aur Taker rapier ka role kya hai?",
            "Sizing material mein starch aur lubricant kyun mix karte hain?"
        )
        else -> listOf("Textile Technology Important Questions")
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(questions) { q ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Q: $q",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onAskAiDetail("Give a detailed $categoryKey board exam answer for: $q") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Get Exam Answer", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
