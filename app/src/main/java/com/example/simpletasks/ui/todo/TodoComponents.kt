package com.example.simpletasks.ui.todo

import androidx.annotation.ColorRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.simpletasks.R
import com.example.simpletasks.data.label.Label
import com.example.simpletasks.ui.components.LabelOptions

@Composable
fun UncompletedTaskRow(
    name: String,
    modifier: Modifier = Modifier,
    onTaskComplete: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onTaskComplete) {
            Icon(
                imageVector = Icons.Outlined.Circle,
                contentDescription = stringResource(id = R.string.check_task)
            )
        }
        Text(
            text = name,
            maxLines = 1,
            modifier = modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CompletedTaskRow(
    name: String,
    @ColorRes iconColor: Int,
    modifier: Modifier = Modifier,
    onTaskUncheck: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onTaskUncheck) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = stringResource(id = R.string.uncheck_task),
                tint = colorResource(id = iconColor)
            )
        }
        Text(
            text = name,
            maxLines = 1,
            textDecoration = TextDecoration.LineThrough,
            color = MaterialTheme.colors.primary,
            modifier = modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TodoFAB(
    @ColorRes backgroundColor: Int,
    onClick: () -> Unit
) {
    FloatingActionButton(
        backgroundColor = colorResource(id = backgroundColor),
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = stringResource(id = R.string.add_new_task)
        )
    }
}

@Composable
fun CompletedIndicator(
    isExpanded: Boolean,
    onExpandChange: () -> Unit,
    completedAmount: Int
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onExpandChange)
                .padding(start = 16.dp, top = 2.dp, bottom = 2.dp)
        ) {
            Text(
                text = stringResource(R.string.completed, completedAmount),
                style = MaterialTheme.typography.body2
            )
            if (isExpanded) {
                Icon(
                    imageVector = Icons.Rounded.ExpandLess,
                    contentDescription = stringResource(id = R.string.hide_labels),
                    modifier = Modifier.padding(12.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = stringResource(id = R.string.show_labels),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun LabelDialog(
    labels: List<Label>,
    onDismissRequest: () -> Unit,
    @ColorRes selectedOption: Int,
    onOptionsSelected: (Int) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(
                    dimensionResource(id = R.dimen.space_between_16)
                )
            ) {
                LabelOptions(
                    labels = labels,
                    selectedOption = selectedOption,
                    onOptionsSelected = onOptionsSelected
                )
            }
        }
    }
}