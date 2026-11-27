package com.softwarica.splitstay.viewmodel.login

import androidx.lifecycle.ViewModel
import com.softwarica.splitstay.repository.AuthRepository

class LoginViewModel(val repo: AuthRepository) : ViewModel() {

    fun signIn(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.signIn(email, password, callback)
    }
}