package com.example.simpletasks.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.simpletasks.R
import com.example.simpletasks.data.task.Task
import com.example.simpletasks.data.todo.Todo

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
            todo.tasks.forEach { task ->
                TaskRow(todo, task)
                Spacer(modifier = Modifier.padding(4.dp))
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