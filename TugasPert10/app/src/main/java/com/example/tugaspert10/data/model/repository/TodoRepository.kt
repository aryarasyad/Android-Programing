package com.example.tugaspert10.data.model.repository

import com.example.tugaspert10.data.model.Priority
import com.example.tugaspert10.data.model.Todo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TodoRepository {
    private val firestore = FirebaseFirestore.getInstance()

    private fun getTodoCollection(userId: String) =
        firestore.collection("users").document(userId).collection("todos")

    fun getTodos(userId: String): Flow<List<Todo>> = callbackFlow {
        val subscription = getTodoCollection(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Tangani error
                    println("ERROR getting todos: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val todos = snapshot.documents.mapNotNull {
                        try {
                            it.toObject(Todo::class.java)?.copy(id = it.id)
                        } catch (e: Exception) {
                            null // Skip dokumen yang error
                        }
                    }
                    trySend(todos)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addTodo(userId: String, title: String, priority: Priority = Priority.MEDIUM) {
        try {
            val todo = Todo(title = title, priority = priority.name)
            getTodoCollection(userId).add(todo).await()
            println("SUCCESS: Todo added for userId: $userId")
        } catch (e: Exception) {
            println("ERROR adding todo: ${e.message}")
            e.printStackTrace()
            throw e // Lempar ulang
        }
    }

    suspend fun updateTodoStatus(userId: String, todoId: String, isCompleted: Boolean) {
        try {
            getTodoCollection(userId).document(todoId)
                .update("isCompleted", isCompleted)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback
            try {
                getTodoCollection(userId).document(todoId)
                    .set(mapOf("isCompleted" to isCompleted), SetOptions.merge())
                    .await()
            } catch (e2: Exception) {
                println("Fallback juga gagal: ${e2.message}")
                throw e2
            }
        }
    }

    suspend fun updateTodoTitle(userId: String, todoId: String, newTitle: String) {
        try {
            getTodoCollection(userId).document(todoId)
                .update("title", newTitle)
                .await()
        } catch (e: Exception) {
            println("ERROR updating title: ${e.message}")
            throw e
        }
    }

    suspend fun updateTodoPriority(userId: String, todoId: String, priority: Priority) {
        try {
            getTodoCollection(userId).document(todoId)
                .update("priority", priority.name)
                .await()
        } catch (e: Exception) {
            println("ERROR updating priority: ${e.message}")
            throw e
        }
    }

    suspend fun deleteTodo(userId: String, todoId: String) {
        try {
            getTodoCollection(userId).document(todoId).delete().await()
        } catch (e: Exception) {
            println("ERROR deleting todo: ${e.message}")
            throw e
        }
    }
}