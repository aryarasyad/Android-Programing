package com.example.projectuas.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectuas.data.model.Journal
import com.example.projectuas.data.repository.JournalRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JournalViewModel : ViewModel() {
    private val repo = JournalRepository()
    private val _journals = MutableStateFlow<List<Journal>>(emptyList())
    val journals: StateFlow<List<Journal>> = _journals

    private var journalJob: Job? = null
    private var currentUserId: String? = null

    fun observeJournals(userId: String) {
        // Jika userId sama dan job masih aktif, jangan buat koneksi baru
        if (currentUserId == userId && journalJob?.isActive == true) return

        journalJob?.cancel()
        currentUserId = userId

        journalJob = viewModelScope.launch {
            repo.getJournals(userId).collect {
                _journals.value = it
            }
        }
    }

    fun clearDataOnSignOut() {
        journalJob?.cancel()
        journalJob = null
        currentUserId = null
        _journals.value = emptyList() // Reset state agar tidak terlihat user lain
    }

    fun addJournal(userId: String, title: String, content: String, mood: String) {
        viewModelScope.launch {
            repo.addJournal(Journal(userId = userId, title = title, content = content, mood = mood, createdAt = com.google.firebase.Timestamp.now()))
        }
    }

    fun deleteJournal(journalId: String) {
        viewModelScope.launch { repo.deleteJournal(journalId) }
    }

    fun updateJournal(journalId: String, title: String, content: String, mood: String) {
        viewModelScope.launch { repo.updateJournal(journalId, title, content, mood) }
    }
}