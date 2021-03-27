package com.example.simpletasks.ui.task.edit

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.simpletasks.R
import com.example.simpletasks.data.task.TaskViewModel
import com.example.simpletasks.data.task.TaskViewModelFactory
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class EditTaskFragment : Fragment() {

    private val args by navArgs<EditTaskFragmentArgs>()

    private val todoViewModel by activityViewModels<TodoViewModel>()
    private val taskViewModel: TaskViewModel by lazy {
        ViewModelProvider(
            this,
            TaskViewModelFactory(todoViewModel)
        ).get(TaskViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return ComposeView(requireContext()).apply {
            setContent {
                val scrollState = rememberScrollState()
                val initialListButtonName by todoViewModel.todoName.observeAsState(initial = "")
                var listButtonExpanded by rememberSaveable { mutableStateOf(false) }
                val todos by todoViewModel.readAllTodos().collectAsState(initial = emptyList())

                SimpleTasksTheme {
                    Scaffold(
                        floatingActionButton = {
                            MarkButton(taskCompleted = args.task.completed) {
                                taskViewModel.onTaskStateChange(args.task, args.todo)
                                goToTodoScreen()
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .padding(
                                    dimensionResource(id = R.dimen.space_between_16)
                                )
                        ) {
                            Box {
                                if (args.task.completed)
                                    ListButton(text = initialListButtonName, enabled = false)
                                else
                                    ListButton(
                                        text = initialListButtonName,
                                        enabled = true,
                                        expanded = listButtonExpanded,
                                        onClick = { listButtonExpanded = true }
                                    )
                                if (listButtonExpanded) {
                                    TodoDropdownMenu(
                                        expanded = listButtonExpanded,
                                        todos = todos,
                                        onDismiss = { listButtonExpanded = false },
                                        onClick = {}
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.padding(
                                dimensionResource(id = R.dimen.space_between_10)
                            ))
                            TaskTextField(
                                name = taskViewModel.taskName,
                                onNameChange = taskViewModel::onTaskNameChange,
                                readOnly = args.task.completed,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        dimensionResource(id = R.dimen.space_between_8)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskViewModel.onTaskNameChange(args.task.name)
    }

    override fun onStop() {
        super.onStop()
        taskViewModel.onTaskEdit(args.task, args.todo)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_task_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_task -> {
                taskViewModel.onTaskDelete(args.task, args.todo)
                goToTodoScreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goToTodoScreen() {
        findNavController().navigateUp()
    }
}