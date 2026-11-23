package com.softwarica.splitstay.repository

/**
 * Sealed class representing the possible error types from the email-verification flow.
 * The ViewModel maps these to user-friendly messages.
 */
sealed class VerifyEmailError : Exception() {

    /** The OTP code is incorrect or has expired. */
    data object InvalidOrExpiredCode : VerifyEmailError()

    /** Rate-limited — too many verification or resend attempts. */
    data object TooManyRequests : VerifyEmailError()

    /** Device is offline or the request timed out. */
    data object Network : VerifyEmailError()

    /** Any other unexpected error. */
    data object Unknown : VerifyEmailError()
}
