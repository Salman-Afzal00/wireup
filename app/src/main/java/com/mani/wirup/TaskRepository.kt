package com.mani.wirup

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    fun getSuggestedTasks(): LiveData<List<Task>> = taskDao.getSuggestedTasks()
    fun getPendingTasks(): LiveData<List<Task>> = taskDao.getPendingTasks()
    fun getCompletedTasks(): LiveData<List<Task>> = taskDao.getCompletedTasks()

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }
    fun insertSuggestedTasks(tasks: List<Task>) {
        taskDao.insertAll(tasks)
    }
    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    suspend fun delete(taskId: Long) {
        taskDao.delete(taskId)
    }

    fun getTasksForMeeting(): LiveData<List<Task>> {
        return taskDao.getTasksForMeeting()
    }

    fun getTasksByDate(date: String): LiveData<List<Task>> {
        return taskDao.getTasksByDate(date)
    }

    suspend fun deleteAllCompletedTasks() {
        taskDao.deleteAllCompletedTasks()
    }
    suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)
    }

    // New function to get tasks by client ID
    fun getTasksByClientId(clientId: Int): LiveData<List<Task>> {
        return taskDao.getTasksByClientId(clientId)
    }
}