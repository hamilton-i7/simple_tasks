package com.example.simpletasks.ui.todo.edit

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.simpletasks.R
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TodoEditFragment : Fragment() {

    private val args by navArgs<TodoEditFragmentArgs>()

    private val todoViewModel by activityViewModels<TodoViewModel>()

    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return ComposeView(requireContext()).apply {
            setContent {
                val keyboardController = LocalSoftwareKeyboardController.current

                SimpleTasksTheme {
                    Surface {
                        Column(modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.space_between_16)
                        )) {
                            TodoNameTextField(
                                name = todoViewModel.todoName,
                                onNameChange = todoViewModel::onNameChange,
                                isError = todoViewModel.isRepeatedName || todoViewModel.isInvalidName,
                                modifier = Modifier.fillMaxWidth()
                            ) { keyboardController?.hideSoftwareKeyboard() }
                            if (todoViewModel.isInvalidName)
                                ErrorText(text = stringResource(id = R.string.invalid_name))
                            else if (todoViewModel.isRepeatedName)
                                ErrorText(text = stringResource(id = R.string.name_repeated))
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.todo_editing_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_todo_editing_done -> {
                todoViewModel.onDone(args.todo, findNavController())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}