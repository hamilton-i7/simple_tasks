package com.example.simpletasks.ui.home

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import com.example.simpletasks.util.createTodoRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TodoCardAdapter(
    private val navController: NavController,
    private val todoViewModel: TodoViewModel
    ) :
        ListAdapter<Todo, TodoCardAdapter.TodoCardViewHolder>(DiffCallback()) {

    inner class TodoCardViewHolder(private val view: ComposeView) : RecyclerView.ViewHolder(view) {

        fun bind(todo: Todo) {
            view.setContent {
                SimpleTasksTheme {
                    TodoCard(todo) {
                        val route = createTodoRoute(todo.id)
                        todoViewModel.onTodoSelect(route)
                        navController.navigate(route)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoCardViewHolder =
        TodoCardViewHolder(ComposeView(parent.context))

    override fun onBindViewHolder(holder: TodoCardViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean =
                oldItem == newItem
    }
}