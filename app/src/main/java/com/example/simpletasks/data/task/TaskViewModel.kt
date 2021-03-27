package com.example.simpletasks.data.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.todo.TodoFragmentDirections
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
        val updatedTodo = Todo(
            id = todo.id,
            name = todo.name,
            colorResource = todo.colorResource,
            tasks = newList
        )
        _tasks.value = newList
        todoViewModel.updateTodo(updatedTodo)
    }

    fun onTasksSwap(tasks: List<Task>, todo: Todo) {
        val updatedTodo = Todo(
            id = todo.id,
            name = todo.name,
            colorResource = todo.colorResource,
            tasks = tasks + todo.tasks.filter { it.completed }
        )
        todoViewModel.updateTodo(updatedTodo)
    }

    fun onTaskClick(task: Task, todo: Todo, navController: NavController) {
        val action = TodoFragmentDirections.actionTodoFragmentToTaskEditFragment(todo, task)
        navController.navigate(action)
    }

    fun onCompletedTasksDelete(todo: Todo) {
        val remainingTasks = todo.tasks.filter { !it.completed }
        val updatedTodo = Todo(todo.id, todo.name, todo.colorResource, remainingTasks)
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
    private val todoViewModel: TodoViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        TaskViewModel(todoViewModel) as T
}