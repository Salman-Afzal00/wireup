package com.mani.wirup

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ClientDao {
    @Insert
    suspend fun insert(client: Client)

    @Update
    suspend fun update(client: Client)

    @Query("SELECT * FROM clients")
    fun getAllClients(): LiveData<List<Client>>

    @Query("DELETE FROM clients WHERE id = :clientId")
    suspend fun deleteClient(clientId: Int)
}