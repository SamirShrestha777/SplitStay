package com.softwarica.splitstay.view.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.lifecycleScope
import com.softwarica.splitstay.navigation.AppNavigator
import com.softwarica.splitstay.repository.GoogleOrigin
import com.softwarica.splitstay.repository.GoogleOutcome
import com.softwarica.splitstay.repository.SupabaseClientProvider
import com.softwarica.splitstay.repository.googleOrigin
import com.softwarica.splitstay.repository.resolveGoogleOutcome
import com.softwarica.splitstay.ui.theme.SplitStayTheme
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class AuthCallbackActivity : ComponentActivity() {

    private enum class Popup { NONE, ALREADY_EXISTS, FAILED }

    private val client get() = SupabaseClientProvider.client
    private var popup by mutableStateOf(Popup.NONE)
    private var origin: GoogleOrigin = GoogleOrigin.WELCOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        origin = googleOrigin()

        setContent {
            SplitStayTheme {
                val primary = Color(0xFF0F625C)
                when (popup) {
                    Popup.ALREADY_EXISTS -> AlertDialog(
                        onDismissRequest = {},
                        properties = DialogProperties(
                            dismissOnBackPress = false,
                            dismissOnClickOutside = false
                        ),
                        containerColor = Color.White,
                        title = { Text("Account already exists") },
                        text = {
                            Text(
                                "This Google account is already registered with SplitStay. " +
                                        "Please sign in instead."
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = { leaveTo(login = true) }) {
                                Text("Sign in", color = primary)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { leaveTo(login = false) }) {
                                Text("Cancel", color = Color.Gray)
                            }
                        }
                    )

                    Popup.FAILED -> AlertDialog(
                        onDismissRequest = {},
                        properties = DialogProperties(
                            dismissOnBackPress = false,
                            dismissOnClickOutside = false
                        ),
                        containerColor = Color.White,
                        title = { Text("Google sign-in failed") },
                        text = { Text("We couldn't complete Google sign-in. Please try again.") },
                        confirmButton = {
                            val toLogin = origin != GoogleOrigin.REGISTER
                            TextButton(onClick = { leaveTo(login = toLogin) }) {
                                Text(if (toLogin) "Back to Login" else "Back to Register", color = primary)
                            }
                        }
                    )

                    Popup.NONE -> {}
                }
            }
        }

        client.handleDeeplinks(intent)

        lifecycleScope.launch {
            val session = withTimeoutOrNull(15_000) {
                client.auth.sessionStatus.first { it is SessionStatus.Authenticated }
            }

            if (session == null) {
                popup = Popup.FAILED
                return@launch
            }

            when (resolveGoogleOutcome()) {
                GoogleOutcome.LOGGED_IN -> {
                    toast("Login successful")
                    AppNavigator.toHome(this@AuthCallbackActivity)
                    finish()
                }
                GoogleOutcome.REGISTERED -> {
                    toast("Registration successful")
                    AppNavigator.toHome(this@AuthCallbackActivity)
                    finish()
                }
                GoogleOutcome.ALREADY_EXISTS -> popup = Popup.ALREADY_EXISTS
            }
        }
    }

    /** Signs out, then opens Login or Register in a fresh task so the page works normally. */
    private fun leaveTo(login: Boolean) {
        lifecycleScope.launch {
            runCatching { client.auth.signOut() }
            if (login) {
                AppNavigator.toLoginClearingStack(this@AuthCallbackActivity)
            } else {
                AppNavigator.toRegisterClearingStack(this@AuthCallbackActivity)
            }
            finish()
        }
    }

    private fun toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        client.handleDeeplinks(intent)
    }
}