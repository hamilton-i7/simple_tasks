package com.example.simpletasks.data.todo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.simpletasks.data.SimpleTasksDatabase
import com.example.simpletasks.data.task.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val applicationScope = CoroutineScope(SupervisorJob())
    private val repo: TodoRepo

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery
    val todos: LiveData<List<Todo>>

    init {
        val todoDao = SimpleTasksDatabase.getDatabase(application, applicationScope).todoDao()
        repo = TodoRepo(todoDao)
        val todosFLow = _searchQuery.flatMapLatest { repo.readTodosByQuery(it) }
        todos = todosFLow.asLiveData()
    }

    fun readTodosByQuery(searchQuery: String): LiveData<List<Todo>> =
        repo.readTodosByQuery(searchQuery).asLiveData()

    fun updateTodo(todo: Todo) = viewModelScope.launch {
        repo.updateTodo(todo)
    }

    fun deleteTodo(todo: Todo) = viewModelScope.launch {
        repo.deleteTodo(todo)
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTasksSwap(todo: Todo, tasks: List<Task>) {
        val updatedTodo = Todo(
            id = todo.id,
            name = todo.name,
            colorResource = todo.colorResource,
            tasks = tasks + todo.tasks.filter { it.completed }
        )
        updateTodo(updatedTodo)
    }
}