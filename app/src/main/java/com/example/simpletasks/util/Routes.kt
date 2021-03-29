package com.example.simpletasks.util

import com.example.simpletasks.ui.EDIT_TASK_ROUTE_PREFIX
import com.example.simpletasks.ui.EDIT_TODO_ROUTE_PREFIX
import com.example.simpletasks.ui.NEW_TASK_ROUTE_PREFIX
import com.example.simpletasks.ui.TODO_ROUTE_PREFIX

fun createTodoRoute(todoId: String) = "$TODO_ROUTE_PREFIX/$todoId"

fun createTodoEditRoute(todoId: String) = "$EDIT_TODO_ROUTE_PREFIX/$todoId"

fun createNewTaskRoute(todoId: String) = "$NEW_TASK_ROUTE_PREFIX/$todoId"

fun createTaskEditRoute(todoId: String, taskId: String) = "$EDIT_TASK_ROUTE_PREFIX/$todoId/$taskId"