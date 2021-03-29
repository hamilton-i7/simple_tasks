package com.example.simpletasks.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DrawerState
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ListAlt
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.simpletasks.R
import com.example.simpletasks.data.label.LabelSource
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.components.NewListDialog
import com.example.simpletasks.ui.components.NoDataDisplay
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import com.example.simpletasks.util.createTodoRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun HomeScreen(
    state: DrawerState,
    navController: NavController,
    lifecycleOwner: LifecycleOwner,
    todoViewModel: TodoViewModel
) {
    val labels = LabelSource.readLabels()
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val (todoName, setTodoName) = rememberSaveable { mutableStateOf("") }
    val todoCardAdapter = TodoCardAdapter(navController)
    val focusManager = LocalFocusManager.current
    val todosState by todoViewModel.todos.observeAsState(initial = emptyList())
    val query by todoViewModel.searchQuery.collectAsState()

    todoViewModel.todos.observe(lifecycleOwner) {
        todoCardAdapter.submitList(it.reversed())
    }

    SimpleTasksTheme {
        Scaffold(
            topBar = {
                     HomeTopBar(todoViewModel, state)
            },
            floatingActionButton = {
                HomeFAB {
                    todoViewModel.onDialogStatusChange(true)
                    focusManager.clearFocus()
                }
            }
        ) {
            Box(
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.space_between_8)
                )
            ) {
                when {
                    todosState.isEmpty() && query.isNotEmpty()  -> {
                        NoDataDisplay(message = stringResource(id = R.string.no_lists_found)) {
                            Icon(
                                imageVector = Icons.Rounded.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(
                                    dimensionResource(id = R.dimen.no_data_icon_size)
                                )
                            )
                        }
                    }
                    todosState.isEmpty() -> {
                        NoDataDisplay(message = stringResource(id = R.string.no_lists_created)) {
                            Icon(
                                imageVector = Icons.Rounded.ListAlt,
                                contentDescription = null,
                                modifier = Modifier.size(
                                    dimensionResource(id = R.dimen.no_data_icon_size)
                                )
                            )
                        }
                    }
                    else -> {
                        AndroidView({ context ->
                            RecyclerView(context).apply {
                                layoutManager = StaggeredGridLayoutManager(
                                    2, StaggeredGridLayoutManager.VERTICAL
                                )
                                adapter = todoCardAdapter
                            }
                        }, modifier = Modifier.fillMaxSize())
                    }
                }

                if (todoViewModel.isDialogVisible) {
                    NewListDialog(
                        todoName = todoName,
                        onNewNameChange = setTodoName,
                        enabled = todoName.trim().isNotEmpty(),
                        labels = labels,
                        isExpanded = isExpanded,
                        onExpandChange = { isExpanded = !isExpanded },
                        onDismissRequest = {
                            todoViewModel.onDialogStatusChange(false)
                        },
                        selectedOption = todoViewModel.newTodoColor,
                        onOptionsSelected = todoViewModel::onNewColorChange,
                        onCancel = {
                            todoViewModel.onCancelDialog()
                            setTodoName("")
                            isExpanded = false
                        },
                        onDone = {
                            todoViewModel.onCreateDone(todoName)
                            goToTodoScreen(navController, todoViewModel.newTodo)
                            setTodoName("")
                        }
                    )
                }
            }
        }
    }
}

private fun goToTodoScreen(navController: NavController, todo: Todo?) {
    todo?.let {
        val route = createTodoRoute(it.id)
        navController.navigate(route)
    }
}