package com.example.simpletasks.ui.todo

import android.view.ViewGroup
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletasks.data.task.Task
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
class UncompletedTaskAdapter(
    private val todoViewModel: TodoViewModel,
    private val todo: Todo
):
    ListAdapter<Task, UncompletedTaskAdapter.UncompletedTaskViewHolder>(DiffCallback()) {

    private var tasks = mutableListOf<Task>()

    class UncompletedTaskViewHolder(private val view: ComposeView) :
        RecyclerView.ViewHolder(view) {

        fun bind(task: Task) {
            view.setContent {
                SimpleTasksTheme {
                    Surface {
                        UncompletedTaskRow(name = task.name) {}
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UncompletedTaskViewHolder =
        UncompletedTaskViewHolder(ComposeView(parent.context))

    override fun onBindViewHolder(holder: UncompletedTaskViewHolder, position: Int) {
        val currentTask = getItem(position)
        holder.bind(currentTask)
    }

    fun swapItems(startPosition: Int, endPosition: Int) {
        Collections.swap(tasks, startPosition, endPosition)
        todoViewModel.onTasksSwap(todo, tasks)
        notifyItemMoved(startPosition, endPosition)
    }

    fun updateList(tasks: List<Task>) {
        this.tasks = tasks as MutableList<Task>
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem
    }
}