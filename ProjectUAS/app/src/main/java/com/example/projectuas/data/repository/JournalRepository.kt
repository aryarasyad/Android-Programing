package com.example.projectuas.data.repository

import com.example.projectuas.data.model.Journal
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class JournalRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getJournals(userId: String): Flow<List<Journal>> = callbackFlow {
        val listener = db.collection("journals")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val journals = snapshot?.documents?.mapNotNull {
                    it.toObject(Journal::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(journals)
            }
        awaitClose { listener.remove() }
    }

    // Di JournalRepository.kt
    suspend fun addJournal(journal: Journal) {
        try {
            db.collection("journals")
                .add(journal)
                .addOnSuccessListener { println("DEBUG: Berhasil masuk ke Firestore!") }
                .addOnFailureListener { e -> println("DEBUG: Gagal ke Firestore: ${e.message}") }
                .await()
        } catch (e: Exception) {
            println("DEBUG: Error Exception: ${e.message}")
        }
    }

    // Ubah bagian ini di JournalRepository.kt
    suspend fun updateJournal(journalId: String, title: String, content: String, mood: String) {
        db.collection("journals")
            .document(journalId)
            .update(
                mapOf(
                    "title" to title,
                    "content" to content,
                    "mood" to mood // Tambahkan baris ini agar mood ikut terupdate di Firebase
                )
            ).await()
    }

    suspend fun deleteJournal(journalId: String) {
        db.collection("journals").document(journalId).delete().await()
    }
}
