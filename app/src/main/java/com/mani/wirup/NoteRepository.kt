package com.mani.wirup

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

     fun getNoteById(noteId: Long): LiveData<Note?> {
        return noteDao.getNoteById(noteId)
    }

    suspend fun delete(noteId: Long) {
        noteDao.delete(noteId)
    }
}