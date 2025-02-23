package com.mani.wirup

import androidx.lifecycle.LiveData
import androidx.room.*

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

    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClientById(clientId: Int): Client?

    // New query to get client by name
    @Query("SELECT * FROM clients WHERE name = :name")
    suspend fun getClientByName(name: String): Client?
}