package com.softwarica.splitstay.view.forgotpassword

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softwarica.splitstay.model.AppDialog
import com.softwarica.splitstay.model.ForgotPasswordModel
import com.softwarica.splitstay.model.ForgotPasswordStep
import com.softwarica.splitstay.model.VerifyEmailModel
import com.softwarica.splitstay.ui.theme.SplitStayTheme
import com.softwarica.splitstay.view.verifyemail.VerifyEmailScreen
import com.softwarica.splitstay.viewmodel.forgotpassword.ForgotPasswordViewModel

private val FpBackground = Color(0xFFFAF5E9)
private val FpPrimary = Color(0xFF0F5F57)
private val FpTitle = Color(0xFF17372F)
private val FpSubtitle = Color(0xFF6B7A75)
private val FpBorder = Color(0xFFEADFC8)
private val FpError = Color(0xFFC0392B)



@Composable
fun ForgotPasswordRoute(
    onFinished: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    ForgotPasswordContent(
        state = viewModel.state,
        onEmailChange = viewModel::onEmailChange,
        onSendClick = viewModel::sendCode,
        onCodeChange = viewModel::onCodeChange,
        onVerifyClick = viewModel::verifyCode,
        onResendClick = viewModel::resendCode,
        onChangeEmailClick = viewModel::goToEmailStep,
        onPasswordChange = viewModel::onNewPasswordChange,
        onConfirmChange = viewModel::onConfirmPasswordChange,
        onSubmitClick = viewModel::updatePassword,
        onStepBack = { viewModel.onBack() },
        onDismissDialog = viewModel::dismissDialog,
        onFinished = onFinished
    )

    BackHandler { if (!viewModel.onBack()) onFinished() }
}


