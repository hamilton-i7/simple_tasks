package com.example.simpletasks.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DrawerState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
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

    todoViewModel.todos.observe(lifecycleOwner) {
        todoCardAdapter.submitList(it.reversed())
    }

    SimpleTasksTheme {
        Scaffold(
            topBar = {
                     HomeTopBar(todoViewModel, state)
            },
            floatingActionButton = {
                HomeFAB { todoViewModel.onDialogStatusChange(true) }
            }
        ) {
            Box(
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.space_between_8)
                )
            ) {
                AndroidView({ context ->
                    RecyclerView(context).apply {
                        layoutManager = StaggeredGridLayoutManager(
                            2, StaggeredGridLayoutManager.VERTICAL
                        )
                        adapter = todoCardAdapter
                    }
                }, modifier = Modifier.fillMaxSize())

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