package com.example.simpletasks.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.simpletasks.R

@Composable
fun NoDataDisplay(
    message: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(modifier = Modifier.padding(
                dimensionResource(id = R.dimen.space_between_8)
            ))
            Text(
                text = message,
                style = MaterialTheme.typography.h5
            )
        }
    }
}