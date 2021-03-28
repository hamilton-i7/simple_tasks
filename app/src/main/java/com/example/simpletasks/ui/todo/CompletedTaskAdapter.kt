package com.example.simpletasks.ui.todo

import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpletasks.R
import com.example.simpletasks.data.task.Task
import com.example.simpletasks.data.task.TaskViewModel
import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.data.todo.TodoViewModel
import com.example.simpletasks.ui.theme.SimpleTasksTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CompletedTaskAdapter(
    private val todo: Todo,
    private val todoViewModel: TodoViewModel,
    private val taskViewModel: TaskViewModel,
    private val navController: NavController
) :
    ListAdapter<Task, CompletedTaskAdapter.CompletedTaskViewHolder>(DiffCallback()) {

    inner class CompletedTaskViewHolder(private val view: ComposeView) :
        RecyclerView.ViewHolder(view) {

        fun bind(task: Task) {
            view.setContent {
                SimpleTasksTheme {
                    Column {
                        CompletedTaskRow(
                            name = task.name,
                            details = task.details,
                            iconColor = todoViewModel.labelColor,
                            onTaskUncheck = { taskViewModel.onTaskStateChange(task, todo) },
                            onNameClick = { goToEditTaskScreen(task) }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedTaskViewHolder =
        CompletedTaskViewHolder(ComposeView(parent.context))

    override fun onBindViewHolder(holder: CompletedTaskViewHolder, position: Int) =
        holder.bind(getItem(position))

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