package com.example.simpletasks.data.todo

import android.app.Application
import androidx.annotation.ColorRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.simpletasks.R
import com.example.simpletasks.data.SimpleTasksDatabase
import com.example.simpletasks.data.task.Task
import com.example.simpletasks.ui.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
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

    var newTodo: Todo? = null
        private set

    var newTodoColor by mutableStateOf(R.color.default_color)
        private set

    var colorResource by mutableStateOf(R.color.default_color)
        private set

    var isDialogVisible by mutableStateOf(false)
        private set

    var selectedRoute by mutableStateOf(Screen.Home.route)
        private set

    var deletingTodo by mutableStateOf(false)
        private set

    private var todoToDelete: Todo? = null
    private var deletedPosition = -1

    init {
        val todoDao = SimpleTasksDatabase.getDatabase(application, applicationScope).todoDao()
        repo = TodoRepo(todoDao)
        val todosFlow = _searchQuery.flatMapLatest { repo.readTodosByQuery(it.trim()) }
        todos = todosFlow.asLiveData()
    }

    fun readAllTodos(): Flow<List<Todo>> = repo.readAllTodos()

    fun readTodoById(id: String): LiveData<Todo> = repo.readTodoById(id).asLiveData()

    fun deleteTodo(todo: Todo) {
        todoToDelete = todo
        viewModelScope.launch {
            repo.deleteTodo(todo)
        }
    }

    fun onDeletingTodo(toDelete: Boolean) {
        deletingTodo = toDelete
    }

    fun onTodoDeleteUndo() {
        if (todoToDelete != null) {
            addTodo(todoToDelete!!)
            resetDeleteState()
        }
        deletingTodo = false
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onDialogStatusChange(showDialog: Boolean) {
        isDialogVisible = showDialog
    }

    fun onEditTodo(todo: Todo, name: String) {
        updateTodo(todo.copy(name = name))
    }

    fun onEditTodo(todo: Todo, newTasks: List<Task>) {
        updateTodo(todo.copy(tasks = newTasks))
    }

    fun onLabelChange(todo: Todo, @ColorRes newColor: Int) {
        colorResource = newColor
        updateTodo(todo.copy(colorResource = newColor))
    }

    fun onNewColorChange(@ColorRes color: Int) {
        newTodoColor = color
    }

    fun onCancelDialog() {
        isDialogVisible = false
        newTodoColor = R.color.default_color
    }

    fun onCreateDone(name: String) {
        newTodo = createTodo(name)
        isDialogVisible = false
    }

    fun onTodoSelect(route: String) {
        selectedRoute = route
    }

    private fun createTodo(name: String): Todo {
        val newTodo = Todo(
            name = name,
            colorResource = newTodoColor,
        )
        addTodo(newTodo)
        return newTodo
    }
    private fun addTodo(todo: Todo) = viewModelScope.launch {
        repo.addTodo(todo)
    }

    private fun updateTodo(todo: Todo) = viewModelScope.launch {
        repo.updateTodo(todo)
    }

    private fun resetDeleteState() {
        todoToDelete = null
        deletedPosition = -1
    }
}