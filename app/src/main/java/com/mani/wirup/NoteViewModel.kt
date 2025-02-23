package com.mani.wirup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    val note: LiveData<Note?> = noteRepository.note

    fun insert(note: Note) = viewModelScope.launch {
        noteRepository.insert(note)
    }

    fun deleteAll() = viewModelScope.launch {
        noteRepository.deleteAll()
    }
}