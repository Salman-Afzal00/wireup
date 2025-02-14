package com.mani.wirup
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM tasks ORDER BY date ASC, time ASC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun delete(taskId: Long)

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY time ASC")
    fun getTasksByDate(date: String): LiveData<List<Task>>

}