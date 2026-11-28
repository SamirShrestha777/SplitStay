
package com.softwarica.splitstay.repository

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class VerifyEmailRepositoryImpl : VerifyEmailRepository {

    private val auth: Auth = SupabaseClientProvider.client.auth

    override fun verifyCode(
        email: String,
        code: String,
        callback: (Boolean, String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                auth.verifyEmailOtp(
                    type = OtpType.Email.SIGNUP,
                    email = email.trim().lowercase(),
                    token = code.trim()
                )

                callback(true, "Email verified successfully.")

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                callback(false, friendlyMessage(e))
            }
        }
    }

    override fun resendCode(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                auth.resendEmail(
                    type = OtpType.Email.SIGNUP,
                    email = email.trim().lowercase()
                )

                callback(true, "A new verification code has been requested.")

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                callback(false, friendlyMessage(e))
            }
        }
    }

    private fun friendlyMessage(e: Throwable): String {
        val message = e.message.orEmpty().lowercase()

        return when {
            e is HttpRequestException ||
                    e is HttpRequestTimeoutException ||
                    e is IOException ->
                "Network error. Check your internet connection."

            "429" in message ||
                    "rate" in message ||
                    "too many" in message ->
                "Too many attempts. Please wait before trying again."

            "expired" in message ||
                    "invalid" in message ||
                    "otp" in message ||
                    "token" in message ->
                "The verification code is invalid or has expired."

            else -> e.message ?: "Unable to verify your email. Please try again."
        }
    }
}
