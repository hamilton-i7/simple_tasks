package com.example.simpletasks.ui

const val TODO_ARG = "todoId"
const val TASK_ARG = "taskId"
const val TODO_ROUTE_PREFIX = "todo"
const val EDIT_TODO_ROUTE_PREFIX = "edit_todo"
const val EDIT_TASK_ROUTE_PREFIX = "edit_task"

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Todo : Screen("$TODO_ROUTE_PREFIX/{$TODO_ARG}")
    object EditTodo: Screen("$EDIT_TODO_ROUTE_PREFIX/{$TODO_ARG}")
    object EditTask: Screen("$EDIT_TASK_ROUTE_PREFIX/{$TODO_ARG}/{$TASK_ARG}")
}