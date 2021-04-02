package com.example.simpletasks.ui.todo.newtodo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.example.simpletasks.R
import com.example.simpletasks.data.label.LabelSource
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.components.DoneTopBar
import com.example.simpletasks.ui.components.LabelOptions
import com.example.simpletasks.ui.components.SelectLabelRow
import com.example.simpletasks.ui.components.SimpleTextField
import com.example.simpletasks.util.createTodoRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun NewTodoScreen(
    todoViewModel: TodoViewModel,
    navController: NavController
) {
    val focusManager = LocalFocusManager.current
    val labels = LabelSource.readLabels()
    var isLabelOptionsExpanded by mutableStateOf(false)

    todoViewModel.onNewTodoNameChange("")
    todoViewModel.onNewColorChange(R.color.default_color)

    Scaffold(
        topBar = {
            DoneTopBar(
                title = stringResource(id = R.string.create_list),
                onUpButtonClick = { navController.navigateUp() },
                onDoneClick = {
                    todoViewModel.onCreateDone(todoViewModel.newTodoName)
                    goToTodoScreen(navController, todoViewModel)
                },
                doneEnabled = todoViewModel.newTodoName.trim().isNotEmpty()
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
                    name = todoViewModel.newTodoName,
                    onNameChange = todoViewModel::onNewTodoNameChange,
                    label = stringResource(id = R.string.list_name),
                    modifier = Modifier.fillMaxWidth()
                ) { focusManager.clearFocus() }
                Spacer(
                    modifier = Modifier.padding(
                        dimensionResource(id = R.dimen.space_between_6)
                    )
                )
                SelectLabelRow(isExpanded = isLabelOptionsExpanded) {
                    isLabelOptionsExpanded = !isLabelOptionsExpanded
                }
                AnimatedVisibility(isLabelOptionsExpanded) {
                    LabelOptions(
                        labels = labels,
                        selectedOption = todoViewModel.newTodoColor,
                        onOptionsSelected = todoViewModel::onNewColorChange
                    )
                }
            }
        }
    }
}

@ExperimentalCoroutinesApi
private fun goToTodoScreen(
    navController: NavController,
    todoViewModel: TodoViewModel
) {
    todoViewModel.newTodo?.let {
        val route = createTodoRoute(it.id)
        todoViewModel.onTodoSelect(route)
        navController.navigate(route)
    }
}