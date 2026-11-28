package com.softwarica.splitstay.view.verifyemail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.softwarica.splitstay.navigation.AppNavigator
import com.softwarica.splitstay.ui.theme.SplitStayTheme

class VerifyEmailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val email = intent.getStringExtra(EXTRA_EMAIL).orEmpty()

        setContent {
            SplitStayTheme {
                VerifyEmailRoute(
                    email = email,
                    onBackClick = { finish() },
                    onChangeEmailClick = { finish() },
                    onVerified = { AppNavigator.toLoginClearingStack(this) }
                )
            }
        }
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
    }
}