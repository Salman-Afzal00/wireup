package com.mani.wirup

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Update if the note already exists
    suspend fun insert(note: Note)

    @Query("SELECT * FROM notes WHERE id = 1") // Always fetch the note with ID 1
    fun getNote(): LiveData<Note?>

    @Query("DELETE FROM notes")
    suspend fun deleteAll()
}