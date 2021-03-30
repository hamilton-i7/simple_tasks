package com.example.simpletasks.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.simpletasks.R

@Composable
fun DefaultSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { data ->
            Snackbar(
                action = {
                    data.actionLabel?.let { actionLabel ->
                        TextButton(onClick = onDismiss) {
                            Text(text = actionLabel)
                        }
                    }
                },
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.space_between_16)
                )
            ) {
                Text(text = data.message)
            }
        },
        modifier = modifier
    )
}