package com.softwarica.splitstay.view.verifyemail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.softwarica.splitstay.model.VerifyEmailModel

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun VerifyEmailEmptyPreview() {
    VerifyEmailScreen(
        uiState = VerifyEmailModel(
            email = "aayush@example.com",
            resendSecondsLeft = 58
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun VerifyEmailFilledPreview() {
    VerifyEmailScreen(
        uiState = VerifyEmailModel(
            email = "aayush@example.com",
            code = "482915",
            resendSecondsLeft = 0
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun VerifyEmailErrorPreview() {
    VerifyEmailScreen(
        uiState = VerifyEmailModel(
            email = "aayush@example.com",
            code = "123456",
            errorMessage = "That code is incorrect or has expired.",
            resendSecondsLeft = 0
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun VerifyEmailCountdownPreview() {
    VerifyEmailScreen(
        uiState = VerifyEmailModel(
            email = "aayush@example.com",
            code = "48",
            resendSecondsLeft = 42
        )
    )
}