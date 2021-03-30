package com.example.simpletasks.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.simpletasks.R

@Composable
fun SelectLabelRow(isExpanded: Boolean, onExpandChange: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onExpandChange
                )
                .padding(
                    vertical = dimensionResource(id = R.dimen.space_between_10)
                )
        ) {
            Text(
                text = stringResource(id = R.string.select_label_color),
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.space_between_2)))
            if (isExpanded) {
                Icon(
                    imageVector = Icons.Rounded.ExpandLess,
                    contentDescription = stringResource(id = R.string.hide_labels)
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = stringResource(id = R.string.show_labels)
                )
            }
        }
    }
}