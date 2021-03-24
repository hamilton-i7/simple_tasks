package com.example.simpletasks.data.todo

import android.app.Application
import androidx.annotation.ColorRes
import androidx.compose.runtime.*
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.simpletasks.R
import com.example.simpletasks.data.SimpleTasksDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val applicationScope = CoroutineScope(SupervisorJob())
    private val repo: TodoRepo

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> get() = _searchQuery
    val todos: LiveData<List<Todo>>

    var newTodoName by mutableStateOf("")
        private set
    var newTodoColor by mutableStateOf(R.color.default_color)
        private set

    var todoName by mutableStateOf("")
        private set

    var isInvalidName by mutableStateOf(false)
        private set

    var isRepeatedName by mutableStateOf(false)

    var isDialogVisible by mutableStateOf(false)
        private set

    init {
        val todoDao = SimpleTasksDatabase.getDatabase(application, applicationScope).todoDao()
        repo = TodoRepo(todoDao)
        todos = repo.readAllTodos().asLiveData()
    }

    @Composable
    fun readAllTodos(): State<List<Todo>> =
        repo.readAllTodos().collectAsState(initial = listOf())

    @Composable
    fun readTodosByQuery(searchQuery: String): State<List<Todo>> =
        repo.readTodosByQuery(searchQuery).collectAsState(initial = listOf())

    fun updateTodo(todo: Todo) = viewModelScope.launch {
        repo.updateTodo(todo)
    }

    fun deleteTodo(todo: Todo) = viewModelScope.launch {
        repo.deleteTodo(todo)
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun clearNameField() {
        newTodoName = ""
    }

    fun onDialogStatusChange(showDialog: Boolean) {
        isDialogVisible = showDialog
        if (!showDialog) resetValidationState()
    }

    fun onNewNameChange(name: String) {
        newTodoName = name
    }

    fun onNewColorChange(@ColorRes color: Int) {
        newTodoColor = color
    }

    fun onNameChange(name: String) {
        todoName = name
    }

    fun onLabelChange(todo: Todo, @ColorRes newColor: Int) {
        val updatedTodo = Todo(
            id = todo.id,
            name = todo.name,
            colorResource = newColor,
            tasks = todo.tasks
        )
        updateTodo(updatedTodo)
    }

    fun onCancelDialog() {
        clearNameField()
        isDialogVisible = false
    }

    fun onCancel() {
        resetValidationState()
    }

    fun onDone(todo: Todo, todos: List<Todo>) {
        if (todoName == todo.name || (isValidName(todoName) && !isNameRepeated(todoName, todos))) {
            val updatedTodo = Todo(
                id = todo.id,
                name = todoName,
                colorResource = todo.colorResource,
                tasks = todo.tasks
            )
            updateTodo(updatedTodo)
            resetValidationState()
        } else if (!isValidName(todoName)) {
            onInvalidName()
        } else if (isNameRepeated(todoName, todos)) {
            onNameRepeated()
        }
    }

    fun onDone(todos: List<Todo>) {
        if (
            isValidName(newTodoName) &&
            !isNameRepeated(newTodoName, todos)
        ) {
            val newTodo = createTodo()
            onValidName(newTodo)
        } else if (!isValidName(newTodoName)) {
            onInvalidName()
        } else if (isNameRepeated(newTodoName, todos)) {
            onNameRepeated()
        }
    }

    private fun createTodo(): Todo {
        val newTodo = Todo(
            name = newTodoName.trim(),
            colorResource = newTodoColor,
        )
        addTodo(newTodo)
        return newTodo
    }

    private fun addTodo(todo: Todo) = viewModelScope.launch {
        repo.addTodo(todo)
    }

    private fun onValidName(todo: Todo) {
        isDialogVisible = false
        clearNameField()
        onNameChange(todo.name)
        resetValidationState()
    }

    private fun onInvalidName() {
        isInvalidName = true
        isRepeatedName = false
    }

    private fun onNameRepeated() {
        isRepeatedName = true
        isInvalidName = false
    }

    private fun isValidName(name: String): Boolean = name.trim().isNotEmpty()

    private fun isNameRepeated(name: String, todos: List<Todo>): Boolean {
        val repeatedTodo = todos.find {
            it.name.toLowerCase(Locale.current) == name.toLowerCase(Locale.current)
        }
        return repeatedTodo != null
    }

    private fun resetValidationState() {
        isInvalidName = false
        isRepeatedName = false
    }
}