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
    var taskDetails by mutableStateOf("")
        private set

    private val _newTaskName = MutableLiveData("")
    val newTaskName: LiveData<String> get() = _newTaskName

    var newTaskDetails by mutableStateOf("")
        private set

    var buttonName by mutableStateOf("")
        private set

    fun setTasks(tasks: List<Task>) {
        _tasks.value = tasks
    }

    fun onTaskCreate(todo: Todo, taskName: String, taskDetails: String) {
        val newTask = Task(
            name = taskName,
            details = taskDetails
        )
        val newList = _tasks.value!!.toMutableList().also {
            it.add(it.indexOfLast { task -> !task.completed } + 1, newTask)
        }
//        todoViewModel.onEvent(todo.copy(tasks = newList))
        _tasks.value = newList
//        newTaskDetails = ""
        resetNewTaskField()
        todoViewModel.updateTodo(todo.copy(tasks = newList))
    }

    fun onTaskNameChange(name: String) {
        taskName = name
    }

    fun onTaskDetailsChange(details: String) {
        taskDetails = details
    }

    fun onNewTaskNameChange(name: String) {
        _newTaskName.value = name
    }

    fun onNewTaskDetailsChange(details: String) {
        newTaskDetails = details
    }

    fun onTaskStateChange(task: Task, todo: Todo) {
        val newList = _tasks.value!!.toMutableList().also { it.remove(task) }
        val updatedTask = task.copy(completed = !task.completed)
        if (!task.completed)
            newList.add(updatedTask)
        else
            newList.add(newList.indexOfLast { !it.completed } + 1, updatedTask)
//        todoViewModel.onEvent(todo.copy(tasks = newList))
        _tasks.value = newList
        todoViewModel.updateTodo(todo.copy(tasks = newList))
    }

    fun onTasksSwap(tasks: List<Task>, todo: Todo) {
        val newList = tasks + todo.tasks.filter { it.completed }
//        todoViewModel.onEvent(todo.copy(tasks = newList))
        _tasks.value = newList
        todoViewModel.updateTodo(todo.copy(tasks = newList))
    }

    fun onCompletedTasksDelete(todo: Todo) {
        val remainingTasks = _tasks.value!!.filter { !it.completed }
//        todoViewModel.onEvent(todo.copy(tasks = remainingTasks))
        _tasks.value = remainingTasks
        todoViewModel.updateTodo(todo.copy(tasks = remainingTasks))
    }

    fun onTaskDelete(task: Task, todo: Todo) {
        val newList = _tasks.value!!.toMutableList().also { it.remove(task) }
//        todoViewModel.onEvent(todo.copy(tasks = newList))
        _tasks.value = newList
        todoViewModel.updateTodo(todo.copy(tasks = newList))
    }

    fun onTaskEdit(task: Task, todo: Todo, taskName: String, taskDetails: String?) {
        val newList = _tasks.value!!.toMutableList().also { tasks ->
            tasks[tasks.indexOfFirst { it.id == task.id }] = task.copy(
                name = taskName,
                details = taskDetails
            )
        }
//            todoViewModel.onEvent(todo.copy(tasks = newList))
        _tasks.value = newList
        todoViewModel.updateTodo(todo.copy(tasks = newList))
    }

    fun onTaskSwitch(task: Task, from: Todo, to: Todo) {
        val newList1 = from.tasks.toMutableList().also { it.remove(task) }
        val newList2 = to.tasks.toMutableList().also {
            it.add(
                index = it.indexOfLast { task -> !task.completed } + 1,
                element = task
            )
        }
        setOf(
            from.copy(tasks = newList1),
            to.copy(tasks = newList2)
        ).forEach {
            todoViewModel.updateTodo(it)
        }

//        todoViewModel.onEvent(setOf(
//            from.copy(tasks = newList1),
//            to.copy(tasks = newList2)
//        ))
    }

    private fun resetNewTaskField() {
        _newTaskName.value = ""
    }

    fun onButtonNameChange(name: String) {
        buttonName = name
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