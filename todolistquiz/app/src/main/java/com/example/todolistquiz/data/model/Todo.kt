package com.example.todolistquiz.data.model

import com.google.firebase.firestore.PropertyName

data class Todo(
    val id: String = "",
    val title: String = "",

    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val category: String = "Kerja" // Kerja, Kuliah, Hobby
)

enum class Priority {
    HIGH, MEDIUM, LOW;

    fun toDisplayString(): String = when(this) {
        HIGH -> "Tinggi"
        MEDIUM -> "Sedang"
        LOW -> "Rendah"
    }
}

enum class Category {
    KERJA, KULIAH, HOBBY;

    fun toDisplayString(): String = when(this) {
        KERJA -> "Kerja"
        KULIAH -> "Kuliah"
        HOBBY -> "Hobby"
    }

    fun toEmoji(): String = when(this) {
        KERJA -> "💼"
        KULIAH -> "📚"
        HOBBY -> "🎮"
    }
}