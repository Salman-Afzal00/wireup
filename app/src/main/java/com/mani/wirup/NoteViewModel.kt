package com.mani.wirup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {

    val allNotes: LiveData<List<Note>> = noteRepository.allNotes


    fun insert(note: Note) = viewModelScope.launch {
        noteRepository.insert(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        noteRepository.update(note)
    }

     fun getNoteById(noteId: Long): LiveData<Note?> {
        return noteRepository.getNoteById(noteId)
    }

    fun delete(noteId: Long) = viewModelScope.launch {
        noteRepository.delete(noteId)
    }
}