package com.example.dailymooduas.data.repository

import com.example.dailymooduas.data.model.Mood
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MoodRepository {
    private val firestore = FirebaseFirestore.getInstance()

    private fun getMoodCollection(userId: String) =
        firestore.collection("users").document(userId).collection("moods")

    fun getMoods(userId: String): Flow<List<Mood>> = callbackFlow {
        val subscription = getMoodCollection(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val moods = snapshot.documents.mapNotNull {
                        it.toObject(Mood::class.java)?.copy(id = it.id)
                    }
                    trySend(moods)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addMood(userId: String, moodType: String, note: String) {
        val mood = Mood(moodType = moodType, note = note)
        getMoodCollection(userId).add(mood).await()
    }

    suspend fun updateMood(userId: String, moodId: String, moodType: String, note: String) {
        getMoodCollection(userId).document(moodId).update(
            mapOf(
                "moodType" to moodType,
                "note" to note
            )
        ).await()
    }

    suspend fun deleteMood(userId: String, moodId: String) {
        getMoodCollection(userId).document(moodId).delete().await()
    }
}