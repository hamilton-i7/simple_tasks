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

    var buttonName by mutableStateOf("")
        private set

    var deletingTask by mutableStateOf(false)
        private set

    private var taskToDelete: Task? = null
    private var deletedPosition = -1

    fun setTasks(tasks: List<Task>) {
        _tasks.value = tasks
    }

    fun onTaskCreate(todo: Todo, taskName: String, taskDetails: String) {
        val newTask = Task(
            name = taskName.trim(),
            details = taskDetails.trim()
        )
        val newList = _tasks.value!!.toMutableList().also {
            it.add(it.indexOfLast { task -> !task.completed } + 1, newTask)
        }
        _tasks.value = newList
        todoViewModel.onEditTodo(todo, newList)
    }

    fun onTaskDeleteUndo(todo: Todo) {
        if (taskToDelete != null && deletedPosition != -1) {
            val newList = _tasks.value!!.toMutableList().also {
                it.add(deletedPosition, taskToDelete!!)
            }
            _tasks.value = newList
            todoViewModel.onEditTodo(todo, newList)
            resetDeleteState()
        }
        deletingTask = false
    }

    fun onDeletingTask(toDelete: Boolean) {
        deletingTask = toDelete
    }

    fun onTaskStateChange(task: Task, todo: Todo) {
        val newList = _tasks.value!!.toMutableList().also { it.remove(task) }
        val updatedTask = task.copy(completed = !task.completed)
        if (!task.completed)
            newList.add(updatedTask)
        else
            newList.add(newList.indexOfLast { !it.completed } + 1, updatedTask)
        _tasks.value = newList
        todoViewModel.onEditTodo(todo, newList)
    }

    fun onTasksSwap(tasks: List<Task>, todo: Todo) {
        val newList = tasks + todo.tasks.filter { it.completed }
        _tasks.value = newList
        todoViewModel.onEditTodo(todo, newList)
    }

    fun onCompletedTasksDelete(todo: Todo) {
        val remainingTasks = _tasks.value!!.filter { !it.completed }
        _tasks.value = remainingTasks
        todoViewModel.onEditTodo(todo, remainingTasks)
    }

    fun onTaskDelete(task: Task, todo: Todo) {
        val newList = _tasks.value!!.toMutableList().also { it.remove(task) }
        deletedPosition = _tasks.value!!.indexOf(task)
        taskToDelete = task
        _tasks.value = newList
        todoViewModel.onEditTodo(todo, newList)
    }

    fun onTaskEdit(task: Task, todo: Todo, taskName: String, taskDetails: String?) {
        val newList = _tasks.value!!.toMutableList().also { tasks ->
            tasks[tasks.indexOfFirst { it.id == task.id }] = task.copy(
                name = taskName,
                details = taskDetails
            )
        }
        _tasks.value = newList
        todoViewModel.onEditTodo(todo, newList)
    }

    fun onTaskSwitch(task: Task, from: Todo, to: Todo) {
        val newList1 = from.tasks.toMutableList().also { it.remove(task) }
        val newList2 = to.tasks.toMutableList().also {
            it.add(
                index = it.indexOfLast { task -> !task.completed } + 1,
                element = task
            )
        }
        todoViewModel.onEditTodo(from, newList1)
        todoViewModel.onEditTodo(to, newList2)
    }

    fun onButtonNameChange(name: String) {
        buttonName = name
    }

    private fun resetDeleteState() {
        taskToDelete = null
        deletedPosition = -1
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