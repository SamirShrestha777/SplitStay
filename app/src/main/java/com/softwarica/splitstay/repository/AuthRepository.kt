package com.softwarica.splitstay.repository

interface AuthRepository {
    suspend fun signUp(name: String, email: String, password: String): Result<Unit>
}