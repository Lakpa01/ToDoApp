package com.example.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.todo.presentation.uistate.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /*
     * Email validation
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS
            .matcher(email.trim())
            .matches()
    }

    /*
     * Password validation
     *
     * Requirements:
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one number
     */
    private fun isValidPassword(password: String): Boolean {

        if (password.length < 8) {
            return false
        }

        if (!password.any { it.isUpperCase() }) {
            return false
        }

        if (!password.any { it.isLowerCase() }) {
            return false
        }

        if (!password.any { it.isDigit() }) {
            return false
        }

        return true
    }

    /*
     * Return password error message
     */
    private fun getPasswordError(password: String): String? {

        if (password.length < 5) {
            return "Password must be at least 5 characters"
        }



        if (!password.any { it.isLowerCase() }) {
            return "Password must contain at least one lowercase letter"
        }

        if (!password.any { it.isDigit() }) {
            return "Password must contain at least one number"
        }

        return null
    }

    /*
     * Tab changed
     */
    fun onTabChanged(isLogin: Boolean) {

        _uiState.update {
            it.copy(
                isLoginTab = isLogin,
                errorMessage = null
            )
        }
    }

    /*
     * Email changed
     */
    fun onEmailChanged(email: String) {

        _uiState.update {
            it.copy(
                emailInput = email,
                errorMessage = null
            )
        }
    }

    /*
     * Password changed
     */
    fun onPasswordChanged(password: String) {

        _uiState.update {
            it.copy(
                passwordInput = password,
                errorMessage = null
            )
        }
    }

    /*
     * Confirm password changed
     */
    fun onConfirmPasswordChanged(password: String) {

        _uiState.update {
            it.copy(
                confirmPasswordInput = password,
                errorMessage = null
            )
        }
    }

    /*
     * Update profile
     */
    fun updateProfile(
        newName: String,
        newImageUri: String?
    ) {

        _uiState.update { currentState ->

            currentState.copy(
                userName = newName,
                profileImageUri = newImageUri
            )
        }
    }

    /*
     * LOGIN
     */
    fun login() {

        val state = _uiState.value

        val email = state.emailInput.trim()
        val password = state.passwordInput

        /*
         * Check empty fields
         */
        if (email.isBlank() || password.isBlank()) {

            _uiState.update {
                it.copy(
                    errorMessage = "Email and password cannot be empty"
                )
            }

            return
        }

        /*
         * Check email format
         */
        if (!isValidEmail(email)) {

            _uiState.update {
                it.copy(
                    errorMessage =
                        "Please enter a valid email address"
                )
            }

            return
        }

        /*
         * Check password
         */
        val passwordError =
            getPasswordError(password)

        if (passwordError != null) {

            _uiState.update {
                it.copy(
                    errorMessage = passwordError
                )
            }

            return
        }

        /*
         * Login successful
         */
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                errorMessage = null
            )
        }
    }

    /*
     * SIGN UP
     */
    fun signUp() {

        val state = _uiState.value

        val email = state.emailInput.trim()
        val password = state.passwordInput
        val confirmPassword =
            state.confirmPasswordInput

        /*
         * Check empty fields
         */
        if (
            email.isBlank() ||
            password.isBlank() ||
            confirmPassword.isBlank()
        ) {

            _uiState.update {
                it.copy(
                    errorMessage =
                        "All fields are required"
                )
            }

            return
        }

        /*
         * Check email format
         */
        if (!isValidEmail(email)) {

            _uiState.update {
                it.copy(
                    errorMessage =
                        "Please enter a valid email address"
                )
            }

            return
        }

        /*
         * Check password requirements
         */
        val passwordError =
            getPasswordError(password)

        if (passwordError != null) {

            _uiState.update {
                it.copy(
                    errorMessage = passwordError
                )
            }

            return
        }

        /*
         * Check confirm password
         */
        if (password != confirmPassword) {

            _uiState.update {
                it.copy(
                    errorMessage =
                        "Passwords do not match"
                )
            }

            return
        }

        /*
         * Signup successful
         */
        _uiState.update {
            it.copy(
                isLoggedIn = true,
                errorMessage = null
            )
        }
    }

    /*
     * LOGOUT
     */
    fun logout() {

        _uiState.update { currentState ->

            currentState.copy(
                isLoggedIn = false,
                profileImageUri = null
            )
        }
    }
}