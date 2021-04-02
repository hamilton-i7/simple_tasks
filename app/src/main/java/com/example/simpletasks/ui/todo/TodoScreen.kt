package com.example.simpletasks.ui.todo

import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletasks.R
import com.example.simpletasks.data.label.LabelSource
import com.example.simpletasks.data.settings.Settings
import com.example.simpletasks.data.settings.SettingsViewModel
import com.example.simpletasks.data.task.TaskViewModel
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.Screen
import com.example.simpletasks.ui.components.DefaultSnackbar
import com.example.simpletasks.ui.components.NoDataDisplay
import com.example.simpletasks.util.DragManager
import com.example.simpletasks.util.SnackbarController
import com.example.simpletasks.util.createNewTaskRoute
import com.example.simpletasks.util.createTodoEditRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
@Composable
fun TodoScreen(
    todoId: String,
    settingsViewModel: SettingsViewModel,
    todoViewModel: TodoViewModel,
    taskViewModel: TaskViewModel,
    navController: NavController,
    lifecycleOwner: LifecycleOwner,
    state: DrawerState,
) {
    val settings by settingsViewModel.readSettings().observeAsState(
        initial = Settings()
    )
    val scope = rememberCoroutineScope()
    val todo by todoViewModel.readTodoById(todoId).observeAsState()
    val labels = LabelSource.readLabels()

    todoViewModel.onQueryChange("")

    todo?.let { currentTodo ->
        todoViewModel.onLabelChange(currentTodo, currentTodo.colorResource)
        taskViewModel.setTasks(currentTodo.tasks)
        taskViewModel.onButtonNameChange(currentTodo.name)

        /** Variable created to control the UI states made with Composables*/
        val tasks by taskViewModel.tasks.observeAsState(initial = currentTodo.tasks)

        val uncompletedTaskAdapter = UncompletedTaskAdapter(
            currentTodo,
            taskViewModel,
            navController
        )
        val completedTaskAdapter = CompletedTaskAdapter(
            currentTodo,
            todoViewModel,
            taskViewModel,
            navController
        )

        taskViewModel.tasks.observe(lifecycleOwner) {
            val uncompletedTasks = it.filter { task -> !task.completed }
            uncompletedTaskAdapter.apply {
                submitList(uncompletedTasks)
                updateList(uncompletedTasks)
            }

            val completedTasks = it.filter { task -> task.completed }
            completedTaskAdapter.submitList(completedTasks)
        }
        val scaffoldState = rememberScaffoldState()
        var isOverflowMenuVisible by mutableStateOf(false)
        var isLabelMenuVisible by mutableStateOf(false)

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                ListTopBar(
                    todo = currentTodo,
                    isMenuVisible = isOverflowMenuVisible,
                    onNavigationIconClick = {
                        scope.launch { state.open() }
                    },
                    onShowOverflowMenu = {
                        isOverflowMenuVisible = true
                    },
                    onDismissRequest = { isOverflowMenuVisible = false },
                    onListRename = {
                        isOverflowMenuVisible = false
                        val route = createTodoEditRoute(currentTodo.id)
                        navController.navigate(route)
                    },
                    onLabelColorChange = {
                        isOverflowMenuVisible = false
                        isLabelMenuVisible = true
                    },
                    onCompletedTasksDelete = {
                        isOverflowMenuVisible = false
                        taskViewModel.onCompletedTasksDelete(currentTodo)
                    },
                    onListDelete = {
                        isOverflowMenuVisible = false
                        todoViewModel.deleteTodo(currentTodo)
                        todoViewModel.onDeletingTodo(toDelete = true)
                        goToHomeScreen(navController, todoViewModel)
                    }
                )
            },
            floatingActionButton = {
                TodoFAB(todoViewModel.colorResource) {
                    goToCreateTaskScreen(navController, currentTodo.id)
                }
            },
            snackbarHost = {
                scaffoldState.snackbarHostState
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(
                            dimensionResource(id = R.dimen.space_between_8)
                        )
                ) {
                    if (isLabelMenuVisible) {
                        LabelDialog(
                            labels = labels,
                            onDismissRequest = {
                                isLabelMenuVisible = false
                            },
                            selectedOption = todoViewModel.colorResource,
                            onOptionsSelected = {
                                isLabelMenuVisible = false
                                todoViewModel.onLabelChange(currentTodo, it)
                            }
                        )
                    }

                    if (currentTodo.tasks.isEmpty()) {
                        NoDataDisplay(
                            message = stringResource(id = R.string.no_tasks_created),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.TaskAlt,
                                contentDescription = null,
                                modifier = Modifier.size(
                                    dimensionResource(id = R.dimen.no_data_icon_size)
                                )
                            )
                        }
                    } else {
                        AndroidView({ context ->
                            RecyclerView(context).apply {
                                val dragManager = DragManager(
                                    dragDirs = ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                                    swipeDirs = 0
                                )
                                val helper = ItemTouchHelper(dragManager)
                                helper.attachToRecyclerView(this)
                                layoutManager = LinearLayoutManager(context)
                                adapter = uncompletedTaskAdapter
                                overScrollMode = View.OVER_SCROLL_NEVER
                            }
                        }, modifier = Modifier.fillMaxWidth())

                        if (tasks.any { it.completed }) {
                            if (tasks.any { !it.completed }) {
                                Divider(
                                    modifier = Modifier.padding(
                                        horizontal = 0.dp,
                                        vertical = dimensionResource(
                                            id = R.dimen.space_between_8
                                        )
                                    )
                                )
                            }
                            CompletedIndicator(
                                isExpanded = settings.completedTasksExpanded,
                                onExpandChange = {
                                    settingsViewModel.onExpandChange(settings)
                                },
                                completedAmount =
                                tasks.filter { it.completed }.size
                            )

                            AnimatedVisibility(settings.completedTasksExpanded) {
                                AndroidView({ context ->
                                    RecyclerView(context).apply {
                                        layoutManager = LinearLayoutManager(context)
                                        adapter = completedTaskAdapter
                                        overScrollMode = View.OVER_SCROLL_NEVER
                                    }
                                }, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
                if (taskViewModel.deletingTask)
                    ShowSnackbar(scope, scaffoldState, taskViewModel)

                DefaultSnackbar(
                    snackbarHostState = scaffoldState.snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            end = dimensionResource(id = R.dimen.space_between_70)
                        )
                ) {
                    taskViewModel.onTaskDeleteUndo(currentTodo)
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    taskViewModel.onDeletingTask(toDelete = false)
                }
            }
        }
    }
}

private fun goToCreateTaskScreen(navController: NavController, todoId: String) {
    val route = createNewTaskRoute(todoId)
    navController.navigate(route)
}

@ExperimentalCoroutinesApi
private fun goToHomeScreen(
    navController: NavController,
    todoViewModel: TodoViewModel,
) {
    navController.navigate(Screen.Home.route)
    todoViewModel.onTodoSelect(Screen.Home.route)
}

@ExperimentalCoroutinesApi
@Composable
private fun ShowSnackbar(
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    taskViewModel: TaskViewModel
) {
    val snackbarController = SnackbarController(scope)
    val message = stringResource(id = R.string.task_deleted)
    val undoStr = stringResource(id = R.string.undo)
    snackbarController.getScope().launch {
        snackbarController.showSnackbar(
            scaffoldState = scaffoldState,
            message = message,
            actionLabel = undoStr
        )
        taskViewModel.onDeletingTask(toDelete = false)
    }
}