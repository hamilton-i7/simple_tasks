package com.example.simpletasks.data.todo

import kotlinx.coroutines.flow.Flow

class TodoRepo(private val todoDao: TodoDao) {

    fun readAllTodos(): Flow<List<Todo>> = todoDao.readAllTodos()

    fun readTodosByQuery(searchQuery: String): Flow<List<Todo>> =
        todoDao.readTodosByQuery(searchQuery)

    fun readTodoById(id: String): Flow<Todo> = todoDao.readTodoById(id)

    suspend fun addTodo(todo: Todo) = todoDao.addTodo(todo)

    suspend fun updateTodo(todo: Todo) = todoDao.updateTodo(todo)


    suspend fun deleteTodo(todo: Todo) = todoDao.deleteTodo(todo)

    suspend fun deleteAllTodos() = todoDao.deleteAllTodos()
}