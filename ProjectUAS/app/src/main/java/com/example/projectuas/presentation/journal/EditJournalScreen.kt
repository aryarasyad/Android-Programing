package com.example.projectuas.presentation.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projectuas.data.model.Journal
import com.example.projectuas.ui.theme.ProjectUASTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJournalScreen(
    journal: Journal?,
    onBack: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(journal?.title ?: "") }
    var content by remember { mutableStateOf(journal?.content ?: "") }
    var selectedMood by remember { mutableStateOf(journal?.mood ?: "😊") }

    val moodOptions = listOf("😊", "🔥", "😔", "😴", "🌿", "⭐")

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text(if (journal == null) "Tulis Cerita" else "Perbarui Cerita", fontWeight = FontWeight.Bold, color = Color.Black) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.Black) } },
                actions = {
                    TextButton(onClick = { if (title.isNotBlank()) onSave(title, content, selectedMood) }, enabled = title.isNotBlank()) {
                        Text("Simpan", fontWeight = FontWeight.Bold, color = if (title.isNotBlank()) Color.Black else Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp).verticalScroll(rememberScrollState())) {
            Text("Bagaimana harimu?", color = Color.DarkGray, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(moodOptions) { mood ->
                    val isSelected = selectedMood == mood
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(color = if (isSelected) Color.Black else Color(0xFFF5F5F5), shape = RoundedCornerShape(16.dp))
                            .clickable { selectedMood = mood },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(mood, fontSize = 24.sp)
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            // Judul: Teks Hitam Pekat
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Berikan Judul...", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.Black, // 🔥 Paksa Hitam
                    unfocusedTextColor = Color.Black, // 🔥 Paksa Hitam
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color(0xFFEEEEEE)
                ),
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(20.dp))

            // Konten: Teks Abu-abu Gelap agar nyaman dibaca
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Tuliskan perasaanmu...", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 400.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color(0xFF333333), // 🔥 Abu gelap pekat
                    unfocusedTextColor = Color(0xFF333333),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp)
            )
        }
    }
}
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun EditPreview() {
    ProjectUASTheme {
        // Berikan nilai null untuk parameter journal
        EditJournalScreen(
            journal = null,
            onBack = {},
            onSave = { _, _, _ -> }
        )
    }
}