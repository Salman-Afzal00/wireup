package com.mani.wirup

import android.app.Application

class MyApplication : Application() {

    val taskRepository: TaskRepository by lazy {
        val database = AppDatabase.getDatabase(this)
        TaskRepository(database.taskDao())
    }

    val noteRepository: NoteRepository by lazy {
        val database = AppDatabase.getDatabase(this)
        NoteRepository(database.noteDao())
    }
}