package com.example.simpletasks.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.simpletasks.data.settings.SettingsViewModel
import com.example.simpletasks.data.task.TaskViewModel
import com.example.simpletasks.data.task.TaskViewModelFactory
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.components.NavDrawerContent
import com.example.simpletasks.ui.home.HomeScreen
import com.example.simpletasks.ui.task.NewTaskScreen
import com.example.simpletasks.ui.task.edit.EditTaskScreen
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import com.example.simpletasks.ui.todo.TodoScreen
import com.example.simpletasks.ui.todo.edit.EditTodoScreen
import com.example.simpletasks.ui.todo.newtodo.NewTodoScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
class MainActivity : ComponentActivity() {

    private val settingsViewModel by viewModels<SettingsViewModel>()
    private val todoViewModel by viewModels<TodoViewModel>()
    private val taskViewModel by viewModels<TaskViewModel> {
        TaskViewModelFactory(todoViewModel)
    }

    private lateinit var navController: NavHostController
    private lateinit var drawerState: DrawerState
    private lateinit var coroutineScope: CoroutineScope

    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            navController = rememberNavController()
            coroutineScope = rememberCoroutineScope()
            drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

            SimpleTasksTheme {
                ModalDrawer(
                    drawerContent = {
                        NavDrawerContent(todoViewModel, drawerState, navController)
                    },
                    drawerShape = MaterialTheme.shapes.large,
                    drawerState = drawerState,
                    gesturesEnabled = drawerState.isOpen
                ) {
                    NavHost(navController, startDestination = Screen.Home.route) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                navController = navController,
                                todoViewModel = todoViewModel,
                                state = drawerState,
                                lifecycleOwner = this@MainActivity
                            )
                            navController.currentDestination?.label = Screen.Home.route
                        }
                        composable(
                            route = Screen.Todo.route,
                            arguments = listOf(navArgument(TODO_ARG) {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            backStackEntry.arguments?.getString(TODO_ARG)?.let {
                                TodoScreen(
                                    todoId = it,
                                    settingsViewModel = settingsViewModel,
                                    todoViewModel = todoViewModel,
                                    taskViewModel = taskViewModel,
                                    navController = navController,
                                    lifecycleOwner = this@MainActivity,
                                    state = drawerState
                                )
                                navController.currentDestination?.label = Screen.Todo.route
                            }
                        }
                        composable(Screen.NewTodo.route) {
                            NewTodoScreen(todoViewModel, navController)
                        }
                        composable(
                            route = Screen.EditTodo.route,
                            arguments = listOf(navArgument(TODO_ARG) {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            backStackEntry.arguments?.getString(TODO_ARG)?.let {
                                EditTodoScreen(
                                    todoId = it,
                                    todoViewModel = todoViewModel,
                                    navController = navController
                                )
                            }
                        }
                        composable(
                            route = Screen.EditTask.route,
                            arguments = listOf(
                                navArgument(TODO_ARG) { type = NavType.StringType },
                                navArgument(TASK_ARG) { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            if (
                                backStackEntry.arguments!!.getString(TODO_ARG) != null &&
                                backStackEntry.arguments!!.getString(TASK_ARG) != null
                            ) {
                                EditTaskScreen(
                                    todoId = backStackEntry.arguments!!.getString(TODO_ARG)!!,
                                    taskId = backStackEntry.arguments!!.getString(TASK_ARG)!!,
                                    todoViewModel = todoViewModel,
                                    taskViewModel = taskViewModel,
                                    navController = navController
                                )
                            }
                        }
                        composable(
                            route = Screen.NewTask.route,
                            arguments = listOf(navArgument(TODO_ARG) { type = NavType.StringType })
                        ) { backStackEntry ->
                            backStackEntry.arguments?.getString(TODO_ARG)?.let {
                                NewTaskScreen(
                                    todoId = it,
                                    todoViewModel = todoViewModel,
                                    taskViewModel = taskViewModel,
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.label) {
            Screen.Home.route -> {
                if (drawerState.isOpen)
                    coroutineScope.launch { drawerState.close() }
                else
                    this@MainActivity.finish()
            }
            Screen.Todo.route -> {
                if (drawerState.isOpen)
                    coroutineScope.launch { drawerState.close() }
                else {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                    todoViewModel.onTodoSelect(Screen.Home.route)
                }
            }
            else -> super.onBackPressed()
        }
    }
}