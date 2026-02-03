package com.example.tugaspert10.data.model

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
    var priority: String = Priority.MEDIUM.name
)

// TAMBAHKAN ENUM UNTUK PRIORITY
enum class Priority {
    LOW, MEDIUM, HIGH
}

