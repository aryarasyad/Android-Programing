package com.example.tugaspert10.presentation.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugaspert10.data.model.Priority
import com.example.tugaspert10.data.model.Todo
import com.example.tugaspert10.data.model.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {
    private val repository = TodoRepository()
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos = _todos.asStateFlow()

    fun observeTodos(userId: String) {
        viewModelScope.launch {
            repository.getTodos(userId).collect { todos ->
                println("DEBUG: Todos updated - ${todos.size} items")
                _todos.value = todos
            }
        }
    }

    // PERBAIKAN: Tambahkan parameter priority
    fun add(userId: String, title: String, priority: Priority = Priority.MEDIUM) = viewModelScope.launch {
        try {
            println("DEBUG: Adding todo - userId: $userId, title: $title, priority: $priority")
            repository.addTodo(userId, title, priority)
        } catch (e: Exception) {
            println("ERROR in ViewModel.add: ${e.message}")
            e.printStackTrace()
        }
    }

    // PERBAIKAN: Fungsi toggle yang benar
    fun toggle(userId: String, todoId: String, isCompleted: Boolean) = viewModelScope.launch {
        try {
            println("DEBUG: Toggling todo - userId: $userId, todoId: $todoId, isCompleted: $isCompleted")
            repository.updateTodoStatus(userId, todoId, isCompleted)
        } catch (e: Exception) {
            println("ERROR toggling todo: ${e.message}")
        }
    }

    fun updateTitle(userId: String, todoId: String, newTitle: String) = viewModelScope.launch {
        try {
            println("DEBUG: Updating title - userId: $userId, todoId: $todoId, newTitle: $newTitle")
            repository.updateTodoTitle(userId, todoId, newTitle)
        } catch (e: Exception) {
            println("ERROR updating title: ${e.message}")
        }
    }

    fun delete(userId: String, todoId: String) = viewModelScope.launch {
        try {
            println("DEBUG: Deleting todo - userId: $userId, todoId: $todoId")
            repository.deleteTodo(userId, todoId)
        } catch (e: Exception) {
            println("ERROR deleting todo: ${e.message}")
        }
    }

    fun updatePriority(userId: String, todoId: String, priority: Priority) = viewModelScope.launch {
        try {
            println("DEBUG: Updating priority - userId: $userId, todoId: $todoId, priority: $priority")
            repository.updateTodoPriority(userId, todoId, priority)
        } catch (e: Exception) {
            println("ERROR updating priority: ${e.message}")
        }
    }
}