package com.example.simpletasks.data.todo

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.simpletasks.R
import com.example.simpletasks.data.task.Task
import kotlinx.parcelize.Parcelize

@Entity(tableName = "todo")
@Parcelize
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
    @ColorRes val colorResource: Int = R.color.default_color,
    val tasks: List<Task> = listOf()
) : Parcelable {
    companion object {
        val Default = Todo(name = "")
    }
}
