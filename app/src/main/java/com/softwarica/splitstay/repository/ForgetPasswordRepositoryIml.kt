
package com.softwarica.splitstay.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CancellationException
import java.io.IOException
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ForgotPasswordRepositoryImpl(
    private val client: SupabaseClient = SupabaseClientProvider.client
) : ForgotPasswordRepository {

    override suspend fun emailExists(
        email: String
    ): Result<Boolean> = safeCall {
        client.postgrest.rpc(
            function = "email_exists",
            parameters = buildJsonObject {
                put("p_email", email.trim().lowercase())
            }
        ).decodeAs<Boolean>()
    }

    override suspend fun sendResetCode(email: String): Result<Unit> = safeCall {
        client.auth.resetPasswordForEmail(
            email = email.trim().lowercase()
        )
    }

    override suspend fun verifyResetCode(
        email: String,
        code: String
    ): Result<Unit> = safeCall {
        client.auth.verifyEmailOtp(
            type = OtpType.Email.RECOVERY,
            email = email.trim().lowercase(),
            token = code.trim()
        )
    }

    override suspend fun updatePassword(
        newPassword: String
    ): Result<Unit> = safeCall {
        client.auth.updateUser {
            password = newPassword
        }

        signOutQuietly()
    }

    private suspend fun signOutQuietly() {
        try {
            client.auth.signOut()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // Password was already updated.
        }
    }

    private suspend fun <T> safeCall(
        block: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toAuthFlowException())
        }
    }

    private fun Throwable.toAuthFlowException(): AuthFlowException {
        return when (this) {
            is AuthFlowException -> this

            is HttpRequestTimeoutException,
            is HttpRequestException,
            is IOException -> AuthFlowException(
                AuthErrorKind.NETWORK,
                "Couldn't reach the server. Check your internet connection."
            )

            is RestException -> {
                val msg = message.orEmpty().lowercase()

                when {
                    statusCode == 429 ||
                            "rate limit" in msg ||
                            "too many" in msg ->
                        AuthFlowException(
                            AuthErrorKind.RATE_LIMIT,
                            "Too many attempts. Please wait before trying again."
                        )

                    "different from the old" in msg ||
                            "same as the old" in msg ->
                        AuthFlowException(
                            AuthErrorKind.SAME_PASSWORD,
                            "Your new password must be different from your old password."
                        )

                    "password" in msg &&
                            ("weak" in msg || "should" in msg) ->
                        AuthFlowException(
                            AuthErrorKind.WEAK_PASSWORD,
                            "Your password is too weak. Use at least 8 characters."
                        )

                    "token" in msg ||
                            "otp" in msg ||
                            "expired" in msg ||
                            "invalid" in msg ->
                        AuthFlowException(
                            AuthErrorKind.INVALID_CODE,
                            "The code is invalid or has expired."
                        )

                    statusCode == 401 ||
                            "session" in msg ||
                            "jwt" in msg ->
                        AuthFlowException(
                            AuthErrorKind.SESSION_EXPIRED,
                            "Your verification session has expired. Please start again."
                        )

                    else -> AuthFlowException(
                        AuthErrorKind.UNKNOWN,
                        message ?: "Something went wrong. Please try again."
                    )
                }
            }

            else -> AuthFlowException(
                AuthErrorKind.UNKNOWN,
                message ?: "Something went wrong. Please try again."
            )
        }
    }
}
