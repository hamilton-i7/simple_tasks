package com.example.simpletasks.ui.components

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.simpletasks.R

@Composable
fun DoneTopBar(
    title: String,
    onUpButtonClick: () -> Unit,
    onDoneClick: () -> Unit,
    doneEnabled: Boolean
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onUpButtonClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.go_back)
                )
            }
        },
        actions = {
            TextButton(
                onClick = onDoneClick,
                enabled = doneEnabled
            ) {
                Text(text = stringResource(id = R.string.done))
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp,
    )
}