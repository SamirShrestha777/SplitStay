package com.softwarica.splitstay.view.verifyemail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softwarica.splitstay.model.VerifyEmailModel
import com.softwarica.splitstay.repository.VerifyEmailRepositoryImpl
import com.softwarica.splitstay.ui.theme.SplitStayTheme
import com.softwarica.splitstay.viewmodel.verifyemail.VerifyEmailViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

private val AppFont: FontFamily = FontFamily.Default

@Composable
fun VerifyEmailRoute(
    email: String,
    onBackClick: () -> Unit = {},
    onChangeEmailClick: () -> Unit = {},
    onVerified: () -> Unit = {}
) {
    val verifyEmailViewModel = remember { VerifyEmailViewModel(VerifyEmailRepositoryImpl()) }
    val loading = verifyEmailViewModel.loading

    var code by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendMessage by remember { mutableStateOf<String?>(null) }
    var resendSecondsLeft by remember { mutableStateOf(60) }
    var cooldownKey by remember { mutableStateOf(0) }


    LaunchedEffect(cooldownKey) {
        resendSecondsLeft = 60
        while (resendSecondsLeft > 0) {
            delay(1.seconds)
            resendSecondsLeft--
        }
    }


    LaunchedEffect(resendMessage) {
        if (resendMessage != null) {
            delay(3.seconds)
            resendMessage = null
        }
    }

    VerifyEmailScreen(
        uiState = VerifyEmailModel(
            email = email,
            code = code,
            isVerifying = loading,
            resendSecondsLeft = resendSecondsLeft,
            errorMessage = errorMessage,
            resendMessage = resendMessage
        ),
        onCodeChange = {
            code = it.filter { c -> c.isDigit() }.take(6)
            errorMessage = null
        },
        onVerifyClick = {
            if (code.length == 6 && !loading) {
                errorMessage = null
                verifyEmailViewModel.verifyCode(email, code) { success, message ->
                    if (success) {
                        onVerified()
                    } else {
                        errorMessage = message
                    }
                }
            }
        },
        onResendClick = {
            if (resendSecondsLeft == 0 && !loading) {
                errorMessage = null
                resendMessage = null
                verifyEmailViewModel.resendCode(email) { success, message ->
                    if (success) {
                        resendMessage = "Code sent again."
                        cooldownKey++
                    } else {
                        errorMessage = message
                    }
                }
            }
        },
        onBackClick = onBackClick,
        onChangeEmailClick = onChangeEmailClick
    )
}

@Composable
fun VerifyEmailScreen(
    uiState: VerifyEmailModel,
    eyebrow: String = "ALMOST THERE",
    title: String = "Verify your email",
    buttonText: String = "Verify email",
    onCodeChange: (String) -> Unit = {},
    onVerifyClick: () -> Unit = {},
    onResendClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onChangeEmailClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = BackArrowIcon,
                contentDescription = "Back",
                tint = TitleColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = PrimaryGreen.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = MailIcon,
                contentDescription = "Email",
                tint = PrimaryGreen,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = eyebrow,
            color = PrimaryGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppFont,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            color = TitleColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppFont
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = SubtitleGray, fontWeight = FontWeight.Normal)) {
                    append("We sent a 6-digit code to ")
                }
                withStyle(SpanStyle(color = SubtitleGray, fontWeight = FontWeight.Bold)) {
                    append(uiState.email)
                }
            },
            fontSize = 15.sp,
            fontFamily = AppFont,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Wrong email?",
            color = PrimaryGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppFont,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onChangeEmailClick
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        CodeInputRow(
            code = uiState.code,
            onCodeChange = onCodeChange,
            isError = uiState.errorMessage != null,
            focusRequester = focusRequester
        )

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.errorMessage,
                color = ErrorRed,
                fontSize = 12.sp,
                fontFamily = AppFont
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        val isEnabled = uiState.code.length == 6 && !uiState.isVerifying
        Button(
            onClick = onVerifyClick,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                disabledContainerColor = PrimaryGreen.copy(alpha = 0.5f),
                disabledContentColor = Color.White.copy(alpha = 0.7f)
            )
        ) {
            if (uiState.isVerifying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = buttonText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.resendMessage != null) {
                Text(
                    text = uiState.resendMessage,
                    color = PrimaryGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = AppFont
                )
            } else if (uiState.resendSecondsLeft > 0) {
                val minutes = uiState.resendSecondsLeft / 60
                val seconds = uiState.resendSecondsLeft % 60
                Text(
                    text = "Resend in $minutes:${seconds.toString().padStart(2, '0')}",
                    color = SubtitleGray,
                    fontSize = 14.sp,
                    fontFamily = AppFont
                )
            } else {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = SubtitleGray, fontWeight = FontWeight.Normal)) {
                            append("Didn't get the code? ")
                        }
                        withStyle(SpanStyle(color = TitleColor, fontWeight = FontWeight.Bold)) {
                            append("Resend")
                        }
                    },
                    fontSize = 14.sp,
                    fontFamily = AppFont,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onResendClick
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun CodeInputRow(
    code: String,
    onCodeChange: (String) -> Unit,
    isError: Boolean,
    focusRequester: FocusRequester
) {
    val digitColor = Color(0xFF17372F)
    val boxFill = Color.White
    val normalBorder = Color(0xFFEADFC8)
    val activeBorder = Color(0xFF0F5F57)
    val errorBorder = Color(0xFFC0392B)

    Box(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (i in 0 until 6) {
                val char = code.getOrNull(i)
                val isFocused = code.length == i
                val borderColor = when {
                    isError -> errorBorder
                    isFocused -> activeBorder
                    else -> normalBorder
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .background(boxFill, RoundedCornerShape(16.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char?.toString() ?: "",
                        color = digitColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFont,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        BasicTextField(
            value = TextFieldValue(text = code, selection = TextRange(code.length)),
            onValueChange = { onCodeChange(it.text) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = TextStyle(color = Color.Transparent),
            cursorBrush = SolidColor(Color.Transparent),
            modifier = Modifier
                .matchParentSize()
                .focusRequester(focusRequester)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Sign-up verify")
@Composable
private fun VerifyEmailPreview() {
    SplitStayTheme {
        VerifyEmailScreen(
            uiState = VerifyEmailModel(
                email = "aayush@example.com",
                code = "123",
                resendSecondsLeft = 42
            )
        )
    }
}