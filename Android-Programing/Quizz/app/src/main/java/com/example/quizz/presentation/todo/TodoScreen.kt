package com.example.quizz.presentation.todo

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.quizz.data.UserData
import com.example.quizz.data.model.Category
import com.example.quizz.data.model.Priority
import com.example.quizz.data.model.Todo


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun TodoScreen(
    userData: UserData?,
    viewModel: TodoViewModel,
    onSignOut: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    // State untuk Input Todo Baru
    var todoText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var selectedCategory by remember { mutableStateOf(Category.LAINNYA) }

    var isPriorityExpanded by remember { mutableStateOf(false) }
    var isCategoryExpanded by remember { mutableStateOf(false) }

    // State dari ViewModel
    val todos by viewModel.todos.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterState by viewModel.filterState.collectAsState()

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.observeTodos(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Todo List", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                actions = {
                    userData?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            AsyncImage(
                                model = it.profilePictureUrl,
                                contentDescription = "Profile",
                                modifier = Modifier.size(40.dp).clip(CircleShape)
                            )
                            IconButton(onClick = onSignOut) {
                                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Sign Out", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            // --- 1. DASHBOARD STATISTIK ---
            DashboardCard(
                total = statistics.total,
                completed = statistics.completed,
                progress = statistics.progress
            )

            // --- 2. BAGIAN PENCARIAN & FILTER ---
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari tugas...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Tabs (LazyRow)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = filterState is TodoFilter.All,
                            onClick = { viewModel.onFilterChange(TodoFilter.All) },
                            label = { Text("Semua") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = filterState is TodoFilter.Active,
                            onClick = { viewModel.onFilterChange(TodoFilter.Active) },
                            label = { Text("Belum Selesai") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        )
                    }
                    items(items = Category.entries.toTypedArray()) { category ->
                        FilterChip(
                            selected = (filterState as? TodoFilter.ByCategory)?.category == category,
                            onClick = { viewModel.onFilterChange(TodoFilter.ByCategory(category)) },
                            label = { Text(category.name) }
                        )
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // --- 3. INPUT TUGAS BARU ---
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = todoText,
                        onValueChange = { todoText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Apa yang ingin kamu lakukan?") },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (todoText.isNotEmpty()) {
                                        userData?.userId?.let { userId ->
                                            viewModel.add(userId, todoText, selectedPriority, selectedCategory)
                                            todoText = ""
                                            selectedPriority = Priority.MEDIUM
                                            selectedCategory = Category.LAINNYA
                                        }
                                    }
                                },
                                enabled = todoText.isNotEmpty()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = isPriorityExpanded,
                            onExpandedChange = { isPriorityExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedPriority.name,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Priority", fontSize = 11.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPriorityExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = isPriorityExpanded,
                                onDismissRequest = { isPriorityExpanded = false }
                            ) {
                                Priority.entries.forEach { priority ->
                                    DropdownMenuItem(
                                        text = { Text(priority.name) },
                                        onClick = { selectedPriority = priority; isPriorityExpanded = false },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = when (priority) {
                                                    Priority.LOW -> Icons.Default.ArrowDownward
                                                    Priority.MEDIUM -> Icons.Default.Star // Menggunakan Star agar aman
                                                    Priority.HIGH -> Icons.Default.ArrowUpward
                                                },
                                                contentDescription = null,
                                                tint = when (priority) {
                                                    Priority.LOW -> Color(0xFF4CAF50)
                                                    Priority.MEDIUM -> Color(0xFF2196F3)
                                                    Priority.HIGH -> Color(0xFFF44336)
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = isCategoryExpanded,
                            onExpandedChange = { isCategoryExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedCategory.name,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category", fontSize = 11.sp) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                            )
                            ExposedDropdownMenu(
                                expanded = isCategoryExpanded,
                                onDismissRequest = { isCategoryExpanded = false }
                            ) {
                                Category.entries.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.name) },
                                        onClick = { selectedCategory = category; isCategoryExpanded = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 4. LIST TUGAS (SWIPE TO DELETE) ---
            Spacer(modifier = Modifier.height(8.dp))

            if (todos.isNotEmpty()) {
                Text(
                    text = "Geser ke kiri untuk menghapus",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).align(Alignment.CenterHorizontally)
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if(searchQuery.isNotEmpty()) "Tidak ada hasil pencarian" else "Belum ada tugas",
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                items(items = todos, key = { it.id }) { todo ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                userData?.userId?.let { userId -> viewModel.delete(userId, todo.id) }
                                true
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color by animateColorAsState(
                                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                                    MaterialTheme.colorScheme.errorContainer
                                else Color.Transparent, label = "Bg"
                            )
                            val scale by animateFloatAsState(
                                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.2f else 0.8f, label = "Icon"
                            )

                            Box(
                                Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)).background(color).padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.scale(scale))
                            }
                        },
                        content = {
                            TodoCardItem(todo = todo, userData = userData, viewModel = viewModel, onNavigateToEdit = onNavigateToEdit)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardCard(total: Int, completed: Int, progress: Float) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Progress Harian",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$completed dari $total tugas selesai",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                val message = when {
                    progress == 1f -> "Luar Biasa! Semua selesai 🎉"
                    progress > 0.5f -> "Sedikit lagi, semangat! 🔥"
                    progress > 0f -> "Ayo mulai kerjakan! 🚀"
                    else -> "Belum ada progress 💤"
                }
                Text(
                    text = message,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(70.dp)
            ) {
                // Background Circle (Full)
                CircularProgressIndicator(
                    progress = 1f, // SUDAH DIPERBAIKI (Float, bukan Lambda)
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f),
                    strokeWidth = 8.dp,
                )
                // Progress Circle (Dinamis)
                CircularProgressIndicator(
                    progress = progress, // SUDAH DIPERBAIKI (Float, bukan Lambda)
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round,
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun TodoCardItem(
    todo: Todo,
    userData: UserData?,
    viewModel: TodoViewModel,
    onNavigateToEdit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val priority = try { Priority.valueOf(todo.priority) } catch (e: Exception) { Priority.MEDIUM }
    val category = try { Category.valueOf(todo.category) } catch (e: Exception) { Category.LAINNYA }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onNavigateToEdit(todo.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (priority) {
                Priority.LOW -> MaterialTheme.colorScheme.surfaceVariant
                Priority.MEDIUM -> MaterialTheme.colorScheme.secondaryContainer
                Priority.HIGH -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { isChecked ->
                    userData?.userId?.let { viewModel.toggle(it, todo.id, isChecked) }
                },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
            )

            Column(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Badge(text = priority.name, color = Color.DarkGray)
                    Badge(text = category.name, color = MaterialTheme.colorScheme.primary)
                }
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp),
                    textDecoration = if (todo.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None,
                    color = if (todo.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}