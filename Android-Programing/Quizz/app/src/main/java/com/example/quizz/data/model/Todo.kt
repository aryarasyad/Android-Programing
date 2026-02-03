package com.example.quizz.data.model


import com.google.firebase.firestore.PropertyName

data class Todo (
    val id: String = "",
    val title: String = "",

    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),

    @get:PropertyName("priority")
    @set:PropertyName("priority")
    var priority: String = Priority.MEDIUM.name,

    @get:PropertyName("category")
    @set:PropertyName("category")
    var category: String = Category.LAINNYA.name
)

enum class Priority {
    LOW, MEDIUM, HIGH
}

enum class Category {
    KERJA, KULIAH, HOBBY, LAINNYA
}