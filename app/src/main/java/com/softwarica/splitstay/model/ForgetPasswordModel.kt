package com.softwarica.splitstay.model

enum class ForgotPasswordStep { EMAIL, VERIFY, NEW_PASSWORD }

data class AppDialog(
    val title: String,
    val message: String,
    val isSuccess: Boolean = false
)

data class ForgotPasswordModel(
    val step: ForgotPasswordStep = ForgotPasswordStep.EMAIL,
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val loading: Boolean = false,
    val emailError: String? = null,
    val codeError: String? = null,
    val passwordError: String? = null,
    val resendSecondsLeft: Int = 0,
    val resendMessage: String? = null,
    val dialog: AppDialog? = null
)