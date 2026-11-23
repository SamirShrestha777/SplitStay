package com.softwarica.splitstay.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

/**
 * Singleton that provides the shared [SupabaseClient] used throughout the app.
 * Configured with the Auth plugin required for email OTP verification.
 */
object SupabaseClientProvider {

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SupabaseConfig.SUPABASE_URL,
            supabaseKey = SupabaseConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
        }
    }
}
