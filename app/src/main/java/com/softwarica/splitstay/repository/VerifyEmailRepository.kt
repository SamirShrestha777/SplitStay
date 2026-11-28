package com.softwarica.splitstay.repository

interface VerifyEmailRepository {
    fun verifyCode(
        email: String,
        code: String,
        callback: (Boolean, String) -> Unit
    )

    fun resendCode(
        email: String,
        callback: (Boolean, String) -> Unit
    )
}