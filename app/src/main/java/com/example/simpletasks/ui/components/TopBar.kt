package com.example.simpletasks.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.simpletasks.R

@Composable
fun NavigationIcon(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Rounded.Menu,
            contentDescription = stringResource(id = R.string.menu_icon)
        )
    }
}