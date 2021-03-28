package com.example.simpletasks.ui.home

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TodoCardAdapter(private val navController: NavController) :
        ListAdapter<Todo, TodoCardAdapter.TodoCardViewHolder>(DiffCallback()) {

    inner class TodoCardViewHolder(private val view: ComposeView) : RecyclerView.ViewHolder(view) {

        fun bind(todo: Todo) {
            view.setContent {
                SimpleTasksTheme {
                    TodoCard(todo) {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToTodoFragment(todo.id)
                        navController.navigate(action)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoCardViewHolder =
        TodoCardViewHolder(ComposeView(parent.context))

    override fun onBindViewHolder(holder: TodoCardViewHolder, position: Int) {
        val currentTodo = getItem(position)
        holder.bind(currentTodo)
    }

    class DiffCallback : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean =
                oldItem == newItem
    }
}