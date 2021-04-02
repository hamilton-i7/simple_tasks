package com.example.simpletasks.ui.todo

import androidx.annotation.ColorRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.simpletasks.R
import com.example.simpletasks.data.label.Label
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.ui.components.LabelOptions
import com.example.simpletasks.ui.components.NavigationIcon

@Composable
fun ListTopBar(
    todo: Todo,
    isMenuVisible: Boolean,
    onNavigationIconClick: () -> Unit,
    onShowOverflowMenu: () -> Unit,
    onDismissRequest: () -> Unit,
    onListRename: () -> Unit,
    onLabelColorChange: () -> Unit,
    onCompletedTasksDelete: () -> Unit,
    onListDelete: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            NavigationIcon(onClick = onNavigationIconClick)
        },
        title = {
            Text(text = todo.name)
        },
        actions = {
            Box(contentAlignment = AbsoluteAlignment.TopRight) {
                IconButton(onClick = onShowOverflowMenu) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = stringResource(id = R.string.view_options)
                    )
                }
                TodoSettingsMenu(
                    isMenuVisible = isMenuVisible,
                    onDismissRequest = onDismissRequest,
                    onRenameList = onListRename,
                    onChangeLabelColor = onLabelColorChange,
                    onDeleteCompletedTasks = onCompletedTasksDelete,
                    onDeleteList = onListDelete
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp,
    )
}

@Composable
fun TodoSettingsMenu(
    isMenuVisible: Boolean,
    onDismissRequest: () -> Unit,
    onRenameList: () -> Unit,
    onChangeLabelColor: () -> Unit,
    onDeleteCompletedTasks: () -> Unit,
    onDeleteList: () -> Unit,
) {
    DropdownMenu(
        expanded = isMenuVisible,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(onClick = onRenameList) {
            Text(
                text = stringResource(id = R.string.change_list_name),
                style = MaterialTheme.typography.body2
            )
        }
        DropdownMenuItem(onClick = onChangeLabelColor) {
            Text(
                text = stringResource(id = R.string.change_label_color),
                style = MaterialTheme.typography.body2
            )
        }
        DropdownMenuItem(onClick = onDeleteCompletedTasks) {
            Text(
                text = stringResource(id = R.string.delete_completed_tasks),
                style = MaterialTheme.typography.body2
            )
        }
        DropdownMenuItem(onClick = onDeleteList) {
            Text(
                text = stringResource(id = R.string.delete_list),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
fun UncompletedTaskRow(
    name: String,
    details: String? = null,
    onTaskComplete: () -> Unit,
    onNameClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onTaskComplete) {
            Icon(
                imageVector = Icons.Outlined.Circle,
                contentDescription = stringResource(id = R.string.check_task)
            )
        }
        Column(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onNameClick
            )
        ) {
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!details.isNullOrEmpty()) {
                Text(
                    text = details,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.50f),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CompletedTaskRow(
    name: String,
    details: String? = null,
    @ColorRes iconColor: Int,
    onTaskUncheck: () -> Unit,
    onNameClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onTaskUncheck) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = stringResource(id = R.string.uncheck_task),
                tint = colorResource(id = iconColor)
            )
        }
        Column(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onNameClick
            )
        ) {
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = TextDecoration.LineThrough,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.70f)

            )
            if (!details.isNullOrEmpty()) {
                Text(
                    text = details,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = TextDecoration.LineThrough,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
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
            val degrees: Float by animateFloatAsState(if (isExpanded) 180f else 0f)
            Icon(
                imageVector = Icons.Rounded.ExpandMore,
                contentDescription = if (isExpanded)
                    stringResource(id = R.string.hide_labels)
                else
                    stringResource(id = R.string.show_labels),
                modifier = Modifier
                    .padding(12.dp)
                    .rotate(degrees)
            )
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