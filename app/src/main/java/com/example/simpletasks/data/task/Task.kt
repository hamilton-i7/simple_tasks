package com.example.simpletasks.data.task

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val details: String? = null,
    val completed: Boolean = false
)
