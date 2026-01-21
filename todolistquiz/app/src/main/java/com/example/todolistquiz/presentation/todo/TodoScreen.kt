package com.example.todolistquiz.presentation.todo

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.todolistquiz.data.model.UserData
import com.example.todolistquiz.data.model.Todo

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
    var selectedCategory by remember { mutableStateOf("Kerja") }
    var expandedPriority by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val todos by viewModel.todos.collectAsState()
    val currentFilter by viewModel.filter.collectAsState()

    // Filter dan search logic
    val filteredTodos = remember(todos, currentFilter, searchText) {
        var result = when(currentFilter) {
            TodoFilter.ALL -> todos
            TodoFilter.INCOMPLETE -> todos.filter { !it.isCompleted }
            is TodoFilter.BY_CATEGORY -> todos.filter {
                it.category == (currentFilter as TodoFilter.BY_CATEGORY).category
            }
        }

        if (searchText.isNotBlank()) {
            result = result.filter {
                it.title.contains(searchText, ignoreCase = true)
            }
        }

        result
    }

    // Hitung statistik
    val totalTodos = todos.size
    val completedTodos = todos.count { it.isCompleted }
    val progressPercentage = if (totalTodos > 0) (completedTodos * 100f / totalTodos) else 0f

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
                            Text("Out", color = TextPrimary, fontSize = 14.sp)
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
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Dashboard Statistik
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SkyBlueDark),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "📊 Progress Hari Ini",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { progressPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = Color(0xFF4CAF50),
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "$completedTodos dari $totalTodos tugas selesai",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "${progressPercentage.toInt()}%",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar - FIXED TEXT COLOR
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Cari tugas...",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = SkyBlueDark)
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SkyBlueDark,
                    unfocusedBorderColor = SkyBlue,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = SkyBlueDark
                ),
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 14.sp
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Tabs - FIXED SCROLLABLE
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = currentFilter == TodoFilter.ALL,
                        onClick = { viewModel.setFilter(TodoFilter.ALL) },
                        label = {
                            Text(
                                "Semua",
                                fontSize = 13.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SkyBlueDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter == TodoFilter.INCOMPLETE,
                        onClick = { viewModel.setFilter(TodoFilter.INCOMPLETE) },
                        label = {
                            Text(
                                "Belum Selesai",
                                fontSize = 13.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SkyBlueDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter is TodoFilter.BY_CATEGORY &&
                                (currentFilter as TodoFilter.BY_CATEGORY).category == "Kerja",
                        onClick = { viewModel.setFilter(TodoFilter.BY_CATEGORY("Kerja")) },
                        label = {
                            Text(
                                "💼 Kerja",
                                fontSize = 13.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SkyBlueDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter is TodoFilter.BY_CATEGORY &&
                                (currentFilter as TodoFilter.BY_CATEGORY).category == "Kuliah",
                        onClick = { viewModel.setFilter(TodoFilter.BY_CATEGORY("Kuliah")) },
                        label = {
                            Text(
                                "📚 Kuliah",
                                fontSize = 13.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SkyBlueDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter is TodoFilter.BY_CATEGORY &&
                                (currentFilter as TodoFilter.BY_CATEGORY).category == "Hobby",
                        onClick = { viewModel.setFilter(TodoFilter.BY_CATEGORY("Hobby")) },
                        label = {
                            Text(
                                "🎮 Hobby",
                                fontSize = 13.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SkyBlueDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Card - IMPROVED LAYOUT
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardSky),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    TextField(
                        value = todoText,
                        onValueChange = { todoText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Tulis tugasmu hari ini… ✍️",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        },
                        textStyle = TextStyle(
                            color = TextPrimary,
                            fontSize = 14.sp
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = SkyBlueDark
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Priority & Category Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Priority Dropdown
                        ExposedDropdownMenuBox(
                            expanded = expandedPriority,
                            onExpandedChange = { expandedPriority = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedButton(
                                onClick = { expandedPriority = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    PriorityDot(selectedPriority)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        when (selectedPriority) {
                                            "HIGH" -> "Tinggi"
                                            "MEDIUM" -> "Sedang"
                                            "LOW" -> "Rendah"
                                            else -> "Priority"
                                        },
                                        color = TextPrimary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            ExposedDropdownMenu(
                                expanded = expandedPriority,
                                onDismissRequest = { expandedPriority = false },
                                modifier = Modifier.background(CardSky)
                            ) {
                                listOf("HIGH" to "Tinggi", "MEDIUM" to "Sedang", "LOW" to "Rendah").forEach { (value, label) ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                PriorityDot(value)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(label, color = TextPrimary, fontSize = 14.sp)
                                            }
                                        },
                                        onClick = {
                                            selectedPriority = value
                                            expandedPriority = false
                                        },
                                        colors = MenuDefaults.itemColors(
                                            textColor = TextPrimary
                                        )
                                    )
                                }
                            }
                        }

                        // Category Dropdown
                        ExposedDropdownMenuBox(
                            expanded = expandedCategory,
                            onExpandedChange = { expandedCategory = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedButton(
                                onClick = { expandedCategory = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    when (selectedCategory) {
                                        "Kerja" -> "💼 Kerja"
                                        "Kuliah" -> "📚 Kuliah"
                                        "Hobby" -> "🎮 Hobby"
                                        else -> "Kategori"
                                    },
                                    color = TextPrimary,
                                    fontSize = 12.sp
                                )
                            }

                            ExposedDropdownMenu(
                                expanded = expandedCategory,
                                onDismissRequest = { expandedCategory = false },
                                modifier = Modifier.background(CardSky)
                            ) {
                                listOf("Kerja" to "💼", "Kuliah" to "📚", "Hobby" to "🎮").forEach { (category, emoji) ->
                                    DropdownMenuItem(
                                        text = { Text("$emoji $category", color = TextPrimary, fontSize = 14.sp) },
                                        onClick = {
                                            selectedCategory = category
                                            expandedCategory = false
                                        },
                                        colors = MenuDefaults.itemColors(
                                            textColor = TextPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Add Button
                    Button(
                        onClick = {
                            if (todoText.isNotBlank()) {
                                userData?.userId?.let {
                                    viewModel.add(it, todoText, selectedPriority, selectedCategory)
                                }
                                todoText = ""
                                selectedPriority = "MEDIUM"
                                selectedCategory = "Kerja"
                            }
                        },
                        enabled = todoText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkyBlueDark,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text("Tambah", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Todo List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = filteredTodos,
                    key = { it.id }
                ) { todo ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                userData?.userId?.let { uid ->
                                    viewModel.delete(uid, todo.id)
                                }
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFE53935), RoundedCornerShape(22.dp))
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        },
                        enableDismissFromStartToEnd = false,
                        enableDismissFromEndToStart = true
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

            Spacer(modifier = Modifier.height(16.dp))
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
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardSky
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        when (todo.category) {
                            "Kerja" -> "💼"
                            "Kuliah" -> "📚"
                            "Hobby" -> "🎮"
                            else -> ""
                        },
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    PriorityDot(todo.priority)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        todo.title,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "${todo.category} • ${when(todo.priority) {
                        "HIGH" -> "Tinggi"
                        "MEDIUM" -> "Sedang"
                        "LOW" -> "Rendah"
                        else -> todo.priority
                    }}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(22.dp)
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