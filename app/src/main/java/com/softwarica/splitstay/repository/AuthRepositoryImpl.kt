package com.softwarica.splitstay.repository

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class EmailAlreadyRegisteredException : Exception("Email already registered")

class AuthRepositoryImpl(
    private val auth: Auth = SupabaseClientProvider.client.auth
) : AuthRepository {

    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {

            val user = auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject { put("name", name) }
            }

            if (user != null && user.identities?.isEmpty() == true) {
                return@withContext Result.failure(EmailAlreadyRegisteredException())
            }

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}