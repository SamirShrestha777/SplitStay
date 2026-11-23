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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softwarica.splitstay.repository.VerifyEmailRepository
import com.softwarica.splitstay.viewmodel.verifyemail.VerifyEmailUiState
import com.softwarica.splitstay.viewmodel.verifyemail.VerifyEmailViewModel

// ── Font (same as RegisterScreen) ──────────────────────────────────────────────
private val AppFont: FontFamily = FontFamily.Default

// ── Route (stateful wrapper) ───────────────────────────────────────────────────

@Composable
fun VerifyEmailRoute(
    email: String,
    onBackClick: () -> Unit = {},
    onChangeEmailClick: () -> Unit = {},
    onVerified: () -> Unit = {}
) {
    val viewModel: VerifyEmailViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return VerifyEmailViewModel(
                    email = email,
                    repository = VerifyEmailRepository()
                ) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    // Navigate once when verification succeeds
    LaunchedEffect(uiState.isVerified) {
        if (uiState.isVerified) {
            onVerified()
        }
    }

    VerifyEmailScreen(
        uiState = uiState,
        onCodeChange = viewModel::onCodeChange,
        onVerifyClick = viewModel::onVerifyClick,
        onResendClick = viewModel::onResendClick,
        onErrorShown = viewModel::onErrorShown,
        onBackClick = onBackClick,
        onChangeEmailClick = onChangeEmailClick
    )
}

// ── Screen (stateless) ─────────────────────────────────────────────────────────

@Composable
fun VerifyEmailScreen(
    uiState: VerifyEmailUiState,
    onCodeChange: (String) -> Unit = {},
    onVerifyClick: () -> Unit = {},
    onResendClick: () -> Unit = {},
    onErrorShown: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onChangeEmailClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    // Auto-focus the hidden text field on open
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

        // ── Back arrow ─────────────────────────────────────────────────────────
        IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = BackArrowIcon,
                contentDescription = "Back",
                tint = TitleColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Mail icon in soft green circle ──────────────────────────────────────
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

        // ── Eyebrow ────────────────────────────────────────────────────────────
        Text(
            text = "ALMOST THERE",
            color = PrimaryGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppFont,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Title ──────────────────────────────────────────────────────────────
        Text(
            text = "Verify your email",
            color = TitleColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppFont
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Subtitle with bold email ───────────────────────────────────────────
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

        // ── "Wrong email?" link ────────────────────────────────────────────────
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

        // ── Six code boxes with hidden BasicTextField ──────────────────────────
        CodeInputRow(
            code = uiState.code,
            onCodeChange = onCodeChange,
            isError = uiState.errorMessage != null,
            focusRequester = focusRequester
        )

        // ── Error text ─────────────────────────────────────────────────────────
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

        // ── Verify button ──────────────────────────────────────────────────────
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
                    text = "Verify email",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFont
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Resend row ─────────────────────────────────────────────────────────
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

// ── Code input boxes ───────────────────────────────────────────────────────────

@Composable
private fun CodeInputRow(
    code: String,
    onCodeChange: (String) -> Unit,
    isError: Boolean,
    focusRequester: FocusRequester
) {
    Box {
        // Hidden BasicTextField that handles all keyboard input, paste, backspace
        BasicTextField(
            value = TextFieldValue(text = code, selection = TextRange(code.length)),
            onValueChange = { onCodeChange(it.text) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            cursorBrush = SolidColor(Color.Transparent),
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .height(1.dp) // effectively invisible
                .background(Color.Transparent),
            decorationBox = { innerTextField ->
                // Draw the visible code boxes over the hidden field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (i in 0 until 6) {
                        val char = code.getOrNull(i)
                        val isFocused = code.length == i
                        val borderColor = when {
                            isError -> ErrorRed
                            isFocused -> PrimaryGreen
                            else -> InputBorder
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .background(InputFill, RoundedCornerShape(16.dp))
                                .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char?.toString() ?: "",
                                color = TitleColor,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = AppFont,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                // The actual inner text field (invisible, positioned behind)
                innerTextField()
            }
        )
    }
}
