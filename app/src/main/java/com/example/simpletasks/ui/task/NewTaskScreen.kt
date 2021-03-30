package com.example.simpletasks.ui.task

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.simpletasks.R
import com.example.simpletasks.data.task.TaskViewModel
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.components.DoneTopBar
import com.example.simpletasks.ui.components.SimpleTextField
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun NewTaskScreen(
    todoId: String,
    todoViewModel: TodoViewModel,
    taskViewModel: TaskViewModel,
    navController: NavController
) {
    val focusManager = LocalFocusManager.current
    var isDetailsTextFieldVisible by rememberSaveable { mutableStateOf(false) }
    val (name, setName) = rememberSaveable { mutableStateOf("") }
    val (details, setDetails) = rememberSaveable { mutableStateOf("") }
    val todo by todoViewModel.readTodoById(todoId).observeAsState()

    todo?.let {
        SimpleTasksTheme {
            Scaffold(
                topBar = {
                    DoneTopBar(
                        title = stringResource(id = R.string.create_task),
                        onUpButtonClick = { navController.navigateUp() },
                        onDoneClick = {
                            taskViewModel.onTaskCreate(it, name, details)
                            navController.navigateUp()
                        },
                        doneEnabled = name.trim().isNotEmpty()
                    )
                }
            ) {
                Surface {
                    Column(
                        modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.space_between_16)
                        )
                    ) {
                        SimpleTextField(
                            name = name,
                            onNameChange = setName,
                            label = stringResource(id = R.string.task_name),
                            modifier = Modifier.fillMaxWidth()
                        ) { focusManager.clearFocus() }
                        Spacer(
                            modifier = Modifier.padding(
                                dimensionResource(id = R.dimen.space_between_8)
                            )
                        )
                        Row {
                            IconButton(onClick = {
                                isDetailsTextFieldVisible = !isDetailsTextFieldVisible
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.Notes,
                                    contentDescription = null
                                )
                            }
                            if (isDetailsTextFieldVisible) {
                                DetailsTextField(
                                    details = details,
                                    onDetailsChange = setDetails,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}