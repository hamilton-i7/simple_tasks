package com.example.simpletasks.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ListAlt
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.Screen
import com.example.simpletasks.ui.components.DefaultSnackbar
import com.example.simpletasks.ui.components.NoDataDisplay
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import com.example.simpletasks.util.SnackbarController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

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
    val todoCardAdapter = TodoCardAdapter(navController, todoViewModel)
    val focusManager = LocalFocusManager.current
    val todosState by todoViewModel.todos.observeAsState(initial = emptyList())
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
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
                    focusManager.clearFocus()
                    goToNewTodoScreen(navController)
                }
            },
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
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
                        }, modifier = Modifier.fillMaxWidth())
                    }
                }
                if (todoViewModel.deletingTodo)
                    ShowSnackbar(scope, scaffoldState, todoViewModel)

                DefaultSnackbar(
                    snackbarHostState = scaffoldState.snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            end = dimensionResource(id = R.dimen.space_between_70)
                        )
                ) {
                    todoViewModel.onTodoDeleteUndo()
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    todoViewModel.onDeletingTodo(toDelete = false)
                }
            }
        }
    }
}

private fun goToNewTodoScreen(navController: NavController) {
    val route = Screen.NewTodo.route
    navController.navigate(route)
}

@ExperimentalCoroutinesApi
@Composable
private fun ShowSnackbar(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    todoViewModel: TodoViewModel
) {
    val snackbarController = SnackbarController(scope)
    val message = stringResource(id = R.string.list_deleted)
    val undoStr = stringResource(id = R.string.undo)
    snackbarController.getScope().launch {
        snackbarController.showSnackbar(
            scaffoldState = scaffoldState,
            message = message,
            actionLabel = undoStr
        )
        todoViewModel.onDeletingTodo(toDelete = false)
    }
}