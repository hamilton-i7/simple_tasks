package com.example.simpletasks.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.simpletasks.data.preference.Preference
import java.util.*

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val preferences: Preference
)
