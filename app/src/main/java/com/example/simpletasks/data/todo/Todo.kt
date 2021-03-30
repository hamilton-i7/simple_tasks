package com.example.simpletasks.data.todo

import androidx.annotation.ColorRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.simpletasks.R
import com.example.simpletasks.data.task.Task
import java.util.*

@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    @ColorRes val colorResource: Int = R.color.default_color,
    val tasks: List<Task> = emptyList(),
    val orderPos: Int
)
