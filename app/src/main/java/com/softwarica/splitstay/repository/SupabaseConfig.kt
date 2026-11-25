package com.softwarica.splitstay.repository

import com.softwarica.splitstay.BuildConfig

object SupabaseConfig {
    const val SUPABASE_URL: String = BuildConfig.SUPABASE_URL
    const val SUPABASE_ANON_KEY: String = BuildConfig.SUPABASE_PUBLISHABLE_KEY
}
