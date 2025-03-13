package com.mani.wirup

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ClientViewModel(private val clientRepository: ClientRepository) : ViewModel() {

    val allClients: LiveData<List<Client>> = clientRepository.allClients

    fun insert(client: Client) {
        viewModelScope.launch {
            val existingClient = clientRepository.getClientById(client.id)
            if (existingClient == null) {
                clientRepository.insert(client)
            } else {
                  null
            }
        }
    }
    fun update(client: Client) = viewModelScope.launch {
        clientRepository.update(client)
    }

    fun delete(clientId: Int) = viewModelScope.launch {
        clientRepository.delete(clientId)
    }
    suspend fun getClientById(clientId: Int): Client? {
        return clientRepository.getClientById(clientId)
    }

    // New function to get client by name
    suspend fun getClientByName(name: String): Client? {
        return clientRepository.getClientByName(name)
    }
}