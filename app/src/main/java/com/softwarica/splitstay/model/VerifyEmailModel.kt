package com.softwarica.splitstay.model


data class VerifyEmailModel(
    val email: String = "",
    val code: String = "",
    val isVerifying: Boolean = false,
    val isResending: Boolean = false,
    val resendSecondsLeft: Int = 0,
    val errorMessage: String? = null,
    val resendMessage: String? = null,
    val isVerified: Boolean = false
)
