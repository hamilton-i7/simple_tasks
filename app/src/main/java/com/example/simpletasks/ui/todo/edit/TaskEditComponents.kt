package com.example.simpletasks.ui.todo.edit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
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
import com.example.simpletasks.R

@Composable
fun MarkButton(isCompleted: Boolean, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(
            text = if (isCompleted)
                stringResource(id = R.string.mark_uncompleted)
            else
                stringResource(id = R.string.mark_completed)
        )
    }
}

@Composable
fun ListButton(name: String, isExpanded: Boolean, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = name)
            Spacer(
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.space_between_10)
                )
            )
            Icon(
                imageVector = if (isExpanded)
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
    isCompleted: Boolean,
    onDone: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface {
        BasicTextField(
            value = name,
            onValueChange = onNameChange,
            readOnly = isCompleted,
            textStyle = if (isCompleted)
                MaterialTheme.typography.h5.copy(
                    textDecoration = TextDecoration.LineThrough,
                    color = MaterialTheme.colors.primary
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