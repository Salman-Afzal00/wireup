package com.mani.wirup

import android.app.Application

class MyApplication : Application() {
    // Provide the TaskRepository
    val repository: TaskRepository by lazy {
        val database = AppDatabase.getDatabase(this)
        TaskRepository(database.taskDao())
    }
}