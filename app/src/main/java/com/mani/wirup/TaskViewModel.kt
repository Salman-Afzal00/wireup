package com.mani.wirup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskrepository: TaskRepository
    ) : ViewModel() {

    val allTasks: LiveData<List<Task>> = taskrepository.allTasks

    fun getTasksByDate(date: String): LiveData<List<Task>> {
        return taskrepository.getTasksByDate(date)
    }

    fun insert(task: Task) = viewModelScope.launch {
        taskrepository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        taskrepository.update(task)
    }

    fun delete(taskId: Long) = viewModelScope.launch {
        taskrepository.delete(taskId)
    }


}