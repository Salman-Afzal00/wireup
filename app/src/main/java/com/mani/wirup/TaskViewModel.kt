package com.mani.wirup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
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
        viewModelScope.launch(Dispatchers.IO) { // Perform database operation on a background thread
            taskRepository.insertSuggestedTasks(tasks)
        }
    }

    fun saveTask(task: Task) = viewModelScope.launch {
        taskRepository.saveTask(task)
        refreshPendingTasks() // Refresh pending tasks after saving a new task
    }

    fun getTasksForMeeting(): LiveData<List<Task>> {
        return taskRepository.getTasksForMeeting()
    }

    fun insert(task: Task) = viewModelScope.launch {
        taskRepository.insert(task)
        refreshPendingTasks() // Refresh pending tasks after inserting a new task
    }

    fun update(task: Task) = viewModelScope.launch {
        taskRepository.update(task)
        refreshPendingTasks() // Refresh pending tasks after updating a task
    }

    fun delete(taskId: Long) = viewModelScope.launch {
        taskRepository.delete(taskId)
        refreshPendingTasks() // Refresh pending tasks after deleting a task
    }

    fun getTasksByDate(date: String): LiveData<List<Task>> {
        return taskRepository.getTasksByDate(date)
    }

    fun deleteAllCompletedTasks() {
        viewModelScope.launch {
            taskRepository.deleteAllCompletedTasks()
            refreshPendingTasks() // Refresh pending tasks after deleting all completed tasks
        }
    }

    fun getTaskById(taskId: Long): LiveData<Task?> {
        return liveData {
            val task = taskRepository.getTaskById(taskId)
            emit(task)
        }
    }

    fun getTasksByClientId(clientId: Int): LiveData<List<Task>> {
        return taskRepository.getTasksByClientId(clientId)
    }

    fun getTasksForCurrentDate(): LiveData<List<Task>> {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return taskRepository.getTasksByDate(currentDate).map { tasks ->
            tasks.filter { it.isPending && it.addToCalendar }
        }
    }

    // Refresh pending tasks
    private fun refreshPendingTasks() {
        viewModelScope.launch {
            taskRepository.refreshPendingTasks() // Ensure this method exists in your repository
        }
    }

    // Load pending tasks explicitly
    fun loadPendingTasks() {
        viewModelScope.launch {
            taskRepository.refreshPendingTasks() // Refresh the pending tasks list
        }
    }
}