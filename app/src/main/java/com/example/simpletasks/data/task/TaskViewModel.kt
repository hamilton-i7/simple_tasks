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

    var todoWithTask: Todo? = null
        private set

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

    fun onTaskStateChange(task: Task) {
        todoWithTask?.let { todo ->
            val newList = todo.tasks.toMutableList().also { it.remove(task) }
            onTaskStateChanged(todo, task, newList)
        }
    }

    fun onTaskStateChange(task: Task, todo: Todo) {
        val newList = _tasks.value!!.toMutableList().also { it.remove(task) }
        onTaskStateChanged(todo, task, newList)
    }

    fun onTasksSwap(tasks: List<Task>, todo: Todo) {
        val newList = tasks + todo.tasks.filter { it.completed }
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

    fun onTaskSwitch(task: Task, to: Todo) {
        if (todoWithTask != null && todoWithTask != to) {
            val newList1 = todoWithTask!!.tasks.toMutableList().also { it.remove(task) }
            val newList2 = to.tasks.toMutableList().also {
                it.add(
                    index = it.indexOfLast { task -> !task.completed } + 1,
                    element = task
                )
            }

            todoViewModel.onEditTodo(todoWithTask!!, newList1)
            todoViewModel.onEditTodo(to, newList2)
        }
    }

    fun onTodoWithTaskChange(todo: Todo) {
        todoWithTask = todo
    }

    fun onCompletedTasksDelete(todo: Todo) {
        val remainingTasks = _tasks.value!!.filter { !it.completed }
        _tasks.value = remainingTasks
        todoViewModel.onEditTodo(todo, remainingTasks)
    }

    fun onDeletingTask(toDelete: Boolean) {
        deletingTask = toDelete
    }

    fun onTaskDelete(task: Task) {
        todoWithTask?.let { todo ->
            val newList = todo.tasks.toMutableList().also { it.remove(task) }
            deletedPosition = todo.tasks.indexOf(task)
            taskToDelete = task
            _tasks.value = newList
            todoViewModel.onEditTodo(todo, newList)
        }
        todoWithTask = todoWithTask?.copy(tasks = _tasks.value!!)
    }

    fun onTaskDeleteUndo() {
        todoWithTask?.let { todo ->
            if (taskToDelete != null && deletedPosition != -1) {
                val newList = todo.tasks.toMutableList().also {
                    it.add(deletedPosition, taskToDelete!!)
                }
                todoViewModel.onEditTodo(todo, newList)
                resetDeleteState()
            }
        }
        deletingTask = false
    }

    fun onButtonNameChange(name: String) {
        buttonName = name
    }

    private fun onTaskStateChanged(todo: Todo, task: Task, newList: MutableList<Task>) {
        val updatedTask = task.copy(completed = !task.completed)
        if (!task.completed)
            newList.add(updatedTask)
        else
            newList.add(newList.indexOfLast { !it.completed } + 1, updatedTask)
        _tasks.value = newList
        todoViewModel.onEditTodo(todo, newList)
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