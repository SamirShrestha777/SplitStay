package com.softwarica.splitstay.viewmodel.register

import androidx.lifecycle.ViewModel
import com.softwarica.splitstay.repository.AuthRepository

class RegisterViewModel(val repo: AuthRepository) : ViewModel() {

    fun signUp(
        name: String,
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.signUp(name, email, password, callback)
    }
}