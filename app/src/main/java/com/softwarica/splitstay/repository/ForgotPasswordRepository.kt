package com.softwarica.splitstay.repository

enum class AuthErrorKind {
    NETWORK, INVALID_CODE, RATE_LIMIT, SAME_PASSWORD, WEAK_PASSWORD, SESSION_EXPIRED, UNKNOWN
}

class AuthFlowException(
    val kind: AuthErrorKind,
    override val message: String
) : Exception(message)

interface ForgotPasswordRepository {

    suspend fun emailExists(email: String): Result<Boolean>

    suspend fun sendResetCode(email: String): Result<Unit>

    suspend fun verifyResetCode(email: String, code: String): Result<Unit>

    suspend fun updatePassword(newPassword: String): Result<Unit>
}