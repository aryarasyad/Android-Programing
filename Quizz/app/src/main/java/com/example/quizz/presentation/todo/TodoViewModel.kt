package com.example.quizz.presentation.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizz.data.model.Category
import com.example.quizz.data.model.Priority
import com.example.quizz.data.model.Todo
import com.example.quizz.data.repository.TodoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// MODEL DATA UNTUK STATISTIK
data class TodoStatistics(
    val total: Int = 0,
    val completed: Int = 0,
    val progress: Float = 0f // Nilai 0.0 sampai 1.0
)

// DEFINISI FILTER
sealed class TodoFilter {
    data object All : TodoFilter()
    data object Active : TodoFilter()
    data class ByCategory(val category: Category) : TodoFilter()
}

class TodoViewModel : ViewModel() {
    private val repository = TodoRepository()

    // 1. DATA SUMBER (RAW DATA)
    private val _sourceTodos = MutableStateFlow<List<Todo>>(emptyList())

    // 2. STATE PENCARIAN & FILTER
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterState = MutableStateFlow<TodoFilter>(TodoFilter.All)
    val filterState = _filterState.asStateFlow()

    // 3. STATISTIK (Dihitung dari _sourceTodos agar konsisten meski sedang difilter)
    val statistics: StateFlow<TodoStatistics> = _sourceTodos.map { todos ->
        val total = todos.size
        val completed = todos.count { it.isCompleted }
        val progress = if (total > 0) completed.toFloat() / total else 0f

        TodoStatistics(total, completed, progress)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodoStatistics()
    )

    // 4. LOGIKA PENGGABUNGAN (FILTERING LIST TUGAS)
    val todos = combine(_sourceTodos, _searchQuery, _filterState) { todos, query, filter ->
        todos.filter { todo ->
            // Filter Search
            val matchesSearch = if (query.isBlank()) true else {
                todo.title.contains(query, ignoreCase = true)
            }

            // Filter Kategori/Status
            val matchesFilter = when (filter) {
                is TodoFilter.All -> true
                is TodoFilter.Active -> !todo.isCompleted
                is TodoFilter.ByCategory -> todo.category == filter.category.name
            }

            matchesSearch && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun observeTodos(userId: String) {
        viewModelScope.launch {
            repository.getTodos(userId).collect {
                _sourceTodos.value = it
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChange(filter: TodoFilter) {
        _filterState.value = filter
    }

    // --- FUNGSI CRUD ---

    fun add(userId: String, title: String, priority: Priority, category: Category) = viewModelScope.launch {
        try {
            repository.addTodo(userId, title, priority, category)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggle(userId: String, todoId: String, isCompleted: Boolean) = viewModelScope.launch {
        repository.updateTodoStatus(userId, todoId, isCompleted)
    }

    fun updateTitle(userId: String, todoId: String, newTitle: String) = viewModelScope.launch {
        repository.updateTodoTitle(userId, todoId, newTitle)
    }

    fun updatePriority(userId: String, todoId: String, priority: Priority) = viewModelScope.launch {
        repository.updateTodoPriority(userId, todoId, priority)
    }

    fun updateCategory(userId: String, todoId: String, category: Category) = viewModelScope.launch {
        repository.updateTodoCategory(userId, todoId, category)
    }

    fun delete(userId: String, todoId: String) = viewModelScope.launch {
        repository.deleteTodo(userId, todoId)
    }
}