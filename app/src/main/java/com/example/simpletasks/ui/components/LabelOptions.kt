package com.example.simpletasks.ui.components

import androidx.annotation.ColorRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.example.simpletasks.R
import com.example.simpletasks.data.label.Label

@ExperimentalFoundationApi
@Composable
fun LabelOptions(
    labels: List<Label>,
    @ColorRes selectedOption: Int,
    onOptionsSelected: (Int) -> Unit
) {
    LazyVerticalGrid(cells = GridCells.Fixed(5)) {
        items(labels) { label ->
            val selected = label.color == selectedOption
            IconButton(
                onClick = { onOptionsSelected(label.color) },
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = stringResource(id = R.string.label_selected),
                        tint = colorResource(id = label.color)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = stringResource(id = label.name),
                        tint = colorResource(id = label.color)
                    )
                }
            }
        }
    }
}