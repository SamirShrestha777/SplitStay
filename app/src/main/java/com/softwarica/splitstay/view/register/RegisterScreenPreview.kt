package com.softwarica.splitstay.view.register

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.softwarica.splitstay.viewmodel.register.RegisterUiState

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenEmptyPreview() {
    RegisterScreen(uiState = RegisterUiState())
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterScreenErrorPreview() {
    RegisterScreen(
        uiState = RegisterUiState(
            name = "",
            email = "invalid-email",
            password = "123",
            nameError = "Name is required",
            emailError = "Enter a valid email",
            passwordError = "Password must be at least 6 characters"
        )
    )
}
