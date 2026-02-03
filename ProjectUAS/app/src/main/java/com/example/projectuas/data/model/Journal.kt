package com.example.projectuas.data.model

import com.google.firebase.Timestamp

data class Journal(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val mood: String = "😊",
    val createdAt: Timestamp? = null
)
