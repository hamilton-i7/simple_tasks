package com.example.simpletasks.data.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.data.todo.TodoViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TaskViewModel(
    todo: Todo,
    private val todoViewModel: TodoViewModel
) : ViewModel() {

    private var _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    init {
        _tasks.value = todo.tasks
    }

    fun onTaskStateChange(
        completed: Boolean,
        task: Task,
        todo: Todo,
    ) {
        val newList = _tasks.value!!.toMutableList().also { it.remove(task) }
        val updatedTask = Task(
            id = task.id,
            name = task.name,
            completed = completed
        )
        if (completed)
            newList.add(updatedTask)
        else
            newList.add(newList.indexOfLast { !it.completed } + 1, updatedTask)
        val updatedTodo = Todo(
            id = todo.id,
            name = todo.name,
            colorResource = todo.colorResource,
            tasks = newList
        )
        _tasks.value = newList
        todoViewModel.updateTodo(updatedTodo)
    }

    fun onTaskCreate(todo: Todo, taskName: String) {
        val newTask = Task(name = taskName)
        val newList = todo.tasks.toMutableList().also {
            it.add(it.indexOfLast { task -> !task.completed } + 1, newTask)
        }
        val updatedTodo = Todo(
            id = todo.id,
            name = todo.name,
            colorResource = todo.colorResource,
            tasks = newList
        )
        todoViewModel.updateTodo(updatedTodo)
    }

    fun onCompletedTasksDelete(todo: Todo) {
        val remainingTasks = _tasks.value!!.filter { !it.completed }
        val updatedTodo = Todo(todo.id!!, todo.name, todo.colorResource, remainingTasks)
        _tasks.value = remainingTasks
        todoViewModel.updateTodo(updatedTodo)
    }

    fun onTaskDelete(task: Task, todo: Todo) {
        val newList = todo.tasks.toMutableList().also { it.remove(task) }
        val updatedTodo = Todo(
            id = todo.id,
            name = todo.name,
            colorResource = todo.colorResource,
            tasks = newList
        )
        todoViewModel.updateTodo(updatedTodo)
    }

    fun onTaskEdit(task: Task, todo: Todo, name: String) {
        val newList = todo.tasks.toMutableList().also {
            it[it.indexOf(task)] = Task(
                id = task.id,
                name = name,
                completed = task.completed
            )
        }
        val updatedTodo = Todo(
            id = todo.id,
            name = todo.name,
            colorResource = todo.colorResource,
            tasks = newList
        )
        todoViewModel.updateTodo(updatedTodo)
    }
}

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class TaskViewModelFactory(
    private val todo: Todo,
    private val todoViewModel: TodoViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        TaskViewModel(todo, todoViewModel) as T
}