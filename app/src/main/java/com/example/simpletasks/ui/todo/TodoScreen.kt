package com.example.simpletasks.ui.todo

import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.DrawerState
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.Screen
import com.example.simpletasks.ui.components.NewListDialog
import com.example.simpletasks.ui.components.NoDataDisplay
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import com.example.simpletasks.util.DragManager
import com.example.simpletasks.util.createNewTaskRoute
import com.example.simpletasks.util.createTodoEditRoute
import com.example.simpletasks.util.createTodoRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

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

    todo?.let { currentTodo ->
        todoViewModel.onNameChange(currentTodo.name)
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

        var isLabelDialogVisible by rememberSaveable { mutableStateOf(false) }
        var isOverflowMenuVisible by rememberSaveable { mutableStateOf(false) }
        val (newTodoName, setNewTodoName) = rememberSaveable { mutableStateOf("") }
        var isExpanded by rememberSaveable { mutableStateOf(false) }

        SimpleTasksTheme {
            Scaffold(
                topBar = {
                    ListTopBar(
                        todo = currentTodo,
                        isMenuVisible = isOverflowMenuVisible,
                        onNavigationIconClick = {
                            scope.launch { state.open() }
                        },
                        onShowMenu = { isOverflowMenuVisible = true },
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
                    TodoFAB(currentTodo.colorResource) {
                        goToCreateTaskScreen(navController, currentTodo.id)
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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
                                goToTodoScreen(navController, todoViewModel.newTodo)
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
                            selectedOption = currentTodo.colorResource,
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

                            if (settings.completedTasksExpanded) {
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

private fun goToTodoScreen(navController: NavController, todo: Todo?) {
    todo?.let {
        val route = createTodoRoute(it.id)
        navController.navigate(route)
    }
}