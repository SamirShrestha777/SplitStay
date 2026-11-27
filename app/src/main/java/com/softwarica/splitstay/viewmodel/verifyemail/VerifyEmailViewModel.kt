package com.softwarica.splitstay.viewmodel.verifyemail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.softwarica.splitstay.repository.VerifyEmailRepository

class VerifyEmailViewModel(val repo: VerifyEmailRepository) : ViewModel() {

    //loading
    var loading by mutableStateOf(false)
        private set

    fun verifyCode(
        email: String,
        code: String,
        callback: (Boolean, String) -> Unit
    ) {
        loading = true
        repo.verifyCode(email, code) { success, message ->
            loading = false
            callback(success, message)
        }
    }

    fun resendCode(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        loading = true
        repo.resendCode(email) { success, message ->
            loading = false
            callback(success, message)
        }
    }
}