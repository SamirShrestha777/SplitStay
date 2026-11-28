package com.softwarica.splitstay.view.household

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.softwarica.splitstay.ui.theme.SplitStayTheme

/**
 * Activity that hosts the Edit Household screen.
 * Receives the household ID via Intent extra "HOUSEHOLD_ID".
 */
class EditHouseholdActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val householdId = intent.getStringExtra("HOUSEHOLD_ID") ?: ""

        setContent {
            SplitStayTheme {
                EditHouseholdRoute(
                    householdId = householdId,
                    onBackClick = { finish() },
                    onSaved = { finish() }
                )
            }
        }
    }
}
