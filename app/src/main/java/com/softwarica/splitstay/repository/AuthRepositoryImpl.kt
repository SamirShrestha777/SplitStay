package com.softwarica.splitstay.repository

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepositoryImpl : AuthRepository {

    private val auth: Auth = SupabaseClientProvider.client.auth

    override fun signUp(
        name: String,
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val user = auth.signUpWith(Email) {
                    this.email = email.trim().lowercase()
                    this.password = password
                    data = buildJsonObject {
                        put("name", name.trim())
                    }
                }

                if (user != null && user.identities.isNullOrEmpty()) {
                    callback(
                        false,
                        "An account with this email already exists."
                    )
                } else {
                    callback(
                        true,
                        "Verification request submitted. Check your email for the OTP."
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                callback(
                    false,
                    e.message ?: "Registration failed. Please try again."
                )
            }
        }
    }

    override fun signIn(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                auth.signInWith(Email) {
                    this.email = email.trim().lowercase()
                    this.password = password
                }

                callback(true, "Login successful.")

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                callback(
                    false,
                    e.message ?: "Login failed. Please check your details."
                )
            }
        }
    }
}
