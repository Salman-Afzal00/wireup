package com.mani.wirup

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND isPending = 0 ORDER BY date ASC, time ASC")
    fun getSuggestedTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isPending = 1 ORDER BY date ASC, time ASC")
    fun getPendingTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY date ASC, time ASC")
    fun getCompletedTasks(): LiveData<List<Task>>

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun delete(taskId: Long)

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY id DESC")
    fun getTasksByDate(date: String): LiveData<List<Task>>

    // New query to get tasks by client ID
    @Query("SELECT * FROM tasks WHERE clientId = :clientId")
    fun getTasksByClientId(clientId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isPending = 1 AND isCompleted = 0 ORDER BY date ASC, time ASC")
    fun getTasksForMeeting(): LiveData<List<Task>>
}