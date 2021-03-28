package com.example.simpletasks.ui.todo

import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletasks.R
import com.example.simpletasks.data.task.Task
import com.example.simpletasks.data.task.TaskViewModel
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import com.example.simpletasks.util.createTaskEditRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
class UncompletedTaskAdapter(
    private val todo: Todo,
    private val taskViewModel: TaskViewModel,
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
                        Column {
                            UncompletedTaskRow(
                                name = task.name,
                                details = task.details,
                                onTaskComplete = {
                                    taskViewModel.onTaskStateChange(task, todo)
                                },
                                onNameClick = { goToEditTaskScreen(task.id) }
                            )
                            if (!task.details.isNullOrEmpty()) {
                                Spacer(
                                    modifier = Modifier.padding(
                                        dimensionResource(id = R.dimen.space_between_2)
                                    )
                                )
                            }
                        }
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

    private fun goToEditTaskScreen(taskId: String) {
        val route = createTaskEditRoute(todo.id, taskId)
        navController.navigate(route)
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem
    }
}