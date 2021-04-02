package com.example.simpletasks.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.simpletasks.R
import com.example.simpletasks.data.task.Task
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.components.NavigationIcon
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

const val MAX_TASK_ROWS = 9

@ExperimentalCoroutinesApi
@Composable
fun HomeTopBar(
    todoViewModel: TodoViewModel,
    state: DrawerState
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val query by todoViewModel.searchQuery.collectAsState()

    TopAppBar(
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp,
        contentPadding = PaddingValues(
            vertical = dimensionResource(id = R.dimen.space_between_8),
            horizontal = dimensionResource(id = R.dimen.space_between_4)
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            NavigationIcon {
                scope.launch { state.open() }
                focusManager.clearFocus()
            }
            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.space_between_2)))
            SearchField(query, todoViewModel::onQueryChange, focusManager)
        }
    }
}

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    focusManager: FocusManager
) {
    Card(
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 12.dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(text = stringResource(id = R.string.search_list))
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                }
            )
        )
    }
}


@Composable
fun TodoCard(todo: Todo, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier
            .padding(6.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Circle,
                    contentDescription = null,
                    tint = colorResource(id = todo.colorResource),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = todo.name,
                    style = MaterialTheme.typography.h6,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.padding(
                dimensionResource(id = R.dimen.space_between_6)
            ))
            if (todo.tasks.size <= MAX_TASK_ROWS + 1) {
                todo.tasks.forEach { task ->
                    TaskRow(todo, task)
                    Spacer(modifier = Modifier.padding(4.dp))
                }
            } else {
                todo.tasks.take(MAX_TASK_ROWS).forEach { task ->
                    TaskRow(todo, task)
                    Spacer(modifier = Modifier.padding(4.dp))
                }
                Icon(
                    imageVector = Icons.Filled.MoreHoriz,
                    contentDescription = stringResource(id = R.string.more_tasks)
                )
            }
        }
    }
}

@Composable
fun TaskRow(todo: Todo, task: Task, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!task.completed) {
            Icon(
                imageVector = Icons.Outlined.Circle,
                contentDescription = null,
                modifier = Modifier.size(17.dp)
            )
            Spacer(
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.space_between_4)
                )
            )
            Text(
                text = task.name,
                style = MaterialTheme.typography.body1,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = colorResource(id = todo.colorResource),
                modifier = Modifier.size(16.dp)
            )
            Spacer(
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.space_between_4)
                )
            )
            Text(
                text = task.name,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.primary,
                textDecoration = TextDecoration.LineThrough,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}

@Composable
fun HomeFAB(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = stringResource(id = R.string.add_list)
        )
    }
}