package com.softwarica.splitstay.viewmodel.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onRegisterClick(onValid: (name: String, email: String, password: String) -> Unit) {
        val current = _uiState.value

        val nameError = if (current.name.isBlank()) "Name is required" else null
        val emailError = when {
            current.email.isBlank() -> "Email is required"
            !Patterns.EMAIL_ADDRESS.matcher(current.email).matches() -> "Enter a valid email"
            else -> null
        }
        val passwordError = when {
            current.password.isBlank() -> "Password is required"
            current.password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        _uiState.update {
            it.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError
            )
        }

        if (nameError == null && emailError == null && passwordError == null) {
            onValid(current.name, current.email, current.password)
        }
    }
}
