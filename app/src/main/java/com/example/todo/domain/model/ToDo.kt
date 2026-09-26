package com.example.todo.domain.model

import java.util.UUID

data class ToDo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val isDone: Boolean = false,
    val dueDateMillis: Long? = null // Target completion timestamp in milliseconds
)