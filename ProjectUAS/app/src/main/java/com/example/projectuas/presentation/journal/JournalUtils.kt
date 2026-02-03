package com.example.projectuas.presentation.journal

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

fun formatDate(timestamp: Timestamp?): String {
    if (timestamp == null) return ""
    val sdf = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale("id", "ID"))
    return sdf.format(timestamp.toDate())
}
