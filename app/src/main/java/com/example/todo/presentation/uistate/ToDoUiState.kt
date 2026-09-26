package com.example.todo.presentation.uistate

import com.example.todo.domain.model.ToDo

enum class Screen {
    HOME, COMPLETED
}

data class ToDoUiState(
    val items: List<ToDo> = listOf(
        ToDo(id = "1", title = "Buy groceries", isDone = false),
        ToDo(id = "2", title = "Finish Kotlin homework", isDone = true),
        ToDo(id = "3", title = "Walk the dog", isDone = false)
    ),
    val input: String = "",
    val editingTodo: ToDo? = null,
    val currentScreen: Screen = Screen.HOME
)