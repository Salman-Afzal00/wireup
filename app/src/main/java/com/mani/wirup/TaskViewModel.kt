package com.mani.wirup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val suggestedTasks: LiveData<List<Task>> = taskRepository.getSuggestedTasks()
    val pendingTasks: LiveData<List<Task>> = taskRepository.getPendingTasks()
    val completedTasks: LiveData<List<Task>> = taskRepository.getCompletedTasks()

    fun insertSuggestedTasks(tasks: List<Task>) {
        viewModelScope.launch {
            taskRepository.insertSuggestedTasks(tasks)
        }
    }

    fun getTasksForMeeting(): LiveData<List<Task>> {
        return taskRepository.getTasksForMeeting()
    }

    fun insert(task: Task) = viewModelScope.launch {
        taskRepository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        taskRepository.update(task)
    }

    fun delete(taskId: Long) = viewModelScope.launch {
        taskRepository.delete(taskId)
    }

    fun getTasksByDate(date: String): LiveData<List<Task>> {
        return taskRepository.getTasksByDate(date)
    }
    fun deleteAllCompletedTasks() {
        viewModelScope.launch {
            taskRepository.deleteAllCompletedTasks()
        }
    }
    fun getTaskById(taskId: Long): LiveData<Task?> {
        return liveData {
            val task = taskRepository.getTaskById(taskId)
            emit(task)
        }
    }
    // New function to get tasks by client ID
    fun getTasksByClientId(clientId: Int): LiveData<List<Task>> {
        return taskRepository.getTasksByClientId(clientId)
    }
    fun getTasksForCurrentDate(): LiveData<List<Task>> {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return taskRepository.getTasksByDate(currentDate).map { tasks ->
            tasks.filter { it.isPending && it.addToCalendar } // Only include pending tasks added to the calendar
        }
    }
}