package com.softwarica.splitstay.viewmodel.verifyemail

/**
 * UI state for the Verify Email screen.
 */
data class VerifyEmailUiState(
    val email: String = "",
    val code: String = "",
    val isVerifying: Boolean = false,
    val isResending: Boolean = false,
    val resendSecondsLeft: Int = 0,
    val errorMessage: String? = null,
    val resendMessage: String? = null,
    val isVerified: Boolean = false
)
