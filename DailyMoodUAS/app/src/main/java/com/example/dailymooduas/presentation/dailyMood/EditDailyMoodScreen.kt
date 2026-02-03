package com.example.dailymooduas.presentation.mood

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailymooduas.data.model.Mood
import com.example.dailymooduas.data.model.MoodType
import java.text.SimpleDateFormat
import java.util.*

val EditPink = Color(0xFFFFC1CC)
val EditPinkSoft = Color(0xFFFFF0F3)
val EditPinkDark = Color(0xFFFF9FB0)
val TextBlack = Color(0xFF2D2D2D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMoodScreen(
    mood: Mood,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var note by remember { mutableStateOf(mood.note) }
    var selectedMood by remember { mutableStateOf(mood.moodType) }
    var expanded by remember { mutableStateOf(false) }

    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateString = sdf.format(Date(mood.createdAt))

    Scaffold(
        containerColor = EditPinkSoft,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Mood",
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EditPink
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "💭 Detail Mood",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Perasaan Kamu",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextBlack
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = "${MoodType.fromString(selectedMood).emoji} ${MoodType.fromString(selectedMood).label}",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EditPinkDark,
                                unfocusedBorderColor = EditPinkDark,
                                focusedTextColor = TextBlack,
                                unfocusedTextColor = TextBlack
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            containerColor = Color.White
                        ) {
                            MoodType.values().forEach { moodType ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "${moodType.emoji} ${moodType.label}",
                                            color = TextBlack,
                                            fontSize = 16.sp
                                        )
                                    },
                                    onClick = {
                                        selectedMood = moodType.name
                                        expanded = false
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = TextBlack
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Catatan",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextBlack
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Ceritakan perasaanmu...", color = Color.Gray)
                        },
                        minLines = 4,
                        maxLines = 8,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EditPinkDark,
                            unfocusedBorderColor = EditPinkDark,
                            focusedTextColor = TextBlack,
                            unfocusedTextColor = TextBlack,
                            cursorColor = EditPinkDark
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = EditPinkSoft
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "🕐 Dicatat: ",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                            Text(
                                text = dateString,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextBlack
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSave(selectedMood, note) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = note.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EditPinkDark,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Simpan Perubahan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}