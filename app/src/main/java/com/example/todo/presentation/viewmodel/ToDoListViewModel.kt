package com.example.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.todo.domain.model.ToDo
import com.example.todo.presentation.uistate.Screen
import com.example.todo.presentation.uistate.ToDoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TodoListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ToDoUiState())
    val uiState: StateFlow<ToDoUiState> = _uiState.asStateFlow()

    fun onInputChanged(newInput: String) {
        _uiState.update { it.copy(input = newInput) }
    }

    fun selectScreen(screen: Screen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun addOrUpdateTodo(dueDateMillis: Long? = null) {
        val currentInput = _uiState.value.input.trim()
        if (currentInput.isEmpty()) return

        val currentEditing = _uiState.value.editingTodo
        val updatedItems = _uiState.value.items.toMutableList()

        if (currentEditing != null) {
            val index = updatedItems.indexOfFirst { it.id == currentEditing.id }
            if (index != -1) {
                updatedItems[index] = currentEditing.copy(
                    title = currentInput,
                    dueDateMillis = dueDateMillis ?: currentEditing.dueDateMillis
                )
            }
        } else {
            updatedItems.add(
                ToDo(
                    title = currentInput,
                    dueDateMillis = dueDateMillis
                )
            )
        }

        // --- PUT YOUR SORTING CODE HERE ---
        val currentTime = System.currentTimeMillis()
        val sortedItems = updatedItems.sortedBy { task ->
            // Tasks with closest upcoming deadlines (shortest remaining time) appear first
            val due = task.dueDateMillis ?: Long.MAX_VALUE
            due - currentTime
        }

        _uiState.update {
            it.copy(
                items = sortedItems,
                input = "",
                editingTodo = null
            )
        }
    }

    fun toggleTodo(todo: ToDo) {
        val updatedItems = _uiState.value.items.map {
            if (it.id == todo.id) it.copy(isDone = !it.isDone) else it
        }

        val currentTime = System.currentTimeMillis()
        val sortedItems = updatedItems.sortedBy { task ->
            val due = task.dueDateMillis ?: Long.MAX_VALUE
            due - currentTime
        }

        _uiState.update { it.copy(items = sortedItems) }
    }

    fun startEditing(todo: ToDo) {
        _uiState.update {
            it.copy(
                editingTodo = todo,
                input = todo.title
            )
        }
    }

    fun deleteTodo(todo: ToDo) {
        val updatedItems = _uiState.value.items.filterNot { it.id == todo.id }
        _uiState.update { it.copy(items = updatedItems) }
    }
}