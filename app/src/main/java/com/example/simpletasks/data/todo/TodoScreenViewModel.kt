package com.example.simpletasks.data.todo

import androidx.annotation.ColorRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TodoScreenViewModel(
    private val todo: Todo,
    private val todoViewModel: TodoViewModel
) : ViewModel() {

    var labelColor by mutableStateOf(todo.colorResource)
        private set

    fun onLabelChange(@ColorRes newColor: Int) {
        updateLabelColor(newColor)
        val updatedTodo = Todo(
            id = todo.id,
            name = todo.name,
            colorResource = labelColor,
            tasks = todo.tasks
        )
        todoViewModel.updateTodo(updatedTodo)
    }

    private fun updateLabelColor(@ColorRes newColor: Int) {
        labelColor = newColor
    }
}

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class TodoScreenViewModelFactory(
    private val todo: Todo,
    private val todoViewModel: TodoViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        TodoScreenViewModel(todo, todoViewModel) as T
}