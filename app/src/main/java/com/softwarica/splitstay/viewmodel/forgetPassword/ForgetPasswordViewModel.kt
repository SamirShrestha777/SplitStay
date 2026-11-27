package com.softwarica.splitstay.viewmodel.forgotpassword

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwarica.splitstay.model.AppDialog
import com.softwarica.splitstay.model.ForgotPasswordModel
import com.softwarica.splitstay.model.ForgotPasswordStep
import com.softwarica.splitstay.repository.AuthErrorKind
import com.softwarica.splitstay.repository.AuthFlowException
import com.softwarica.splitstay.repository.ForgotPasswordRepository
import com.softwarica.splitstay.repository.ForgotPasswordRepositoryImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val repository: ForgotPasswordRepository = ForgotPasswordRepositoryImpl()
) : ViewModel() {

    var state by mutableStateOf(ForgotPasswordModel())
        private set

    private var cooldownJob: Job? = null



    fun onEmailChange(value: String) {
        state = state.copy(email = value.trim(), emailError = null)
    }


    fun sendCode() {
        val email = state.email.trim().lowercase()

        if (state.loading) return

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            state = state.copy(
                emailError = "Enter a valid email address"
            )
            return
        }

        viewModelScope.launch {
            state = state.copy(
                email = email,
                loading = true,
                emailError = null,
                dialog = null
            )

            repository.sendResetCode(email).getOrElse {
                showError(it)
                return@launch
            }

            state = state.copy(
                loading = false,
                step = ForgotPasswordStep.VERIFY,
                code = "",
                codeError = null
            )

            startCooldown()
        }
    }




    fun onCodeChange(value: String) {
        state = state.copy(code = value.filter { it.isDigit() }.take(6), codeError = null)
    }

    fun verifyCode() {
        if (state.loading || state.code.length != 6) return

        viewModelScope.launch {
            state = state.copy(loading = true, codeError = null)

            repository.verifyResetCode(state.email, state.code).getOrElse {
                val kind = (it as? AuthFlowException)?.kind
                if (kind == AuthErrorKind.INVALID_CODE) {
                    // inline error under the code boxes
                    state = state.copy(loading = false, codeError = it.message)
                } else {
                    showError(it)
                }
                return@launch
            }

            state = state.copy(
                loading = false,
                step = ForgotPasswordStep.NEW_PASSWORD,
                newPassword = "",
                confirmPassword = "",
                passwordError = null
            )
        }
    }

    fun resendCode() {
        if (state.loading || state.resendSecondsLeft > 0) return

        viewModelScope.launch {
            state = state.copy(loading = true, codeError = null, resendMessage = null)

            repository.sendResetCode(state.email).getOrElse {
                showError(it)
                return@launch
            }

            state = state.copy(loading = false, resendMessage = "Code sent again.")
            startCooldown()
            delay(3_000)
            state = state.copy(resendMessage = null)
        }
    }



    fun onNewPasswordChange(value: String) {
        state = state.copy(newPassword = value, passwordError = null)
    }

    fun onConfirmPasswordChange(value: String) {
        state = state.copy(confirmPassword = value, passwordError = null)
    }

    fun updatePassword() {
        if (state.loading) return
        val password = state.newPassword

        when {
            password.length < 8 -> {
                state = state.copy(passwordError = "Password must be at least 8 characters")
                return
            }
            password != state.confirmPassword -> {
                state = state.copy(passwordError = "Passwords don't match")
                return
            }
        }

        viewModelScope.launch {
            state = state.copy(loading = true, passwordError = null)

            repository.updatePassword(password).getOrElse {
                val kind = (it as? AuthFlowException)?.kind
                when (kind) {
                    AuthErrorKind.SAME_PASSWORD, AuthErrorKind.WEAK_PASSWORD ->
                        state = state.copy(loading = false, passwordError = it.message)
                    AuthErrorKind.SESSION_EXPIRED -> {
                        showError(it)
                        goToEmailStep() // verification is gone, start over
                    }
                    else -> showError(it)
                }
                return@launch
            }

            state = state.copy(
                loading = false,
                dialog = AppDialog(
                    title = "Password updated",
                    message = "Your password has been changed. Please log in with your new password.",
                    isSuccess = true
                )
            )
        }
    }



    fun onBack(): Boolean = when (state.step) {
        ForgotPasswordStep.EMAIL -> false
        else -> {
            goToEmailStep()
            true
        }
    }

    fun goToEmailStep() {
        cooldownJob?.cancel()
        state = state.copy(
            step = ForgotPasswordStep.EMAIL,
            code = "",
            codeError = null,
            resendSecondsLeft = 0,
            resendMessage = null,
            newPassword = "",
            confirmPassword = "",
            passwordError = null,
            loading = false
        )
    }

    fun dismissDialog() {
        state = state.copy(dialog = null)
    }



    private fun showError(t: Throwable) {
        state = state.copy(
            loading = false,
            dialog = AppDialog(
                title = "Something went wrong",
                message = t.message ?: "Please try again."
            )
        )
    }

    private fun startCooldown(seconds: Int = 60) {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            state = state.copy(resendSecondsLeft = seconds)
            while (state.resendSecondsLeft > 0) {
                delay(1_000)
                state = state.copy(resendSecondsLeft = state.resendSecondsLeft - 1)
            }
        }
    }
}