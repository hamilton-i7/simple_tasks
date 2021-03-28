package com.example.simpletasks.ui.task.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavController
import com.example.simpletasks.R
import com.example.simpletasks.data.task.TaskViewModel
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.components.DeleteTopBar
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun EditTaskScreen(
    todoId: String,
    taskId: String,
    todoViewModel: TodoViewModel,
    taskViewModel: TaskViewModel,
    navController: NavController
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var listButtonExpanded by rememberSaveable { mutableStateOf(false) }
    val todos by todoViewModel.readAllTodos().collectAsState(initial = emptyList())

    val todo by todoViewModel.readTodoById(todoId).observeAsState()

    todo?.let { currentTodo ->
        taskViewModel.onButtonNameChange(currentTodo.name)
        val task = currentTodo.tasks.first { task -> task.id == taskId }
        val (name, setName) = rememberSaveable { mutableStateOf(task.name) }
        val (details, setDetails) = rememberSaveable { mutableStateOf(task.details) }

        SimpleTasksTheme {
            Scaffold(
                topBar = {
                    DeleteTopBar(
                        onUpButtonClick = {},
                        onDelete = {}
                    )
                },
                floatingActionButton = {
                    MarkButton(taskCompleted = task.completed) {
                        taskViewModel.onTaskStateChange(task, currentTodo)
                        navController.navigateUp()
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(
                            dimensionResource(id = R.dimen.space_between_16)
                        )
                ) {
                    Box(contentAlignment = Alignment.BottomStart) {
                        if (task.completed)
                            ListButton(text = taskViewModel.buttonName, enabled = false)
                        else
                            ListButton(
                                text = taskViewModel.buttonName,
                                enabled = true,
                                expanded = listButtonExpanded,
                                onClick = { listButtonExpanded = true }
                            )
                        if (listButtonExpanded) {
                            TodoDropdownMenu(
                                expanded = listButtonExpanded,
                                task = task,
                                currentTodo = currentTodo,
                                todos = todos.sortedBy { it.name },
                                taskViewModel = taskViewModel,
                                onDismiss = { listButtonExpanded = false },
                            )
                        }
                    }
                    Spacer(
                        modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.space_between_10)
                        )
                    )
                    TaskTextField(
                        name = name,
                        onNameChange = setName,
                        readOnly = task.completed,
                        onDone = { focusManager.clearFocus() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.space_between_8))
                    )
                    Spacer(
                        modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.space_between_6)
                        )
                    )
                    DetailsRow(
                        details = details ?: "",
                        onDetailsChange = setDetails,
                        readOnly = task.completed,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}