@Composable
fun ForgotPasswordContent(
    state: ForgotPasswordModel,
    onEmailChange: (String) -> Unit = {},
    onSendClick: () -> Unit = {},
    onCodeChange: (String) -> Unit = {},
    onVerifyClick: () -> Unit = {},
    onResendClick: () -> Unit = {},
    onChangeEmailClick: () -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmChange: (String) -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onStepBack: () -> Unit = {},
    onDismissDialog: () -> Unit = {},
    onFinished: () -> Unit = {}
) {
    when (state.step) {
        ForgotPasswordStep.EMAIL -> EmailStep(
            state = state,
            onEmailChange = onEmailChange,
            onSendClick = onSendClick,
            onBackClick = onFinished
        )

        ForgotPasswordStep.VERIFY -> VerifyEmailScreen(
            uiState = VerifyEmailModel(
                email = state.email,
                code = state.code,
                isVerifying = state.loading,
                resendSecondsLeft = state.resendSecondsLeft,
                errorMessage = state.codeError,
                resendMessage = state.resendMessage
            ),
            eyebrow = "CHECK YOUR INBOX",
            title = "Enter reset code",
            buttonText = "Verify code",
            onCodeChange = onCodeChange,
            onVerifyClick = onVerifyClick,
            onResendClick = onResendClick,
            onBackClick = onStepBack,
            onChangeEmailClick = onChangeEmailClick
        )

        ForgotPasswordStep.NEW_PASSWORD -> NewPasswordStep(
            state = state,
            onPasswordChange = onPasswordChange,
            onConfirmChange = onConfirmChange,
            onSubmitClick = onSubmitClick,
            onBackClick = onStepBack
        )
    }

    state.dialog?.let { dialog ->
        AlertDialog(
            onDismissRequest = {
                onDismissDialog()
                if (dialog.isSuccess) onFinished()
            },
            containerColor = Color.White,
            title = {
                Text(dialog.title, color = FpTitle, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(dialog.message, color = FpSubtitle)
            },
            confirmButton = {
                TextButton(onClick = {
                    onDismissDialog()
                    if (dialog.isSuccess) onFinished()
                }) {
                    Text(
                        text = if (dialog.isSuccess) "Go to login" else "OK",
                        color = FpPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}


@Composable
private fun EmailStep(
    state: ForgotPasswordModel,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit
) {
    StepScaffold(
        icon = Icons.Filled.Email,
        eyebrow = "FORGOT PASSWORD",
        title = "Reset your password",
        subtitle = "Enter the email linked to your account and we'll send you a 6-digit code.",
        onBackClick = onBackClick
    ) {
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("Email address") },
            singleLine = true,
            isError = state.emailError != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onSendClick() }),
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        FieldError(state.emailError)

        Spacer(Modifier.height(28.dp))

        PrimaryButton(
            text = "Send code",
            loading = state.loading,
            enabled = state.email.isNotBlank() && !state.loading,
            onClick = onSendClick
        )
    }
}

@Composable
private fun NewPasswordStep(
    state: ForgotPasswordModel,
    onPasswordChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit
) {
    StepScaffold(
        icon = Icons.Filled.Lock,
        eyebrow = "LAST STEP",
        title = "Create new password",
        subtitle = "Your new password must be at least 8 characters long.",
        onBackClick = onBackClick
    ) {
        PasswordField(
            value = state.newPassword,
            onValueChange = onPasswordChange,
            label = "New password",
            isError = state.passwordError != null,
            imeAction = ImeAction.Next
        )

        Spacer(Modifier.height(16.dp))

        PasswordField(
            value = state.confirmPassword,
            onValueChange = onConfirmChange,
            label = "Confirm password",
            isError = state.passwordError != null,
            imeAction = ImeAction.Done,
            onDone = onSubmitClick
        )
        FieldError(state.passwordError)

        Spacer(Modifier.height(28.dp))

        PrimaryButton(
            text = "Update password",
            loading = state.loading,
            enabled = state.newPassword.isNotEmpty() &&
                    state.confirmPassword.isNotEmpty() && !state.loading,
            onClick = onSubmitClick
        )
    }
}


@Composable
private fun StepScaffold(
    icon: ImageVector,
    eyebrow: String,
    title: String,
    subtitle: String,
    onBackClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FpBackground)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = FpTitle,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(FpPrimary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = FpPrimary, modifier = Modifier.size(30.dp))
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = eyebrow,
            color = FpPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(text = title, color = FpTitle, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(text = subtitle, color = FpSubtitle, fontSize = 15.sp, lineHeight = 22.sp)

        Spacer(Modifier.height(28.dp))

        content()

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    loading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FpPrimary,
            contentColor = Color.White,
            disabledContainerColor = FpPrimary.copy(alpha = 0.5f),
            disabledContentColor = Color.White.copy(alpha = 0.7f)
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(text = text, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    imeAction: ImeAction,
    onDone: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        trailingIcon = {
            Text(
                text = if (visible) "Hide" else "Show",
                color = FpPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clickable { visible = !visible }
            )
        },
        shape = RoundedCornerShape(16.dp),
        colors = fieldColors(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun FieldError(message: String?) {
    if (message != null) {
        Spacer(Modifier.height(8.dp))
        Text(text = message, color = FpError, fontSize = 12.sp)
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    errorContainerColor = Color.White,
    focusedBorderColor = FpPrimary,
    unfocusedBorderColor = FpBorder,
    errorBorderColor = FpError,
    focusedTextColor = FpTitle,
    unfocusedTextColor = FpTitle,
    errorTextColor = FpTitle,
    cursorColor = FpPrimary,
    focusedLabelColor = FpPrimary,
    unfocusedLabelColor = FpSubtitle,
    errorLabelColor = FpError
)



@Preview(showBackground = true, showSystemUi = true, name = "1 - Email")
@Composable
private fun EmailStepPreview() {
    SplitStayTheme {
        ForgotPasswordContent(state = ForgotPasswordModel(email = "aayush@example.com"))
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "1 - Email error")
@Composable
private fun EmailStepErrorPreview() {
    SplitStayTheme {
        ForgotPasswordContent(
            state = ForgotPasswordModel(email = "bad@", emailError = "Enter a valid email address")
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "2 - Verify code")
@Composable
private fun VerifyStepPreview() {
    SplitStayTheme {
        ForgotPasswordContent(
            state = ForgotPasswordModel(
                step = ForgotPasswordStep.VERIFY,
                email = "aayush@example.com",
                code = "123",
                resendSecondsLeft = 42
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "2 - Verify code error")
@Composable
private fun VerifyStepErrorPreview() {
    SplitStayTheme {
        ForgotPasswordContent(
            state = ForgotPasswordModel(
                step = ForgotPasswordStep.VERIFY,
                email = "aayush@example.com",
                code = "123456",
                codeError = "That code is invalid or has expired."
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "3 - New password")
@Composable
private fun NewPasswordStepPreview() {
    SplitStayTheme {
        ForgotPasswordContent(
            state = ForgotPasswordModel(
                step = ForgotPasswordStep.NEW_PASSWORD,
                newPassword = "secret123",
                confirmPassword = "secret12",
                passwordError = "Passwords don't match"
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Success dialog")
@Composable
private fun SuccessDialogPreview() {
    SplitStayTheme {
        ForgotPasswordContent(
            state = ForgotPasswordModel(
                step = ForgotPasswordStep.NEW_PASSWORD,
                dialog = AppDialog(
                    title = "Password updated",
                    message = "Your password has been changed. Please log in with your new password.",
                    isSuccess = true
                )
            )
        )
    }
}