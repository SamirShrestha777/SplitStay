package com.softwarica.splitstay.repository

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepositoryImpl : AuthRepository {

    val auth : Auth = SupabaseClientProvider.client.auth

    override fun signUp(
        name: String,
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = runCatching {
                auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    data = buildJsonObject { put("name", name) }
                }
            }
            if(result.isSuccess){
                callback(true, "Register Successfully")
            } else {
                callback(false, "${result.exceptionOrNull()}")
            }
        }
    }

    override fun signIn(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = runCatching {
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
            }
            if(result.isSuccess){
                callback(true, "Login Successfully")
            } else {
                callback(false, "${result.exceptionOrNull()}")
            }
        }
    }
}