package com.example.simpletasks.ui.todo

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletasks.R
import com.example.simpletasks.data.label.LabelSource
import com.example.simpletasks.data.settings.Settings
import com.example.simpletasks.data.settings.SettingsViewModel
import com.example.simpletasks.data.task.TaskViewModel
import com.example.simpletasks.data.task.TaskViewModelFactory
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import com.example.simpletasks.util.DragManager
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TodoFragment : Fragment() {

    private val args by navArgs<TodoFragmentArgs>()

    private val todoViewModel by activityViewModels<TodoViewModel>()
    private val settingsViewModel by activityViewModels<SettingsViewModel>()

    private val taskViewModel: TaskViewModel by lazy {
        ViewModelProvider(
            this,
            TaskViewModelFactory(todoViewModel)
        ).get(TaskViewModel::class.java)
    }
    private var todo: Todo? = null
    private val labels = LabelSource.readLabels()

    private lateinit var uncompletedTaskAdapter: UncompletedTaskAdapter
    private lateinit var completedTaskAdapter: CompletedTaskAdapter

    @ExperimentalComposeUiApi
    @ExperimentalFoundationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setHasOptionsMenu(true)

        setContent {
            LocalSoftwareKeyboardController.current?.hideSoftwareKeyboard()
            val settings by settingsViewModel.readSettings().observeAsState(
                initial = Settings()
            )
            val todo by todoViewModel.readTodoById(args.todoId).observeAsState()
            val scrollState = rememberScrollState()

            todo?.let { currentTodo ->
                todoViewModel.onNameChange(currentTodo.name)
                todoViewModel.setInitialLabel(currentTodo.colorResource)
                taskViewModel.setTasks(currentTodo.tasks)
                (requireActivity() as AppCompatActivity).supportActionBar?.title = currentTodo.name

                /** Variable created to control the UI states made with Composables*/
                val tasks by taskViewModel.tasks.observeAsState(initial = currentTodo.tasks)

                uncompletedTaskAdapter = UncompletedTaskAdapter(
                    currentTodo,
                    taskViewModel,
                    findNavController()
                )
                completedTaskAdapter = CompletedTaskAdapter(
                    currentTodo,
                    todoViewModel,
                    taskViewModel,
                    findNavController()
                )
                taskViewModel.tasks.observe(viewLifecycleOwner) {
                    val uncompletedTasks = it.filter { task -> !task.completed }
                    uncompletedTaskAdapter.apply {
                        submitList(uncompletedTasks)
                        updateList(uncompletedTasks)
                    }

                    val completedTasks = it.filter { task -> task.completed }
                    completedTaskAdapter.submitList(completedTasks)
                }

                SimpleTasksTheme {
                    Scaffold(
                        floatingActionButton = {
                            TodoFAB(todoViewModel.labelColor) { goToCreateTaskScreen() }
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .padding(
                                    dimensionResource(id = R.dimen.space_between_8)
                                )
                        ) {
                            if (todoViewModel.isLabelDialogVisible) {
                                LabelDialog(
                                    labels = labels,
                                    onDismissRequest = {
                                        todoViewModel.onLabelDialogStatusChange(false)
                                    },
                                    selectedOption = todoViewModel.labelColor,
                                    onOptionsSelected = {
                                        todoViewModel.onLabelChange(currentTodo, it)
                                        todoViewModel.onLabelDialogStatusChange(false)
                                    }
                                )
                            }

                            AndroidView({ context ->
                                RecyclerView(context).apply {
                                    val dragManager = DragManager(
                                        dragDirs = ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                                        swipeDirs = 0
                                    )
                                    val helper = ItemTouchHelper(dragManager)
                                    helper.attachToRecyclerView(this)
                                    layoutManager = LinearLayoutManager(context)
                                    adapter = uncompletedTaskAdapter
                                    overScrollMode = View.OVER_SCROLL_NEVER
                                }
                            }, modifier = Modifier.fillMaxWidth())
                            if (tasks.any { it.completed }) {
                                if (tasks.any { !it.completed }) {
                                    Divider(
                                        modifier = Modifier.padding(
                                            horizontal = 0.dp,
                                            vertical = dimensionResource(
                                                id = R.dimen.space_between_8
                                            )
                                        )
                                    )
                                }
                                CompletedIndicator(
                                    isExpanded = settings.completedTasksExpanded,
                                    onExpandChange = {
                                        settingsViewModel.onExpandChange(settings)
                                    },
                                    completedAmount =
                                    tasks.filter { it.completed }.size
                                )

                                if (settings.completedTasksExpanded) {
                                    AndroidView({ context ->
                                        RecyclerView(context).apply {
                                            layoutManager = LinearLayoutManager(context)
                                            adapter = completedTaskAdapter
                                            overScrollMode = View.OVER_SCROLL_NEVER
                                        }
                                    }, modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoViewModel.readTodoById(args.todoId).observe(viewLifecycleOwner) { todo = it }
    }

    override fun onStop() {
        super.onStop()
        todoViewModel.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        todo = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.todo_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_rename_list -> {
                goToEditingScreen()
                true
            }
            R.id.action_change_label_color -> {
                todoViewModel.onLabelDialogStatusChange(showDialog = true)
                true
            }
            R.id.action_delete_completed_tasks -> {
                todo?.let {
                    taskViewModel.onCompletedTasksDelete(it)
                    true
                } ?: false
            }
            R.id.action_delete_list -> {
                todo?.let {
                    todoViewModel.deleteTodo(it)
                    goToHomeScreen()
                    true
                } ?: false
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goToHomeScreen() {
        val action = TodoFragmentDirections.actionTodoFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun goToEditingScreen() {
        todo?.let {
            val action = TodoFragmentDirections.actionTodoFragmentToTodoEditFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun goToCreateTaskScreen() {
        todo?.let {
            val action = TodoFragmentDirections.actionTodoFragmentToCreateTaskFragment(it)
            findNavController().navigate(action)
        }
    }
}