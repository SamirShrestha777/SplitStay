package com.softwarica.splitstay.viewmodel.login

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

class LoginViewModel(

    private val repository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {

        _uiState.update { it.copy(email = value, emailError = null, authError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, authError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun onSignInClick() {
        val current = _uiState.value
        if (current.isLoading) return

        val email = current.email.trim()
        val password = current.password

        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)

        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(emailError = emailError, passwordError = passwordError, authError = null)
            }
            return
        }

        _uiState.update {
            it.copy(isLoading = true, emailError = null, passwordError = null, authError = null)
        }

        viewModelScope.launch {
            repository.signIn(email, password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, authError = friendlyMessage(error))
                    }
                }
        }
    }


    fun onLoginSuccessHandled() {
        _uiState.update { it.copy(loginSuccess = false) }
    }

    private fun validateEmail(email: String): String? = when {
        email.isEmpty() -> "Email is required."
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address."
        else -> null
    }

    private fun validatePassword(password: String): String? = when {
        password.isEmpty() -> "Password is required."
        password.length < 6 -> "Password must be at least 6 characters."
        else -> null
    }


    private fun friendlyMessage(error: Throwable): String {
        val msg = error.message.orEmpty().lowercase()
        val type = error::class.simpleName.orEmpty().lowercase()
        return when {
            "unresolvedaddress" in type || "unknownhost" in type ||
                    "timeout" in type || "connect" in type ->
                "Can't reach the server. Check your internet connection."
            "email not confirmed" in msg || "email_not_confirmed" in msg ->
                "Please confirm your email first."
            "invalid login credentials" in msg || "invalid_credentials" in msg ->
                "Incorrect email or password."
            "unable to resolve host" in msg || "timeout" in msg ||
                    "failed to connect" in msg || "network" in msg ->
                "Can't reach the server. Check your internet connection."
            else -> "Incorrect email or password."
        }
    }
}