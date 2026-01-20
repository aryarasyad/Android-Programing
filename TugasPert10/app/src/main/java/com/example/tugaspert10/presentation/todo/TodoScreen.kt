package com.example.tugaspert10.presentation.todo

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tugaspert10.data.model.Priority
import com.example.tugaspert10.data.model.Todo
import com.example.tugaspert10.data.model.UserData

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun TodoScreen(
    userData: UserData?,
    viewModel: TodoViewModel,
    onSignOut: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    var todoText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    val todos by viewModel.todos.collectAsState()

    LaunchedEffect(userData?.userId) {
        userData?.userId?.let { viewModel.observeTodos(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Todo List",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                actions = {
                    userData?.let {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = it.profilePictureUrl,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            IconButton(
                                onClick = onSignOut,
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Outlined.Logout, // Perbaikan: gunakan AutoMirrored
                                    contentDescription = "Sign Out",
                                    tint = MaterialTheme.colorScheme.error
                                )
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
            // CARD INPUT TUGAS BARU
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // INPUT SINGLE LINE
                    OutlinedTextField(
                        value = todoText,
                        onValueChange = { todoText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Apa yang ingin kamu lakukan?") },
                        singleLine = true,
                        maxLines = 1,
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (todoText.isNotEmpty()) {
                                        userData?.userId?.let { userId ->
                                            viewModel.add(userId, todoText, selectedPriority)
                                            todoText = ""
                                        }
                                    }
                                },
                                enabled = todoText.isNotEmpty()
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = if (todoText.isNotEmpty()) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // PRIORITY SELECTOR - LAYOUT YANG LEBIH BAIK
                    Text(
                        "Priority:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Gunakan Row dengan wrap content
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Priority.entries.forEach { priority ->
                            // Gunakan FilterChip bukan CompactChip
                            FilterChip(
                                selected = selectedPriority == priority,
                                onClick = { selectedPriority = priority },
                                label = {
                                    Text(
                                        priority.name,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (priority) {
                                            Priority.LOW -> Icons.Default.ArrowDownward
                                            Priority.MEDIUM -> Icons.Default.HorizontalRule
                                            Priority.HIGH -> Icons.Default.ArrowUpward
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = when (priority) {
                                        Priority.LOW -> Color(0xFF4CAF50)
                                        Priority.MEDIUM -> Color(0xFF2196F3)
                                        Priority.HIGH -> Color(0xFFF44336)
                                    },
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                ),
                                modifier = Modifier
                                    .height(40.dp)
                            )
                        }
                    }
                }
            }

            // ANIMASI UNTUK LIST TODO
            AnimatedVisibility(
                visible = todos.isNotEmpty(),
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Text(
                    "Total Tugas: ${todos.size}",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // LAZY COLUMN DENGAN ANIMASI
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(
                    items = todos,
                    key = { it.id }
                ) { todo ->
                    // ANIMASI MASUK UNTUK SETIAP ITEM
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        // TODO ITEM LANGSUNG DI SINI
                        TodoCardItem(
                            todo = todo,
                            userData = userData,
                            viewModel = viewModel,
                            onNavigateToEdit = onNavigateToEdit,
                            modifier = Modifier.animateContentSize()
                        )
                    }
                }
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
    // Konversi string priority ke enum dengan error handling
    val priority = try {
        Priority.valueOf(todo.priority)
    } catch (e: IllegalArgumentException) {
        Priority.MEDIUM
    }

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
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // CHECKBOX - PERBAIKAN: Gunakan fungsi toggle yang baru
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { isChecked ->
                    userData?.userId?.let { userId ->
                        // Panggil fungsi toggle dengan parameter yang benar
                        viewModel.toggle(userId, todo.id, isChecked)
                    }
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = when (priority) {
                        Priority.LOW -> Color(0xFF4CAF50)
                        Priority.MEDIUM -> MaterialTheme.colorScheme.primary
                        Priority.HIGH -> MaterialTheme.colorScheme.error
                    }
                ),
                modifier = Modifier.padding(end = 8.dp)
            )

            // TITLE AND PRIORITY
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (todo.isCompleted) FontWeight.Normal else FontWeight.Medium
                    ),
                    color = if (todo.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                // PRIORITY BADGE
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (priority) {
                                Priority.LOW -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                Priority.MEDIUM -> Color(0xFF2196F3).copy(alpha = 0.2f)
                                Priority.HIGH -> Color(0xFFF44336).copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = when (priority) {
                                Priority.LOW -> Icons.Default.ArrowDownward
                                Priority.MEDIUM -> Icons.Default.HorizontalRule
                                Priority.HIGH -> Icons.Default.ArrowUpward
                            },
                            contentDescription = null,
                            tint = when (priority) {
                                Priority.LOW -> Color(0xFF4CAF50)
                                Priority.MEDIUM -> Color(0xFF2196F3)
                                Priority.HIGH -> Color(0xFFF44336)
                            },
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = priority.name,
                            fontSize = 10.sp,
                            color = when (priority) {
                                Priority.LOW -> Color(0xFF4CAF50)
                                Priority.MEDIUM -> Color(0xFF2196F3)
                                Priority.HIGH -> Color(0xFFF44336)
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // DELETE BUTTON
            IconButton(
                onClick = {
                    userData?.userId?.let { userId ->
                        viewModel.delete(userId, todo.id)
                    }
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}