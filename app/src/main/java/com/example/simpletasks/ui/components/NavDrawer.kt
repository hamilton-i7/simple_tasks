package com.example.simpletasks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.popUpTo
import com.example.simpletasks.R
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.Screen
import com.example.simpletasks.util.createTodoRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@Composable
fun NavDrawerContent(
    todoViewModel: TodoViewModel,
    state: DrawerState,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val todos by todoViewModel.readAllTodos().collectAsState(initial = emptyList())

    LazyColumn {
        item { NavDrawerHeader() }
        item {
            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.space_between_8)))
        }
        item {
            val isSelected = Screen.Home.route == todoViewModel.selectedRoute
            NavDrawerRow(
                icon = Icons.Rounded.Home,
                iconColor = MaterialTheme.colors.primary,
                isSelected = isSelected,
                title = stringResource(id = R.string.lists),
                onRowSelected = {
                    todoViewModel.onTodoSelect(Screen.Home.route)
                    scope.launch {
                        val route = Screen.Home.route
                        navController.navigate(route) {
                            popUpTo(route) { inclusive = true }
                            launchSingleTop = true
                        }
                        state.close()
                    }
                }
            )
        }
        item {
            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.space_between_2)))
        }
        items(todos.sortedBy { it.name }) { todo ->
            val route = createTodoRoute(todo.id)
            val isSelected = route == todoViewModel.selectedRoute
            NavDrawerRow(
                icon = Icons.Filled.Circle,
                iconColor = colorResource(id = todo.colorResource),
                title = todo.name,
                isSelected = isSelected,
                onRowSelected = {
                    todoViewModel.onTodoSelect(route)
                    scope.launch {
                        navController.navigate(route) {
                            popUpTo(route) { inclusive = true }
                        }
                        state.close()
                    }
                }
            )
            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.space_between_2)))
        }
        item {
            NavDrawerRow(
                icon = Icons.Rounded.Add,
                iconColor = MaterialTheme.colors.primary,
                isSelected = stringResource(id = R.string.add_list) == todoViewModel.selectedRoute,
                title = stringResource(id = R.string.add_list),
                onRowSelected = {
                    scope.launch { state.close() }
                    val route = Screen.NewTodo.route
                    navController.navigate(route)
                }
            )
        }
    }
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