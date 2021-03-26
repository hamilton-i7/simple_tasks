package com.example.simpletasks.ui.task.edit

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
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
            TaskViewModelFactory(args.todo, todoViewModel)
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
                SimpleTasksTheme {
                    Scaffold(
                        floatingActionButton = {
                            MarkButton(isCompleted = false) {}
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .padding(
                                    dimensionResource(id = R.dimen.space_between_16)
                                )
                        ) {
                            ListButton(name = args.todo.name, isExpanded = false) {}
                            Spacer(modifier = Modifier.padding(
                                dimensionResource(id = R.dimen.space_between_10)
                            ))
                            TaskTextField(
                                name = taskViewModel.taskName,
                                onNameChange = taskViewModel::onTaskNameChange,
                                isCompleted = false,
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.task_editing_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_task -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}