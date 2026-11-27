package com.softwarica.splitstay.view.forgotpassword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.softwarica.splitstay.ui.theme.SplitStayTheme

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitStayTheme {
                ForgotPasswordRoute(onFinished = { finish() })
            }
        }
    }
}