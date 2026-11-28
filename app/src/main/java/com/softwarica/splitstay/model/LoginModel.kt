package com.softwarica.splitstay.model

data class LoginModel(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,

    val emailError: String? = null,
    val passwordError: String? = null,

    val authError: String? = null,

    val isLoading: Boolean = false,

    val loginSuccess: Boolean = false
) {
    val isEmailError: Boolean get() = emailError != null || authError != null
    val isPasswordError: Boolean get() = passwordError != null || authError != null
}