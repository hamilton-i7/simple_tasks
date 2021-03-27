package com.example.simpletasks.ui.todo

import android.view.ViewGroup
import androidx.compose.material.Surface
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletasks.data.task.Task
import com.example.simpletasks.data.task.TaskViewModel
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
class UncompletedTaskAdapter(
    private val taskViewModel: TaskViewModel,
    private val todo: Todo,
    private val navController: NavController
) :
    ListAdapter<Task, UncompletedTaskAdapter.UncompletedTaskViewHolder>(DiffCallback()) {

    private var tasks = mutableListOf<Task>()

    inner class UncompletedTaskViewHolder(private val view: ComposeView) :
        RecyclerView.ViewHolder(view) {

        fun bind(task: Task) {
            view.setContent {
                SimpleTasksTheme {
                    Surface {
                        UncompletedTaskRow(
                            name = task.name,
                            onTaskComplete = {
                                taskViewModel.onTaskStateChange(task, todo)
                            },
                            onNameClick = { goToEditTaskScreen(task) }
                        )
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UncompletedTaskViewHolder =
        UncompletedTaskViewHolder(ComposeView(parent.context))

    override fun onBindViewHolder(holder: UncompletedTaskViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapItems(startPosition: Int, endPosition: Int) {
        Collections.swap(tasks, startPosition, endPosition)
        notifyItemMoved(startPosition, endPosition)
        taskViewModel.onTasksSwap(tasks, todo)
    }

    fun updateList(tasks: List<Task>) {
        this.tasks = tasks as MutableList<Task>
    }

    private fun goToEditTaskScreen(task: Task) {
        val action = TodoFragmentDirections.actionTodoFragmentToTaskEditFragment(todo, task)
        navController.navigate(action)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem
    }
}