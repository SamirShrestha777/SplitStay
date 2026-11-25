package com.softwarica.splitstay.viewmodel.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwarica.splitstay.repository.AuthRepository
import com.softwarica.splitstay.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null, generalError = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, generalError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, generalError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onRegisterClick(onSuccess: (name: String, email: String, password: String) -> Unit) {
        val state = _uiState.value
        if (state.isLoading) return

        val name = state.name.trim()
        val email = state.email.trim()

        val nameError = if (name.isBlank()) "Name is required" else null
        val emailError =
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                "Enter a valid email" else null
        val passwordError =
            if (state.password.length < 6)
                "Password must be at least 6 characters" else null

        if (nameError != null || emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, generalError = null) }

            authRepository.signUp(name, email, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess(name, email, state.password)
                }
                .onFailure { e ->
                    val message =
                        if (e.message?.contains("already", ignoreCase = true) == true)
                            "An account with this email already exists"
                        else
                            "Sign up failed. Check your connection and try again."
                    _uiState.update { it.copy(isLoading = false, generalError = message) }
                }
        }
    }
}
