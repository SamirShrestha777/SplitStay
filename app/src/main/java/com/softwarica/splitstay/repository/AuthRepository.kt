
package com.softwarica.splitstay.repository

interface AuthRepository {

    fun signUp(
        name: String,
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    )

    fun signIn(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    )
}
