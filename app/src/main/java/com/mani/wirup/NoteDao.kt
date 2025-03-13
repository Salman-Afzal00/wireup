package com.mani.wirup

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteById(noteId: Long): LiveData<Note?>

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun delete(noteId: Long)

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>
}