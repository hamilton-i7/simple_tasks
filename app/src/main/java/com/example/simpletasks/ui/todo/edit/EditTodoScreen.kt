package com.example.simpletasks.ui.todo.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.example.simpletasks.R
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.components.DoneTopBar
import com.example.simpletasks.ui.components.SimpleTextField
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import com.example.simpletasks.util.createTodoRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun EditTodoScreen(
    todoId: String,
    todoViewModel: TodoViewModel,
    navController: NavController
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val todo by todoViewModel.readTodoById(todoId).observeAsState()

    todo?.let {
        val (name, setName) = rememberSaveable { mutableStateOf(it.name) }

        SimpleTasksTheme {
            Scaffold(
                topBar = {
                    DoneTopBar(
                        title = stringResource(id = R.string.rename_list),
                        doneEnabled = name.isNotEmpty(),
                        onDoneClick = {
                            todoViewModel.onEditDone(it, name)
                            goToTodoScreen(navController, todoId)
                        },
                        onUpButtonClick = {
                            navController.navigateUp()
                        }
                    )
                }
            ) {
                Surface {
                    Box(
                        modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.space_between_16)
                        )
                    ) {
                        SimpleTextField(
                            name = name,
                            onNameChange = setName,
                            label = stringResource(id = R.string.list_name),
                            modifier = Modifier.fillMaxWidth()
                        ) { keyboardController?.hideSoftwareKeyboard() }
                    }
                }
            }
        }
    }
}

private fun goToTodoScreen(navController: NavController, todoId: String) {
    val route = createTodoRoute(todoId)
    navController.navigate(route)
}

