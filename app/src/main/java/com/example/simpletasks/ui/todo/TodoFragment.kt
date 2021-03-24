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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
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
import com.example.simpletasks.data.settings.SettingsViewModel
import com.example.simpletasks.data.task.TaskViewModel
import com.example.simpletasks.data.task.TaskViewModelFactory
import com.example.simpletasks.data.todo.TodoScreenViewModel
import com.example.simpletasks.data.todo.TodoScreenViewModelFactory
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import com.example.simpletasks.util.DragManager
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TodoFragment : Fragment() {

    private val args by navArgs<TodoFragmentArgs>()

    private val todoViewModel by activityViewModels<TodoViewModel>()
    private val settingsViewModel by activityViewModels<SettingsViewModel>()

    private val todoScreenViewModel: TodoScreenViewModel by lazy {
        ViewModelProvider(
            this,
            TodoScreenViewModelFactory(args.todo, todoViewModel)
        ).get(TodoScreenViewModel::class.java)
    }
    private val taskViewModel: TaskViewModel by lazy {
        ViewModelProvider(
            this,
            TaskViewModelFactory(args.todo, todoViewModel)
        ).get(TaskViewModel::class.java)
    }

    private val labels = LabelSource.readLabels()

    private lateinit var uncompletedTaskAdapter: UncompletedTaskAdapter
    private lateinit var completedTaskAdapter: CompletedTaskAdapter

    @ExperimentalFoundationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        uncompletedTaskAdapter = UncompletedTaskAdapter(todoViewModel, args.todo)
        completedTaskAdapter = CompletedTaskAdapter(todoScreenViewModel)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = args.todo.name
        setHasOptionsMenu(true)

        return ComposeView(requireContext()).apply {
            setContent {
                val settings by settingsViewModel.readSettings()
                val scrollState = rememberScrollState()
                val uncompletedTasks by taskViewModel.uncompletedTasks.observeAsState(
                    initial = listOf()
                )
                val completedTasks by taskViewModel.completedTasks.observeAsState(
                    initial = listOf()
                )
                val (labelColor, setLabelColor) =
                    rememberSaveable { mutableStateOf(args.todo.colorResource) }

                SimpleTasksTheme {
                    Scaffold(
                        floatingActionButton = {
                            TodoFAB(todoScreenViewModel.labelColor) { }
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
                                        setLabelColor(args.todo.colorResource)
                                    },
                                    selectedOption = labelColor,
                                    onOptionsSelected = {
                                        setLabelColor(it)
                                        todoScreenViewModel.onLabelChange(it)
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

                            if (completedTasks.isNotEmpty()) {
                                if (uncompletedTasks.isNotEmpty()) {
                                    Divider(
                                        modifier = Modifier.padding(
                                            horizontal = 0.dp,
                                            vertical = dimensionResource(id = R.dimen.space_between_8)
                                        )
                                    )
                                }
                                CompletedIndicator(
                                    isExpanded = settingsViewModel.isExpanded,
                                    onExpandChange = { settingsViewModel.onExpandChange(settings) },
                                    completedAmount = completedTasks.size
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

        taskViewModel.uncompletedTasks.observe(viewLifecycleOwner) {
            uncompletedTaskAdapter.apply {
                submitList(it)
                updateList(it)
            }

        }
        taskViewModel.completedTasks.observe(viewLifecycleOwner) {
            completedTaskAdapter.submitList(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.todo_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_rename_list -> {
                true
            }
            R.id.action_change_label_color -> {
                todoViewModel.onLabelDialogStatusChange(showDialog = true)
                true
            }
            R.id.action_delete_completed_tasks -> {
                taskViewModel.onCompletedTasksDelete(args.todo)
                true
            }
            R.id.action_delete_list -> {
                goToHomeScreen()
                todoViewModel.deleteTodo(args.todo)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goToHomeScreen() {
        findNavController().navigateUp()
    }
}