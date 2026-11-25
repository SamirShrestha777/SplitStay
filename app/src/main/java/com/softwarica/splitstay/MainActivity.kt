package com.softwarica.splitstay

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.softwarica.splitstay.view.register.RegisterRoute
import com.softwarica.splitstay.view.verifyemail.VerifyEmailRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var pendingEmail by remember { mutableStateOf<String?>(null) }
            val email = pendingEmail

            if (email == null) {
                RegisterRoute(
                    onRegisterSuccess = { _, registeredEmail, _ ->
                        pendingEmail = registeredEmail
                    }
                )
            } else {
                VerifyEmailRoute(
                    email = email,
                    onBackClick = { pendingEmail = null },
                    onChangeEmailClick = { pendingEmail = null },
                    onVerified = {
                        Toast.makeText(
                            this@MainActivity,
                            "Email verified!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }
    }
}