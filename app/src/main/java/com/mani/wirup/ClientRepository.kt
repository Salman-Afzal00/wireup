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

    suspend fun getClientById(clientId: Int): Client? {
        return clientDao.getClientById(clientId)
    }

    // New function to get client by name
    suspend fun getClientByName(name: String): Client? {
        return clientDao.getClientByName(name)
    }
}