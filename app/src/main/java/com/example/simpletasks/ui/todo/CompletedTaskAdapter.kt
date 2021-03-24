package com.example.simpletasks.ui.todo

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletasks.data.task.Task
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.ui.theme.SimpleTasksTheme

class CompletedTaskAdapter(private val todo: Todo) :
ListAdapter<Task, CompletedTaskAdapter.CompletedTaskViewHolder>(DiffCallback()) {

    inner class CompletedTaskViewHolder(private val view: ComposeView) :
        RecyclerView.ViewHolder(view) {

        fun bind(task: Task) {
            view.setContent {
                SimpleTasksTheme {
                    CompletedTaskRow(
                        name = task.name,
                        iconColor = todo.colorResource
                    ) {}
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTaskViewHolder =
        CompletedTaskViewHolder(ComposeView(parent.context))

    override fun onBindViewHolder(holder: CompletedTaskViewHolder, position: Int) {
        val currentTask = getItem(position)
        holder.bind(currentTask)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem
    }
}