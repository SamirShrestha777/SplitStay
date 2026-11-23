package com.softwarica.splitstay.repository

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.net.UnknownHostException

/**
 * Repository that handles Supabase Auth operations for email OTP verification.
 * Uses the existing [SupabaseClientProvider.client].
 */
class VerifyEmailRepository {

    private val auth get() = SupabaseClientProvider.client.auth

    /**
     * Verifies the 6-digit OTP [code] sent to [email] during sign-up.
     *
     * @return [Result.success] if the OTP is valid, [Result.failure] with
     *         a [VerifyEmailError] subtype otherwise.
     */
    suspend fun verifyCode(email: String, code: String): Result<Unit> {
        return try {
            auth.verifyEmailOtp(
                type = OtpType.Email.SIGNUP,
                email = email,
                token = code
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapError(e))
        }
    }

    /**
     * Re-sends the sign-up OTP to [email].
     *
     * @return [Result.success] on a successful resend, [Result.failure] with
     *         a [VerifyEmailError] subtype otherwise.
     */
    suspend fun resendCode(email: String): Result<Unit> {
        return try {
            auth.resendEmail(
                type = OtpType.Email.SIGNUP,
                email = email
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapError(e))
        }
    }

    // ── Error mapping ──────────────────────────────────────────────────────────

    private fun mapError(e: Exception): VerifyEmailError {
        return when {
            // Network-level errors
            e is HttpRequestException ||
            e is HttpRequestTimeoutException ||
            e is UnknownHostException ||
            e.cause is UnknownHostException -> VerifyEmailError.Network

            // Supabase REST errors
            e is RestException -> {
                val msg = e.message.orEmpty().lowercase()
                when {
                    "otp" in msg || "token" in msg || "expired" in msg ||
                    "invalid" in msg -> VerifyEmailError.InvalidOrExpiredCode

                    "rate" in msg || "too many" in msg ||
                    "429" in msg -> VerifyEmailError.TooManyRequests

                    else -> VerifyEmailError.Unknown
                }
            }

            else -> VerifyEmailError.Unknown
        }
    }
}
