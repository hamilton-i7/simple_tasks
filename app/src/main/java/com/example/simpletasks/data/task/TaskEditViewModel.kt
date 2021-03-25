package com.example.simpletasks.data.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TaskEditViewModel(task: Task, taskViewModel: TaskViewModel) : ViewModel() {

    var taskName by mutableStateOf(task.name)
        private set

    fun onTaskNameChange(name: String) {
        taskName = name
    }
}

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class TaskEditViewModelFactory(
    private val task: Task,
    private val taskViewModel: TaskViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        TaskEditViewModel(task, taskViewModel) as T
}