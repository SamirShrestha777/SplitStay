package com.softwarica.splitstay.repository

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google

enum class GoogleOrigin { WELCOME, LOGIN, REGISTER }
enum class GoogleOutcome { LOGGED_IN, REGISTERED, ALREADY_EXISTS }

@Volatile
private var currentOrigin: GoogleOrigin = GoogleOrigin.WELCOME

suspend fun startGoogleSignIn(origin: GoogleOrigin = GoogleOrigin.WELCOME): Result<Unit> {
    currentOrigin = origin
    return runCatching {
        SupabaseClientProvider.client.auth.signInWith(Google)
    }
}

fun googleOrigin(): GoogleOrigin = currentOrigin

fun resolveGoogleOutcome(): GoogleOutcome {
    val user = SupabaseClientProvider.client.auth.currentUserOrNull()
    val created = user?.createdAt?.toEpochMilliseconds()
    val lastSignIn = user?.lastSignInAt?.toEpochMilliseconds()
    val isNew: Boolean? =
        if (created != null && lastSignIn != null) lastSignIn - created <= 10_000 else null

    return when (currentOrigin) {
        GoogleOrigin.LOGIN -> GoogleOutcome.LOGGED_IN
        GoogleOrigin.REGISTER ->
            if (isNew == false) GoogleOutcome.ALREADY_EXISTS else GoogleOutcome.REGISTERED
        GoogleOrigin.WELCOME ->
            if (isNew == true) GoogleOutcome.REGISTERED else GoogleOutcome.LOGGED_IN
    }
}