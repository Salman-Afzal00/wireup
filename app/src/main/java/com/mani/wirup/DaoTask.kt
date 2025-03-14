package com.mani.wirup

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)

    @Insert
    suspend fun insertAll(tasks: List<Task>)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM tasks WHERE isSuggested = 1 ORDER BY date ASC") // Fetch suggested tasks
    fun getSuggestedTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isPending = 1 ORDER BY date ASC")
    fun getPendingTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY date ASC")
    fun getCompletedTasks(): LiveData<List<Task>>

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun delete(taskId: Long)

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY id DESC")
    fun getTasksByDate(date: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE clientId = :clientId")
    fun getTasksByClientId(clientId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isPending = 1 AND isCompleted = 0 ORDER BY date ASC")
    fun getTasksForMeeting(): LiveData<List<Task>>

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteAllCompletedTasks()

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task?
}