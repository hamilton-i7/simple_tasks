package com.example.simpletasks.ui.task

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.simpletasks.R

@Composable
fun DetailsTextField(
    details: String,
    onDetailsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = details,
        onValueChange = onDetailsChange,
        placeholder = {
            Text(text = stringResource(id = R.string.add_details))
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent
        ),
        modifier = modifier
    )

}