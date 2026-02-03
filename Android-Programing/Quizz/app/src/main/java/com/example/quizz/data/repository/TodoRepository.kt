package com.example.quizz.data.repository

import com.example.quizz.data.model.Category
import com.example.quizz.data.model.Priority
import com.example.quizz.data.model.Todo
import com.google.firebase.firestore.FirebaseFirestore
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
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val todos = snapshot.documents.mapNotNull {
                        try {
                            it.toObject(Todo::class.java)?.copy(id = it.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(todos)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addTodo(userId: String, title: String, priority: Priority, category: Category) {
        try {
            val todo = Todo(
                title = title,
                priority = priority.name,
                category = category.name
            )
            getTodoCollection(userId).add(todo).await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun updateTodoStatus(userId: String, todoId: String, isCompleted: Boolean) {
        try {
            getTodoCollection(userId).document(todoId)
                .update("isCompleted", isCompleted)
                .await()
        } catch (e: Exception) {
            // Fallback jika dokumen belum ada fieldnya
            getTodoCollection(userId).document(todoId)
                .set(mapOf("isCompleted" to isCompleted), SetOptions.merge())
                .await()
        }
    }

    suspend fun updateTodoTitle(userId: String, todoId: String, newTitle: String) {
        getTodoCollection(userId).document(todoId).update("title", newTitle).await()
    }

    suspend fun updateTodoPriority(userId: String, todoId: String, priority: Priority) {
        getTodoCollection(userId).document(todoId).update("priority", priority.name).await()
    }

    suspend fun updateTodoCategory(userId: String, todoId: String, category: Category) {
        getTodoCollection(userId).document(todoId).update("category", category.name).await()
    }

    suspend fun deleteTodo(userId: String, todoId: String) {
        getTodoCollection(userId).document(todoId).delete().await()
    }
}