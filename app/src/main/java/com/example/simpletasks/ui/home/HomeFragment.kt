package com.example.simpletasks.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.simpletasks.R
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import com.example.simpletasks.util.onQueryTextChanged
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class HomeFragment : Fragment() {

    private val todoViewModel by activityViewModels<TodoViewModel>()

    private lateinit var searchView: SearchView
    private lateinit var todoCardAdapter: TodoCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        todoCardAdapter = TodoCardAdapter(findNavController())
        setHasOptionsMenu(true)
        return ComposeView(requireContext()).apply {
            setContent {
                SimpleTasksTheme {
                    Scaffold(
                        floatingActionButton = {
                            HomeFAB { }
                        }
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
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoViewModel.todos.observe(viewLifecycleOwner) { todos ->
            todoCardAdapter.submitList(todos)
        }
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