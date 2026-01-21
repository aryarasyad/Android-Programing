package com.example.quizz.presentation.todo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizz.data.model.Category
import com.example.quizz.data.model.Priority
import com.example.quizz.data.model.Todo
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoScreen(
    todo: Todo,
    onSave: (String, Priority, Category) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(todo.title) }
    var selectedPriority by remember {
        mutableStateOf(try { Priority.valueOf(todo.priority) } catch (e: Exception) { Priority.MEDIUM })
    }
    var selectedCategory by remember {
        mutableStateOf(try { Category.valueOf(todo.category) } catch (e: Exception) { Category.LAINNYA })
    }

    // State untuk Dropdown
    var isPriorityExpanded by remember { mutableStateOf(false) }
    var isCategoryExpanded by remember { mutableStateOf(false) }

    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateString = sdf.format(Date(todo.createdAt))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Tugas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Tugas") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // DROPDOWN PRIORITY
            ExposedDropdownMenuBox(
                expanded = isPriorityExpanded,
                onExpandedChange = { isPriorityExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedPriority.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    leadingIcon = {
                        Icon(
                            imageVector = when (selectedPriority) {
                                Priority.LOW -> Icons.Default.ArrowDownward
                                Priority.MEDIUM -> Icons.Default.HorizontalRule
                                Priority.HIGH -> Icons.Default.ArrowUpward
                            },
                            contentDescription = null
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPriorityExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isPriorityExpanded,
                    onDismissRequest = { isPriorityExpanded = false }
                ) {
                    Priority.entries.forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority.name) },
                            onClick = {
                                selectedPriority = priority
                                isPriorityExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (priority) {
                                        Priority.LOW -> Icons.Default.ArrowDownward
                                        Priority.MEDIUM -> Icons.Default.HorizontalRule
                                        Priority.HIGH -> Icons.Default.ArrowUpward
                                    },
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DROPDOWN CATEGORY
            ExposedDropdownMenuBox(
                expanded = isCategoryExpanded,
                onExpandedChange = { isCategoryExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategori") },
                    leadingIcon = { Icon(Icons.Default.Label, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isCategoryExpanded,
                    onDismissRequest = { isCategoryExpanded = false }
                ) {
                    Category.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                isCategoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Dibuat pada: $dateString", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, selectedPriority, selectedCategory)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Simpan Perubahan")
            }
        }
    }
}