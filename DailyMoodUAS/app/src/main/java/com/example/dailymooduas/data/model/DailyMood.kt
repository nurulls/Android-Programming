package com.example.dailymooduas.data.model

import com.google.firebase.firestore.PropertyName

data class Mood(
    val id: String = "",
    val moodType: String = "NEUTRAL", // VERY_GOOD, GOOD, NEUTRAL, BAD, VERY_BAD
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)


enum class MoodType(val emoji: String, val label: String) {
    VERY_GOOD("😄", "Sangat Baik"),
    GOOD("🙂", "Baik"),
    NEUTRAL("😐", "Biasa Saja"),
    BAD("😔", "Buruk"),
    VERY_BAD("😢", "Sangat Buruk");

    companion object {
        fun fromString(value: String): MoodType {
            return values().find { it.name == value } ?: NEUTRAL
        }
    }
}