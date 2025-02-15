package com.mani.wirup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ClientViewModel(private val repository: ClientRepository) : ViewModel() {

    val allClients: LiveData<List<Client>> = repository.allClients

    fun insert(client: Client) = viewModelScope.launch {
        repository.insert(client)
    }

    fun update(client: Client) = viewModelScope.launch {
        repository.update(client)
    }

    fun delete(clientId: Int) = viewModelScope.launch {
        repository.delete(clientId)
    }
}