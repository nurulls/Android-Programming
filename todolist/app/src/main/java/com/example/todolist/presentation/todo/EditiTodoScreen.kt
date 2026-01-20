package com.example.todolist.presentation.todo

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
import com.example.todolist.data.model.Todo
import java.text.SimpleDateFormat
import java.util.*

val BlueSky = Color(0xFFBEE7E8)
val BlueSkySoft = Color(0xFFEAF7F8)
val BlueSkyDark = Color(0xFF7ECED4)
val TextBlack = Color(0xFF000000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(
    todo: Todo,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(todo.title) }
    var selectedPriority by remember { mutableStateOf(todo.priority) }
    var expanded by remember { mutableStateOf(false) }

    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateString = sdf.format(Date(todo.createdAt))

    Scaffold(
        containerColor = BlueSkySoft,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Tugas",
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
                    containerColor = BlueSky
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
                    containerColor = BlueSkySoft
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "📝 Detail Tugas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = {
                            Text(
                                "Judul Tugas",
                                color = TextBlack
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BlueSkyDark,
                            unfocusedBorderColor = BlueSkyDark,
                            focusedTextColor = TextBlack,
                            unfocusedTextColor = TextBlack,
                            cursorColor = BlueSkyDark
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Prioritas",
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
                            value = when (selectedPriority) {
                                "HIGH" -> "🔴 Tinggi"
                                "MEDIUM" -> "🟡 Sedang"
                                "LOW" -> "🟢 Rendah"
                                else -> "Pilih Priority"
                            },
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
                                focusedBorderColor = BlueSkyDark,
                                unfocusedBorderColor = BlueSkyDark,
                                focusedTextColor = TextBlack,
                                unfocusedTextColor = TextBlack
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🔴 Tinggi", color = TextBlack) },
                                onClick = {
                                    selectedPriority = "HIGH"
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🟡 Sedang", color = TextBlack) },
                                onClick = {
                                    selectedPriority = "MEDIUM"
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🟢 Rendah", color = TextBlack) },
                                onClick = {
                                    selectedPriority = "LOW"
                                    expanded = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = BlueSky
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "🕐 Dibuat: ",
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
                onClick = { onSave(title, selectedPriority) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueSkyDark,
                    contentColor = TextBlack
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
