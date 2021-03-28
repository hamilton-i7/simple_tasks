package com.example.simpletasks.ui

const val TODO_ARG = "todoId"
const val TODO_ROUTE_PREFIX = "todo/"
const val TODO_EDIT_ROUTE_PREFIX = "rename_todo/"

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Todo : Screen("$TODO_ROUTE_PREFIX{$TODO_ARG}")
    object TodoEdit: Screen("$TODO_EDIT_ROUTE_PREFIX{$TODO_ARG}")
}