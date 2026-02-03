package com.example.dailymooduas.presentation.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailymooduas.data.model.Mood
import com.example.dailymooduas.data.repository.MoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoodViewModel : ViewModel() {
    private val repository = MoodRepository()
    private val _moods = MutableStateFlow<List<Mood>>(emptyList())
    val moods = _moods.asStateFlow()

    fun observeMoods(userId: String) {
        viewModelScope.launch {
            repository.getMoods(userId).collect { moodList ->
                _moods.value = moodList.sortedByDescending { it.createdAt }
            }
        }
    }

    fun add(userId: String, moodType: String, note: String) = viewModelScope.launch {
        repository.addMood(userId, moodType, note)
    }

    fun updateMood(userId: String, moodId: String, moodType: String, note: String) =
        viewModelScope.launch {
            repository.updateMood(userId, moodId, moodType, note)
        }

    fun delete(userId: String, moodId: String) = viewModelScope.launch {
        repository.deleteMood(userId, moodId)
    }
}