package com.mani.wirup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // LiveData to hold the suggested tasks
    private val _suggestedTasks = MutableLiveData<List<Task>>()
    val suggestedTasks: MutableLiveData<List<Task>> get() = _suggestedTasks

    // Function to update the suggested tasks
    fun addSuggestedTasks(tasks: List<Task>) {
        _suggestedTasks.value = tasks
    }
}