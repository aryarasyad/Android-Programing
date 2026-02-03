package com.example.projectuas.presentation.journal


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectuas.data.model.Journal
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailJournalScreen(
    journal: Journal,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Jurnal?") },
            text = { Text("Cerita ini akan dihapus permanen.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) { Text("Hapus", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Detail Cerita", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.Black) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.White, tonalElevation = 4.dp) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color(0xFFD32F2F))
                    ) { Text("Hapus Cerita", color = Color(0xFFD32F2F)) }

                    Button(
                        onClick = onEdit,
                        Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) { Text("Edit Cerita", color = Color.White) }
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Text(journal.mood, fontSize = 72.sp, modifier = Modifier.align(Alignment.CenterHorizontally))

            Spacer(Modifier.height(8.dp))

            Text(
                getMoodNameDetail(journal.mood),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray // 🔥 Lebih gelap
            )

            Spacer(Modifier.height(32.dp))

            Text(formatFullDate(journal.createdAt), color = Color(0xFF757575), fontSize = 14.sp)

            Spacer(Modifier.height(12.dp))

            // Judul Hitam Pekat
            Text(
                journal.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold
            )

            // Divider lebih terlihat
            HorizontalDivider(Modifier.padding(vertical = 20.dp), thickness = 1.dp, color = Color(0xFFE0E0E0))

            // Konten Hitam dengan opacity 0.9 agar tidak terlalu kontras menyakitkan mata tapi jelas
            Text(
                journal.content,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black.copy(0.9f),
                lineHeight = 30.sp
            )
        }
    }
}

// --- FUNGSI HELPER (PASTIKAN ADA DI LUAR @COMPOSABLE) ---

fun formatFullDate(timestamp: com.google.firebase.Timestamp?): String {
    if (timestamp == null) return "Tanggal tidak tersedia"
    return SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(timestamp.toDate())
}

fun getMoodNameDetail(emoji: String): String = when(emoji) {
    "😊" -> "Bahagia"
    "🔥" -> "Semangat"
    "😔" -> "Sedih"
    "😴" -> "Ngantuk"
    "🌿" -> "Tenang"
    "⭐" -> "Istimewa"
    else -> "Biasa Saja"
}