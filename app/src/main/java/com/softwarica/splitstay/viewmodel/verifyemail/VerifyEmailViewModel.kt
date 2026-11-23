package com.softwarica.splitstay.viewmodel.verifyemail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwarica.splitstay.repository.VerifyEmailError
import com.softwarica.splitstay.repository.VerifyEmailRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Verify Email screen.
 *
 * @param email       The email address the OTP was sent to.
 * @param repository  Repository for Supabase Auth OTP operations.
 */
class VerifyEmailViewModel(
    email: String,
    private val repository: VerifyEmailRepository = VerifyEmailRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyEmailUiState(email = email))
    val uiState: StateFlow<VerifyEmailUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null
    private var resendMessageJob: Job? = null

    init {
        startCooldown()
    }

    // ── Public functions ───────────────────────────────────────────────────────

    /**
     * Updates the code value. Accepts only digits and enforces max 6 characters.
     */
    fun onCodeChange(code: String) {
        val digits = code.filter { it.isDigit() }.take(6)
        _uiState.update { it.copy(code = digits, errorMessage = null) }
    }

    /**
     * Verifies the entered 6-digit code with Supabase.
     * Only runs when exactly 6 digits have been entered.
     */
    fun onVerifyClick() {
        val current = _uiState.value
        if (current.code.length != 6 || current.isVerifying) return

        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, errorMessage = null) }

            val result = repository.verifyCode(current.email, current.code)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isVerifying = false, isVerified = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isVerifying = false,
                            errorMessage = friendlyMessage(error)
                        )
                    }
                }
            )
        }
    }

    /**
     * Resends the OTP to the user's email.
     * Only runs when the cooldown has finished.
     */
    fun onResendClick() {
        val current = _uiState.value
        if (current.resendSecondsLeft > 0 || current.isResending) return

        viewModelScope.launch {
            _uiState.update { it.copy(isResending = true, errorMessage = null, resendMessage = null) }

            val result = repository.resendCode(current.email)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isResending = false, resendMessage = "Code sent again.") }
                    startCooldown()
                    clearResendMessageAfterDelay()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isResending = false,
                            errorMessage = friendlyMessage(error)
                        )
                    }
                }
            )
        }
    }

    /**
     * Clears the current error message (e.g. after a Snackbar is shown).
     */
    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private fun startCooldown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            _uiState.update { it.copy(resendSecondsLeft = 60) }
            for (seconds in 59 downTo 0) {
                delay(1_000L)
                _uiState.update { it.copy(resendSecondsLeft = seconds) }
            }
        }
    }

    private fun clearResendMessageAfterDelay() {
        resendMessageJob?.cancel()
        resendMessageJob = viewModelScope.launch {
            delay(3_000L)
            _uiState.update { it.copy(resendMessage = null) }
        }
    }

    private fun friendlyMessage(error: Throwable): String {
        return when (error) {
            is VerifyEmailError.InvalidOrExpiredCode -> "That code is incorrect or has expired."
            is VerifyEmailError.TooManyRequests -> "Too many attempts. Please wait a moment."
            is VerifyEmailError.Network -> "No internet connection."
            else -> "Something went wrong. Try again."
        }
    }
}
