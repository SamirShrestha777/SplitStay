package com.softwarica.splitstay.view.household

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.softwarica.splitstay.navigation.AppNavigator
import com.softwarica.splitstay.ui.theme.SplitStayTheme

/**
 * Activity that hosts the Household Details screen.
 * Receives the household ID via Intent extra "HOUSEHOLD_ID".
 */
class HouseholdDetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val householdId = intent.getStringExtra("HOUSEHOLD_ID") ?: ""

        setContent {
            SplitStayTheme {
                HouseholdDetailsRoute(
                    householdId = householdId,
                    onBackClick = { finish() },
                    onEditClick = { id ->
                        AppNavigator.toEditHousehold(this, id)
                    }
                )
            }
        }
    }
}
