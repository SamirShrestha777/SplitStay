package com.softwarica.splitstay.repository

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class VerifyEmailRepositoryImpl : VerifyEmailRepository {

    val auth : Auth = SupabaseClientProvider.client.auth

    override fun verifyCode(
        email: String,
        code: String,
        callback: (Boolean, String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = runCatching {
                auth.verifyEmailOtp(
                    type = OtpType.Email.SIGNUP,
                    email = email,
                    token = code
                )
            }
            if(result.isSuccess){
                callback(true, "Email Verified Successfully")
            } else {
                callback(false, friendlyMessage(result.exceptionOrNull()))
            }
        }
    }

    override fun resendCode(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = runCatching {
                auth.resendEmail(
                    type = OtpType.Email.SIGNUP,
                    email = email
                )
            }
            if(result.isSuccess){
                callback(true, "Code sent to $email")
            } else {
                callback(false, friendlyMessage(result.exceptionOrNull()))
            }
        }
    }

    // turns an error into a short message the user can read
    private fun friendlyMessage(e: Throwable?): String {
        val msg = e?.message.orEmpty().lowercase()
        return when {
            e is HttpRequestException ||
                    e is HttpRequestTimeoutException ||
                    e is UnknownHostException ||
                    e?.cause is UnknownHostException -> "No internet connection."

            "rate" in msg || "too many" in msg || "429" in msg ->
                "Too many attempts. Please wait a moment."

            "otp" in msg || "token" in msg || "expired" in msg || "invalid" in msg ->
                "That code is incorrect or has expired."

            else -> "Something went wrong. Try again."
        }
    }
}