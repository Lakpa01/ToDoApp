package com.example.todo.presentation.uistate

data class AuthUiState(
    val userName: String = "User",
    val profileImageUri: String? = null,
    val isLoggedIn: Boolean = false,
    val isLoginTab: Boolean = true,
    val emailInput: String = "",
    val passwordInput: String = "",
    val confirmPasswordInput: String = "",
    val errorMessage: String? = null
)