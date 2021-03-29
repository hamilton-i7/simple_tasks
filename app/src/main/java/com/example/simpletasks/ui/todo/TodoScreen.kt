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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.simpletasks.ui.components.NewListDialog
import com.example.simpletasks.ui.components.NoDataDisplay
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import com.example.simpletasks.util.DragManager
import com.example.simpletasks.util.createNewTaskRoute
import com.example.simpletasks.util.createTodoEditRoute
import com.example.simpletasks.util.createTodoRoute
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
    state: DrawerState
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
        var isLabelDialogVisible by rememberSaveable { mutableStateOf(false) }
        var isOverflowMenuVisible by rememberSaveable { mutableStateOf(false) }
        val (newTodoName, setNewTodoName) = rememberSaveable { mutableStateOf("") }
        var isExpanded by rememberSaveable { mutableStateOf(false) }

        SimpleTasksTheme {
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
                            val route = createTodoEditRoute(currentTodo.id)
                            navController.navigate(route)
                            isOverflowMenuVisible = false
                        },
                        onLabelColorChange = {
                            isLabelDialogVisible = true
                            isOverflowMenuVisible = false
                        },
                        onCompletedTasksDelete = {
                            isOverflowMenuVisible = false
                            taskViewModel.onCompletedTasksDelete(currentTodo)
                        },
                        onListDelete = {
                            isOverflowMenuVisible = false
                            goToHomeScreen(navController, todoViewModel)
                            todoViewModel.deleteTodo(currentTodo)
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
                        if (todoViewModel.isDialogVisible) {
                            NewListDialog(
                                todoName = newTodoName,
                                onNewNameChange = setNewTodoName,
                                enabled = newTodoName.trim().isNotEmpty(),
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
                                    setNewTodoName("")
                                    isExpanded = false
                                },
                                onDone = {
                                    todoViewModel.onCreateDone(newTodoName)
                                    goToTodoScreen(navController, todoViewModel)
                                    setNewTodoName("")
                                }
                            )
                        }

                        if (isLabelDialogVisible) {
                            LabelDialog(
                                labels = labels,
                                onDismissRequest = {
                                    isLabelDialogVisible = false
                                },
                                selectedOption = todoViewModel.colorResource,
                                onOptionsSelected = {
                                    todoViewModel.onLabelChange(currentTodo, it)
                                    isLabelDialogVisible = false
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
                    if (taskViewModel.upToDelete) {
                        val message = stringResource(id = R.string.task_deleted)
                        val undoStr = stringResource(id = R.string.undo)
                        scope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = undoStr
                            )
                        }
                    }
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
                        taskViewModel.onUpToDelete(toDelete = false)
                    }
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