package com.example.simpletasks.util

import androidx.room.TypeConverter
import com.example.simpletasks.data.settings.Settings
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

        @TypeConverter
        @JvmStatic
        fun toPreference(value: String): Settings =
            Gson().fromJson(value, Settings::class.java)

        @TypeConverter
        @JvmStatic
        fun fromPreference(value: Settings): String = Gson().toJson(value)
    }
}