package com.example.dailymooduas.presentation.mood

import android.util.Log
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.dailymooduas.data.model.UserData
import com.example.dailymooduas.data.model.Mood
import com.example.dailymooduas.data.model.MoodType

// Color Palette
val SoftPink = Color(0xFFFFC1CC)
val SoftPinkDark = Color(0xFFFF9FB0)
val CardPink = Color(0xFFFFF0F3)
val BackgroundSoft = Color(0xFFFFFAFB)
val TextPrimary = Color(0xFF2D2D2D)
val TextSecondary = Color(0xFF666666)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodScreen(
    userData: UserData?,
    viewModel: MoodViewModel,
    onNavigateToProfile: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    var noteText by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("NEUTRAL") }
    var expanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<MoodType?>(null) }
    var searchQuery by remember { mutableStateOf("") } // State untuk search
    val moods by viewModel.moods.collectAsState()

    // Filter moods berdasarkan tipe yang dipilih DAN search query
    val filteredMoods = remember(moods, selectedFilter, searchQuery) {
        var result = moods

        // Filter berdasarkan mood type
        if (selectedFilter != null) {
            result = result.filter { it.moodType == selectedFilter!!.name }
        }

        // Filter berdasarkan search query
        if (searchQuery.isNotBlank()) {
            result = result.filter { mood ->
                mood.note.contains(searchQuery, ignoreCase = true) ||
                        MoodType.fromString(mood.moodType).label.contains(searchQuery, ignoreCase = true)
            }
        }

        result
    }

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.observeMoods(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Daily Mood 💭",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SoftPink
                ),
                actions = {
                    userData?.let { user ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                "Hi, ${user.username} ✨",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            // Avatar yang bisa diklik
                            AsyncImage(
                                model = user.profilePictureUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        Log.d("MoodScreen", "Avatar clicked, navigating to profile")
                                        onNavigateToProfile()
                                    }
                            )
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
            // Card Input Mood - DIPERKECIL
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardPink
                ),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Bagaimana perasaanmu? 🌸",
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mood Selector - Lebih compact
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${MoodType.fromString(selectedMood).emoji} ${MoodType.fromString(selectedMood).label}",
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                            }
                        }

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
                                            color = TextPrimary,
                                            fontSize = 14.sp
                                        )
                                    },
                                    onClick = {
                                        selectedMood = moodType.name
                                        expanded = false
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = TextPrimary
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Note Input - Lebih compact
                    TextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Ceritakan perasaanmu 💕",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        },
                        minLines = 2,
                        maxLines = 3,
                        textStyle = LocalTextStyle.current.copy(
                            color = TextPrimary,
                            fontSize = 13.sp
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = SoftPinkDark,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (noteText.isNotBlank()) {
                                userData?.userId?.let {
                                    viewModel.add(it, selectedMood, noteText)
                                }
                                noteText = ""
                                selectedMood = "NEUTRAL"
                            }
                        },
                        enabled = noteText.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftPinkDark,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "Simpan Mood",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SEARCH BAR - FITUR BARU
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Cari catatan mood...",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SoftPinkDark
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray
                ),
                shape = RoundedCornerShape(28.dp),
                textStyle = LocalTextStyle.current.copy(
                    color = TextPrimary,
                    fontSize = 14.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Header dengan jumlah mood
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Riwayat Mood 📖",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(
                    "${filteredMoods.size} mood",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Chip "Semua"
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = {
                            Text(
                                "Semua",
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Text("✨", fontSize = 16.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SoftPinkDark,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFilter == null,
                            borderColor = if (selectedFilter == null) SoftPinkDark else Color.LightGray,
                            selectedBorderColor = SoftPinkDark
                        )
                    )
                }

                // Chips untuk setiap mood type
                items(MoodType.values()) { moodType ->
                    val moodCount = moods.count { it.moodType == moodType.name }

                    FilterChip(
                        selected = selectedFilter == moodType,
                        onClick = { selectedFilter = moodType },
                        label = {
                            Text(
                                "${moodType.label} ($moodCount)",
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Text(moodType.emoji, fontSize = 16.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SoftPinkDark,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFilter == moodType,
                            borderColor = if (selectedFilter == moodType) SoftPinkDark else Color.LightGray,
                            selectedBorderColor = SoftPinkDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // List Mood dengan filtered data
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredMoods.isEmpty()) {
                    item {
                        // Empty state
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "🌸",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    when {
                                        searchQuery.isNotBlank() -> "Tidak ditemukan mood dengan '${searchQuery}'"
                                        selectedFilter != null -> "Tidak ada mood ${selectedFilter!!.label}"
                                        else -> "Belum ada mood yang tersimpan"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                } else {
                    items(filteredMoods, key = { it.id }) { mood ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically { it / 2 },
                            exit = fadeOut() + slideOutHorizontally { -it }
                        ) {
                            SwipeToDeleteMoodCard(
                                mood = mood,
                                onDelete = {
                                    userData?.userId?.let { uid ->
                                        viewModel.delete(uid, mood.id)
                                    }
                                },
                                onClick = { onNavigateToEdit(mood.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteMoodCard(
    mood: Mood,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
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
                    .background(SoftPinkDark, RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        enableDismissFromStartToEnd = false,
        content = {
            MoodItemCard(
                mood = mood,
                onClick = onClick
            )
        }
    )
}

@Composable
fun MoodItemCard(
    mood: Mood,
    onClick: () -> Unit
) {
    val moodType = MoodType.fromString(mood.moodType)
    val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault())
    val dateString = dateFormat.format(java.util.Date(mood.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = SoftPink
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        moodType.emoji,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            moodType.label,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                        Text(
                            dateString,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    mood.note,
                    modifier = Modifier.padding(12.dp),
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}