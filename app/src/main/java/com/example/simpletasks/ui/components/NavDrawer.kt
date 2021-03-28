package com.example.simpletasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.simpletasks.data.todo.TodoViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun NavDrawerContent(
    todoViewModel: TodoViewModel,
    state: DrawerState,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val todos by todoViewModel.readAllTodos().collectAsState(initial = emptyList())

//    LazyColumn {
//        item { NavDrawerHeader() }
//        item {
//            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.space_between_8)))
//        }
//        item {
//            val route = Screen.Home.route
//            val isSelected = Screen.Home.route == todoViewModel.selectedTodo
//            NavDrawerRow(
//                icon = Icons.Rounded.Home,
//                iconColor = MaterialTheme.colors.primary,
//                isSelected = isSelected,
//                title = stringResource(id = R.string.lists),
//                onRowSelected = {
//                    todoViewModel.onTodoSelect(it)
//                    scope.launch {
//                        navController.navigate(route) {
//                            popUpTo(route) { inclusive = true }
//                            launchSingleTop = true
//                        }
//                        todoViewModel.clearNameField()
//                        state.close()
//                    }
//                }
//            )
//        }
//        item {
//            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.space_between_2)))
//        }
//        items(todos.sortedBy { it.name }) { todo ->
//            val route = createTodoRoute(todo.id)
//            val isSelected = todo.name == todoViewModel.selectedTodo
//            NavDrawerRow(
//                icon = Icons.Filled.Circle,
//                iconColor = colorResource(id = todo.colorResource),
//                title = todo.name,
//                isSelected = isSelected,
//                onRowSelected = {
//                    todoViewModel.onTodoSelect(it)
//                    todoViewModel.onNameChange(it)
//                    scope.launch {
//                        navController.navigate(route) {
//                            popUpTo(route) { inclusive = true }
//                        }
//                        todoViewModel.clearNameField()
//                        state.close()
//                    }
//                }
//            )
//            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.space_between_2)))
//        }
//        item {
//            NavDrawerRow(
//                icon = Icons.Rounded.Add,
//                iconColor = MaterialTheme.colors.primary,
//                isSelected = stringResource(id = R.string.add_list) == todoViewModel.selectedTodo,
//                title = stringResource(id = R.string.add_list),
//                onRowSelected = {
//                    todoViewModel.clearNameField()
//                    todoViewModel.onDialogStatusChange(true)
//                    scope.launch { state.close() }
//                }
//            )
//        }
//    }
}

@Composable
private fun NavDrawerHeader() {
    Row(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(4f)
                .fillMaxHeight()
                .background(MaterialTheme.colors.primary)
                .padding(8.dp)
        ) {
            AppNameText(color = Color.Black)
            CreatorByNameText()
        }
        Spacer(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colors.primaryVariant.copy(alpha = 0.9f))
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colors.primaryVariant)
        )
    }
}

@Composable
private fun NavDrawerRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    isSelected: Boolean = false,
    onRowSelected: (String) -> Unit = {}
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.2f) else MaterialTheme.colors.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = isSelected,
                    onClick = { onRowSelected(title) }
                )
                .padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.h6
            )
        }
    }
}