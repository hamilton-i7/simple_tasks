package com.example.simpletasks.ui.task.edit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.simpletasks.R
import com.example.simpletasks.data.todo.Todo

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
            Text(text = text)
            Spacer(
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.space_between_10)
                )
            )
            Icon(
                imageVector = if (expanded)
                    Icons.Rounded.ExpandLess
                else
                    Icons.Rounded.ExpandMore,
                contentDescription = null
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
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            ),
            cursorBrush = SolidColor(MaterialTheme.colors.primary),
            modifier = modifier
        )
    }
}

@Composable
fun TodoDropdownMenu(
    expanded: Boolean,
    todos: List<Todo>,
    onDismiss: () -> Unit,
    onClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.widthIn(min = 160.dp)
    ) {
        Text(
            text = stringResource(id = R.string.move_task_to),
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.space_between_16)
            )
        )

        todos.forEach { todo ->
            DropdownMenuItem(onClick = onClick) {
                Text(text = todo.name)
            }
        }
    }
}