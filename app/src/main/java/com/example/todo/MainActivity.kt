package com.example.todo

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.todo.presentation.compose.AuthScreen
import com.example.todo.presentation.compose.ToDoListScreen
import com.example.todo.presentation.viewmodel.AuthViewModel
import com.example.todo.presentation.viewmodel.TodoListViewModel
import com.example.todo.ui.theme.ToDoTheme

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val todoListViewModel: TodoListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep the bottom bar fixed when the keyboard appears
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )

        setContent {
            ToDoTheme {

                val authState by authViewModel.uiState.collectAsState()

                if (authState.isLoggedIn) {

                    ToDoListScreen(
                        viewModel = todoListViewModel,
                        authViewModel = authViewModel
                    )

                } else {

                    AuthScreen(
                        viewModel = authViewModel
                    )
                }
            }
        }
    }
}