package com.mani.wirup

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {

    val note: LiveData<Note?> = noteDao.getNote()

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun deleteAll() {
        noteDao.deleteAll()
    }
}