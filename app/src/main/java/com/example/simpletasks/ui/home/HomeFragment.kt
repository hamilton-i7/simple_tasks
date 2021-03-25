package com.example.simpletasks.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.simpletasks.R
import com.example.simpletasks.data.label.LabelSource
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.components.NewListDialog
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import com.example.simpletasks.util.onQueryTextChanged
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalCoroutinesApi
class HomeFragment : Fragment() {

    private val todoViewModel by activityViewModels<TodoViewModel>()
    private val labels = LabelSource.readLabels()

    private lateinit var searchView: SearchView
    private lateinit var todoCardAdapter: TodoCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        todoCardAdapter = TodoCardAdapter(findNavController())

        return ComposeView(requireContext()).apply {
            setContent {
                val todos by todoViewModel.readTodosByQuery(todoViewModel.searchQuery.value)
                var isExpanded by rememberSaveable { mutableStateOf(false) }

                SimpleTasksTheme {
                    Scaffold(
                        floatingActionButton = {
                            HomeFAB { todoViewModel.onDialogStatusChange(true) }
                        }
                    ) {
                        Box(
                            modifier = Modifier.padding(
                                dimensionResource(id = R.dimen.space_between_8)
                            )
                        ) {
                            AndroidView({ context ->
                                RecyclerView(context).apply {
                                    id = R.id.todo_card_recyclerview
                                    layoutManager = StaggeredGridLayoutManager(
                                        2, StaggeredGridLayoutManager.VERTICAL
                                    )
                                    adapter = todoCardAdapter
                                }
                            }, modifier = Modifier.fillMaxSize())

                            if (todoViewModel.isDialogVisible) {
                                NewListDialog(
                                    todoViewModel = todoViewModel,
                                    labels = labels,
                                    isExpanded = isExpanded,
                                    onExpandChange = { isExpanded = !isExpanded },
                                    onDismissRequest = {
                                        todoViewModel.onDialogStatusChange(false)
                                    },
                                    selectedOption = todoViewModel.newTodoColor,
                                    onOptionsSelected = todoViewModel::onNewColorChange,
                                    onCancel = {
                                        todoViewModel.onCancelDialog()
                                        isExpanded = false
                                    },
                                    onDone = { todoViewModel.onDone(todos, findNavController()) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoViewModel.todos.asLiveData().observe(viewLifecycleOwner) { todos ->
            todoCardAdapter.submitList(todos)
        }
    }

    override fun onStop() {
        super.onStop()
        todoViewModel.onQueryChange("")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)

        searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_list)

        val pendingQuery = todoViewModel.searchQuery.value
        if (pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryTextChanged { query ->
            todoViewModel.onQueryChange(query)
        }
    }
}