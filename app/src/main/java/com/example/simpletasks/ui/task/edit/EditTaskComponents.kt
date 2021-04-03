package com.example.simpletasks.ui.task.edit

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.simpletasks.R
import com.example.simpletasks.data.task.Task
import com.example.simpletasks.data.task.TaskViewModel
import com.example.simpletasks.data.todo.Todo
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Composable
fun MarkButton(taskCompleted: Boolean, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(
            text = if (taskCompleted)
                stringResource(id = R.string.mark_uncompleted)
            else
                stringResource(id = R.string.mark_completed)
        )
    }
}

@Composable
fun ListButton(
    text: String,
    enabled: Boolean,
    expanded: Boolean = false,
    onClick: () -> Unit = {}
) {
    TextButton(
        onClick = onClick,
        enabled = enabled
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val degrees by animateFloatAsState(if (expanded) 180f else 0f)

            Text(text = text)
            Spacer(
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.space_between_10)
                )
            )
            Icon(
                imageVector = Icons.Rounded.ExpandMore,
                contentDescription = null,
                modifier = Modifier.rotate(degrees)
            )
        }
    }
}

@Composable
fun TaskTextField(
    name: String,
    onNameChange: (String) -> Unit,
    readOnly: Boolean,
    modifier: Modifier = Modifier,
    onDone: () -> Unit = {}
) {
    Surface {
        BasicTextField(
            value = name,
            onValueChange = onNameChange,
            readOnly = readOnly,
            textStyle = if (readOnly)
                MaterialTheme.typography.h5.copy(
                    textDecoration = TextDecoration.LineThrough,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.35f)
                ) else
                MaterialTheme.typography.h5.copy(
                    color = MaterialTheme.colors.onSurface
                ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            ),
            cursorBrush = SolidColor(MaterialTheme.colors.primary),
            modifier = modifier
        )
    }
}

@Composable
fun DetailsRow(
    details: String,
    onDetailsChange: (String) -> Unit,
    readOnly: Boolean,
    modifier: Modifier = Modifier,
) {
    Row {
        Icon(
            imageVector = Icons.Rounded.Notes,
            contentDescription = null,
            tint = if (readOnly)
                MaterialTheme.colors.onSurface.copy(alpha = 0.35f)
            else
                MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(
                top = dimensionResource(id = R.dimen.space_between_16)
            )
        )
        Spacer(
            modifier = Modifier.padding(
                dimensionResource(id = R.dimen.space_between_8)
            )
        )
        Surface {
            TextField(
                value = details,
                onValueChange = onDetailsChange,
                readOnly = readOnly,
                placeholder = {
                    Text(text = stringResource(id = R.string.add_details))
                },
                textStyle = if (readOnly)
                    MaterialTheme.typography.body1.copy(
                        textDecoration = TextDecoration.LineThrough,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.35f)
                    ) else
                    MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.onSurface
                    ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.None,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = modifier
            )
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun TodoDropdownMenu(
    expanded: Boolean,
    task: Task,
    todos: List<Todo>,
    taskViewModel: TaskViewModel,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.widthIn(min = 160.dp)
    ) {
        Text(
            text = stringResource(id = R.string.move_task_to),
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.space_between_16))
                .padding(bottom = dimensionResource(id = R.dimen.space_between_8))
        )

        todos.forEach { todo ->
            DropdownMenuItem(
                onClick = {
                    taskViewModel.onButtonNameChange(todo.name)
                    taskViewModel.onTaskSwitch(task, todo)
                    onDismiss()
                },
                modifier = Modifier.background(
                    if (todo.tasks.contains(task))
                        MaterialTheme.colors.primary.copy(alpha = 0.35f)
                    else
                        Color.Transparent
                )
            ) {
                Text(text = todo.name)
            }
        }
    }
}