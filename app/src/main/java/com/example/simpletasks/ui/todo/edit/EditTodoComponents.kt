package com.example.simpletasks.ui.todo.edit

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.simpletasks.R

@Composable
fun TodoNameTextField(
    name: String,
    onNameChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
    onDone: () -> Unit
) {
    TextField(
        value = name,
        onValueChange = onNameChange,
        label = {
            Text(text = stringResource(id = R.string.list_name))
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        ),
        singleLine = true,
        isError = isError,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent
        ),
        modifier = modifier
    )
}

@Composable
fun ErrorText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.caption,
        color = MaterialTheme.colors.error
    )
}