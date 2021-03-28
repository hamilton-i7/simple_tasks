package com.example.simpletasks.ui.task

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.simpletasks.R
import com.example.simpletasks.data.task.TaskViewModel
import com.example.simpletasks.data.task.TaskViewModelFactory
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.components.SimpleTextField
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CreateTaskFragment : Fragment() {

    private val args by navArgs<CreateTaskFragmentArgs>()

    private val todoViewModel by activityViewModels<TodoViewModel>()
    private val taskViewModel by activityViewModels<TaskViewModel> {
        TaskViewModelFactory(todoViewModel)
    }

    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setHasOptionsMenu(true)

        setContent {
            val focusManager = LocalFocusManager.current
            val newTaskName by taskViewModel.newTaskName.observeAsState(initial = "")

            SimpleTasksTheme {
                Surface {
                    Box(
                        modifier = Modifier.padding(
                            dimensionResource(id = R.dimen.space_between_16)
                        )
                    ) {
                        SimpleTextField(
                            name = newTaskName,
                            onNameChange = taskViewModel::onNewTaskNameChange,
                            label = stringResource(id = R.string.task_name),
                            modifier = Modifier.fillMaxWidth()
                        ) { focusManager.clearFocus() }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        taskViewModel.resetNewTaskField()
        todoViewModel.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.editing_menu, menu)

        val doneButton = menu.findItem(R.id.action_done)

        taskViewModel.newTaskName.observe(viewLifecycleOwner) {
            doneButton.isEnabled = it.trim().isNotEmpty()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                taskViewModel.onTaskCreate(args.todo)
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}