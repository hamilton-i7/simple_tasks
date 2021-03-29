package com.example.simpletasks.util

import androidx.room.TypeConverter
import com.example.simpletasks.data.task.Task
import com.google.gson.Gson

class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun fromJson(value: String): List<Task> =
            Gson().fromJson(value, Array<Task>::class.java).asList()

        @TypeConverter
        @JvmStatic
        fun toJson(value: List<Task>): String = Gson().toJson(value)

    }
}