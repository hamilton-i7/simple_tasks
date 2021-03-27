package com.example.simpletasks.data.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.data.todo.TodoViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TaskViewModel(private val todoViewModel: TodoViewModel) : ViewModel() {

    private var _tasks = MutableLiveData<List<Task>>(emptyList())
    val tasks: LiveData<List<Task>> get() = _tasks

    var taskName by mutableStateOf("")
        private set

    private val _newTaskName = MutableLiveData("")
    val newTaskName: LiveData<String> get() = _newTaskName

    fun setTasks(tasks: List<Task>) {
        _tasks.value = tasks
    }

    fun onTaskCreate(todo: Todo) {
        val newTask = Task(name = _newTaskName.value!!)
        val newList = _tasks.value!!.toMutableList().also {
            it.add(it.indexOfLast { task -> !task.completed } + 1, newTask)
        }
        todoViewModel.onEvent(todo.copy(tasks = newList))
        _tasks.value = newList
    }

    fun onTaskNameChange(name: String) {
        taskName = name
    }

    fun onNewTaskNameChange(name: String) {
        _newTaskName.value = name
    }

    fun onTaskStateChange(task: Task, todo: Todo) {
        val newList = _tasks.value!!.toMutableList().also { it.remove(task) }
        val updatedTask = Task(
            id = task.id,
            name = task.name,
            completed = !task.completed
        )
        if (!task.completed)
            newList.add(updatedTask)
        else
            newList.add(newList.indexOfLast { !it.completed } + 1, updatedTask)
        todoViewModel.onEvent(todo.copy(tasks = newList))
        _tasks.value = newList
    }

    fun onTasksSwap(tasks: List<Task>, todo: Todo) {
        val newList = tasks + todo.tasks.filter { it.completed }
        todoViewModel.onEvent(todo.copy(tasks = newList))
        _tasks.value = newList
    }

    fun onCompletedTasksDelete(todo: Todo) {
        val remainingTasks = _tasks.value!!.filter { !it.completed }
        todoViewModel.onEvent(todo.copy(tasks = remainingTasks))
        _tasks.value = remainingTasks
    }

    fun onTaskDelete(task: Task, todo: Todo) {
        val newList = _tasks.value!!.toMutableList().also { it.remove(task) }
        todoViewModel.onEvent(todo.copy(tasks = newList))
        _tasks.value = newList
    }

    fun onTaskEdit(task: Task, todo: Todo) {
        val newList = _tasks.value!!.toMutableList().also {
            it[it.indexOf(task)] = Task(
                id = task.id,
                name = taskName,
                completed = task.completed
            )
        }
        todoViewModel.onEvent(todo.copy(tasks = newList))
        _tasks.value = newList
    }
}

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class TaskViewModelFactory(
    private val todoViewModel: TodoViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        TaskViewModel(todoViewModel) as T
}