package com.example.simpletasks.util

import com.example.simpletasks.data.todo.Todo
import com.example.simpletasks.ui.TODO_EDIT_ROUTE_PREFIX
import com.example.simpletasks.ui.TODO_ROUTE_PREFIX

fun createTodoRoute(todos: List<Todo>) =
    TODO_ROUTE_PREFIX + todos[0].id?.plus(1)

fun createTodoRoute(todoId: String) = TODO_ROUTE_PREFIX + todoId

fun createTodoEditRoute(todoId: Int) = TODO_EDIT_ROUTE_PREFIX + todoId