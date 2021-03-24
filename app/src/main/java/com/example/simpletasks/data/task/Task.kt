package com.example.simpletasks.data.task

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val completed: Boolean = false
) : Parcelable
