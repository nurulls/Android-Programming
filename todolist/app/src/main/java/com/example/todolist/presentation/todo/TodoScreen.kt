package com.example.todolist.presentation.todo

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.todolist.data.model.UserData
import com.example.todolist.data.model.Todo


val SkyBlue = Color(0xFFB3E5FC)
val SkyBlueDark = Color(0xFF81D4FA)
val CardSky = Color(0xFFE3F2FD)
val BackgroundSoft = Color(0xFFF9FBFD)

val TextPrimary = Color.Black
val TextSecondary = Color(0xFF424242)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    userData: UserData?,
    viewModel: TodoViewModel,
    onSignOut: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    var todoText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("MEDIUM") }
    var expanded by remember { mutableStateOf(false) }
    val todos by viewModel.todos.collectAsState()

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.observeTodos(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Hari Ini 🌤",
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SkyBlue
                ),
                actions = {
                    userData?.let {
                        Text(
                            "Hi, ${it.username} ✨",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        AsyncImage(
                            model = it.profilePictureUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                        )
                        IconButton(onClick = onSignOut) {
                            Text("Out", color = TextPrimary)
                        }
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundSoft)
                .padding(padding)
                .padding(16.dp)
        ) {


            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardSky
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    TextField(
                        value = todoText,
                        onValueChange = { todoText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Tulis apa yang kamu rasakan hari ini… ✍️",
                                color = TextSecondary
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(color = TextPrimary),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }
                        ) {
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.menuAnchor(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    PriorityDot(selectedPriority)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        when (selectedPriority) {
                                            "HIGH" -> "Tinggi"
                                            "MEDIUM" -> "Sedang"
                                            "LOW" -> "Rendah"
                                            else -> "Priority"
                                        },
                                        color = TextPrimary
                                    )
                                }
                            }

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            PriorityDot("HIGH")
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Tinggi", color = TextPrimary)
                                        }
                                    },
                                    onClick = {
                                        selectedPriority = "HIGH"
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            PriorityDot("MEDIUM")
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Sedang", color = TextPrimary)
                                        }
                                    },
                                    onClick = {
                                        selectedPriority = "MEDIUM"
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            PriorityDot("LOW")
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Rendah", color = TextPrimary)
                                        }
                                    },
                                    onClick = {
                                        selectedPriority = "LOW"
                                        expanded = false
                                    }
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (todoText.isNotBlank()) {
                                    userData?.userId?.let {
                                        viewModel.add(it, todoText, selectedPriority)
                                    }
                                    todoText = ""
                                    selectedPriority = "MEDIUM"
                                }
                            },
                            enabled = todoText.isNotBlank(),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SkyBlueDark,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Tambah")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(todos, key = { it.id }) { todo ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically { it / 2 },
                        exit = fadeOut()
                    ) {
                        TodoItemCard(
                            todo = todo,
                            onToggle = {
                                userData?.userId?.let { uid ->
                                    viewModel.toggle(uid, todo)
                                }
                            },
                            onDelete = {
                                userData?.userId?.let { uid ->
                                    viewModel.delete(uid, todo.id)
                                }
                            },
                            onClick = { onNavigateToEdit(todo.id) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TodoItemCard(
    todo: Todo,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardSky
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = SkyBlueDark,
                    uncheckedColor = SkyBlueDark
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    todo.title,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Text(
                    todo.priority,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = SkyBlueDark
                )
            }
        }
    }
}


@Composable
fun PriorityDot(priority: String) {
    val color = when (priority) {
        "HIGH" -> Color(0xFFE53935)
        "MEDIUM" -> Color(0xFFFBC02D)
        "LOW" -> Color(0xFF43A047)
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color)
    )
}