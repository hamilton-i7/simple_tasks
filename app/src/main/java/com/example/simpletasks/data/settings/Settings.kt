package com.example.simpletasks.data.settings

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val completedTasksExpanded: Boolean = true
) {
    companion object {
        val Default = Settings()
    }
}
