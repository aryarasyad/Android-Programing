package com.example.projectuas.presentation.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectuas.data.model.Journal
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    userData: com.example.projectuas.data.UserData?,
    journals: List<Journal>,
    onAddJournal: () -> Unit,
    onEditJournal: (Journal) -> Unit,
    onOpenDetail: (Journal) -> Unit,
    onDeleteJournal: (Journal) -> Unit,
    onSignOut: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("Semua") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var journalToDelete by remember { mutableStateOf<Journal?>(null) }

    val haptic = LocalHapticFeedback.current
    val moodOptions = listOf("Semua", "😊", "🔥", "😔", "😴", "🌿", "⭐")

    val filteredJournals = journals.filter {
        it.title.contains(searchQuery, true)
    }.filter {
        if (selectedMood == "Semua") true else it.mood == selectedMood
    }

    // Fungsi Warna Pastel untuk Background Putih
    fun getMoodColor(mood: String): Color {
        return when (mood) {
            "😊" -> Color(0xFFE8F5E9) // Hijau Pastel
            "🔥" -> Color(0xFFFFEBEE) // Merah Pastel
            "😔" -> Color(0xFFE3F2FD) // Biru Pastel
            "😴" -> Color(0xFFF3E5F5) // Ungu Pastel
            "🌿" -> Color(0xFFE0F2F1) // Toska Pastel
            "⭐" -> Color(0xFFFFFDE7) // Kuning Pastel
            else -> Color(0xFFF5F5F5) // Abu Default
        }
    }

    if (showDeleteDialog && journalToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Jurnal?") },
            text = { Text("Cerita '${journalToDelete?.title}' akan dihapus.") },
            confirmButton = {
                TextButton(onClick = {
                    journalToDelete?.let { onDeleteJournal(it) }
                    showDeleteDialog = false
                }) { Text("Hapus", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            Column(Modifier.background(Color(0xFFF8F9FA))) {
                CenterAlignedTopAppBar(
                    title = { Text("Daily Journal", fontWeight = FontWeight.ExtraBold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFF8F9FA),
                        titleContentColor = Color.Black
                    ),
                    actions = {
                        IconButton(onClick = onSignOut) {
                            Icon(Icons.Default.ExitToApp, null, tint = Color(0xFFD32F2F))
                        }
                    }
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    placeholder = { Text("Cari judul...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                LazyRow(Modifier.padding(vertical = 12.dp), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(moodOptions) { mood ->
                        FilterChip(
                            selected = selectedMood == mood,
                            onClick = { selectedMood = mood },
                            label = { Text(mood) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddJournal, containerColor = Color.Black, contentColor = Color.White) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 12.dp
        ) {
            items(filteredJournals, key = { it.id }) { journal ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            journalToDelete = journal
                            showDeleteDialog = true
                        }
                        false
                    }
                )

                SwipeToDismissBox(state = dismissState, enableDismissFromStartToEnd = false, backgroundContent = {
                    Box(Modifier.fillMaxSize().padding(vertical = 4.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(24.dp)), contentAlignment = Alignment.CenterEnd) {
                        Icon(Icons.Default.Delete, null, Modifier.padding(end = 16.dp), tint = Color(0xFFD32F2F))
                    }
                }) {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onOpenDetail(journal) },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = getMoodColor(journal.mood)),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                Box(Modifier.background(Color.White.copy(0.5f), CircleShape).padding(6.dp)) {
                                    Text(journal.mood, fontSize = 18.sp)
                                }
                                Text(text = formatShortDate(journal.createdAt), fontSize = 10.sp, color = Color.Gray)
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(text = journal.title, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}
fun formatShortDate(timestamp: com.google.firebase.Timestamp?): String {
    if (timestamp == null) return ""
    return SimpleDateFormat("dd MMM", Locale.getDefault()).format(timestamp.toDate())
}