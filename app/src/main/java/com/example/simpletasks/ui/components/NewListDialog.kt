package com.example.simpletasks.ui.components

import androidx.annotation.ColorRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.window.Dialog
import com.example.simpletasks.R
import com.example.simpletasks.data.label.Label
import com.example.simpletasks.data.todo.TodoViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun NewListDialog(
    todoViewModel: TodoViewModel,
    labels: List<Label>,
    isExpanded: Boolean,
    onExpandChange: () -> Unit,
    onDismissRequest: () -> Unit,
    @ColorRes selectedOption: Int,
    onOptionsSelected: (Int) -> Unit,
    onCancel: () -> Unit,
    onDone: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .padding(dimensionResource(id = R.dimen.space_between_16))
            ) {
                Column {
                    DialogTitle()
                    Spacer(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.space_between_4))
                    )
                    DialogTextField(
                        todoViewModel.newTodoName,
                        todoViewModel::onNewNameChange,
                        modifier = Modifier.padding(
                            bottom = dimensionResource(id = R.dimen.space_between_2)
                        )
                    )

                    if (todoViewModel.isInvalidName)
                        ErrorText(text = stringResource(id = R.string.invalid_name))
                    else if (todoViewModel.isRepeatedName)
                        ErrorText(text = stringResource(id = R.string.name_repeated))
                    Spacer(
                        modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.space_between_4)
                        )
                    )
                    DialogSelectRow(isExpanded, onExpandChange)
                    if (isExpanded) {
                        Spacer(
                            modifier = Modifier.padding(
                                dimensionResource(id = R.dimen.space_between_2)
                            )
                        )
                        LabelOptions(
                            labels = labels,
                            selectedOption = selectedOption,
                            onOptionsSelected = onOptionsSelected
                        )
                    }
                    Spacer(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.space_between_4))
                    )
                    DialogButtons(
                        onCancel = onCancel,
                        onDone = onDone,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogTitle() {
    Text(
        text = stringResource(id = R.string.create_list),
        style = MaterialTheme.typography.h6
    )
}

@ExperimentalComposeUiApi
@Composable
private fun DialogTextField(
    listName: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = listName,
        onValueChange = onNameChange,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent
        ),
        label = {
            Text(text = stringResource(id = R.string.list_name))
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hideSoftwareKeyboard()
            }
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun DialogSelectRow(isExpanded: Boolean, onExpandChange: () -> Unit) {
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

@Composable
private fun DialogButtons(
    onCancel: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        TextButton(onClick = onCancel) {
            Text(text = stringResource(id = R.string.cancel))
        }
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.space_between_8)))
        TextButton(onClick = onDone) {
            Text(text = stringResource(id = R.string.done))
        }
    }
}