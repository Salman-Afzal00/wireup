package com.mani.wirup

import androidx.lifecycle.LiveData

class ClientRepository(private val clientDao: ClientDao) {

    val allClients: LiveData<List<Client>> = clientDao.getAllClients()

    suspend fun insert(client: Client) {
        clientDao.insert(client)
    }

    suspend fun update(client: Client) {
        clientDao.update(client)
    }

    suspend fun delete(clientId: Int) {
        clientDao.deleteClient(clientId)
    }
}