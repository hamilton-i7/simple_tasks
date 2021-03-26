package com.example.simpletasks.data.todo

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo ORDER BY id DESC")
    fun readAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todo WHERE name LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    fun readTodosByQuery(searchQuery: String): Flow<List<Todo>>

    @Query("SELECT * FROM todo WHERE id LIKE :id")
    fun readTodoById(id: Int): Flow<Todo>

    @Insert
    suspend fun addTodo(todo: Todo)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("DELETE FROM todo")
    suspend fun deleteAllTodos()
}