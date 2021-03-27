package com.example.simpletasks.data.todo

import android.app.Application
import androidx.annotation.ColorRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.example.simpletasks.R
import com.example.simpletasks.data.SimpleTasksDatabase
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

    private var updatedTodo: Todo? = null

    var newTodoColor by mutableStateOf(R.color.default_color)
        private set

    private var _todoName = MutableLiveData("")
    val todoName: LiveData<String> get() = _todoName

    var labelColor by mutableStateOf(R.color.default_color)
        private set

    var isInvalidName by mutableStateOf(false)
        private set

    var isDialogVisible by mutableStateOf(false)
        private set
    var isLabelDialogVisible by mutableStateOf(false)
        private set

    init {
        val todoDao = SimpleTasksDatabase.getDatabase(application, applicationScope).todoDao()
        repo = TodoRepo(todoDao)
        val todosFlow = _searchQuery.flatMapLatest { repo.readTodosByQuery(it) }
        todos = todosFlow.asLiveData()
    }

    fun readAllTodos(): Flow<List<Todo>> = repo.readAllTodos()

    fun readTodoById(id: String): LiveData<Todo> = repo.readTodoById(id).asLiveData()

    fun deleteTodo(todo: Todo) = viewModelScope.launch {
        repo.deleteTodo(todo)
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onDialogStatusChange(showDialog: Boolean) {
        isDialogVisible = showDialog
        if (!showDialog) resetValidationState()
    }

    fun setInitialLabel(@ColorRes color: Int) {
        labelColor = color
    }

    fun onLabelDialogStatusChange(showDialog: Boolean) {
        isLabelDialogVisible = showDialog
    }

    fun onLabelChange(todo: Todo, @ColorRes newColor: Int) {
        labelColor = newColor
        updatedTodo = todo.copy(colorResource = newColor)
    }

    fun onNewColorChange(@ColorRes color: Int) {
        newTodoColor = color
    }

    fun onNameChange(name: String) {
        _todoName.value = name
    }

    fun onCancelDialog() {
        isDialogVisible = false
        newTodoColor = R.color.default_color
    }

    fun onEditDone(todo: Todo) {
        updatedTodo = todo.copy(name = _todoName.value!!)
        resetValidationState()
    }

    fun onCreateDone(name: String) {
        newTodo = createTodo(name)
        onValidTodo(newTodo!!)
    }

    fun onEvent(todo: Todo) {
        updatedTodo = todo
    }

    fun onStop() {
        updatedTodo?.let { updateTodo(it) }
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

    private fun onValidTodo(todo: Todo) {
        isDialogVisible = false
        onNameChange(todo.name)
        resetValidationState()
    }

    private fun resetValidationState() {
        isInvalidName = false
    }
}