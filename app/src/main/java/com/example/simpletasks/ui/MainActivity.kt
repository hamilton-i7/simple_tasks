package com.example.simpletasks.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.compose.*
import com.example.simpletasks.data.settings.SettingsViewModel
import com.example.simpletasks.data.task.TaskViewModel
import com.example.simpletasks.data.task.TaskViewModelFactory
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.home.HomeScreen
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private val settingsViewModel by viewModels<SettingsViewModel>()
    private val todoViewModel by viewModels<TodoViewModel>()
    private val taskViewModel by viewModels<TaskViewModel> {
        TaskViewModelFactory(todoViewModel)
    }

    @ExperimentalFoundationApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()
            val state = rememberDrawerState(initialValue = DrawerValue.Closed)

            SimpleTasksTheme {
                ModalDrawer(
                    drawerContent = {
//                        NavDrawerContent(todoViewModel, state, navController)
                    },
                    drawerShape = MaterialTheme.shapes.large,
                    drawerState = state
                ) {
                    NavHost(navController, startDestination = Screen.Home.route) {
                        composable(Screen.Home.route) { backStackEntry ->
                            HomeScreen(
                                navController = navController,
                                todoViewModel = todoViewModel,
                                state = state,
                                lifecycleOwner = this@MainActivity
                            )
                            this@MainActivity.onBackPressedDispatcher.addCallback(backStackEntry,
                                object : OnBackPressedCallback(true) {
                                    override fun handleOnBackPressed() {
                                        if (state.isOpen)
                                            scope.launch { state.close() }
                                        else
                                            this@MainActivity.finish()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}