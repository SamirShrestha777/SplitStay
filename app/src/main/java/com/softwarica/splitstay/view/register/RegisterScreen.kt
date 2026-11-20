package com.softwarica.splitstay.view.register

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softwarica.splitstay.viewmodel.register.RegisterUiState
import com.softwarica.splitstay.viewmodel.register.RegisterViewModel

// ── Font (swap this single variable to change the whole screen) ────────────────
private val AppFont: FontFamily = FontFamily.Default

// ── Route (stateful wrapper) ───────────────────────────────────────────────────

@Composable
fun RegisterRoute(
    onBackClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onRegisterSuccess: (name: String, email: String, password: String) -> Unit = { _, _, _ -> },
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    RegisterScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onCreateAccountClick = { viewModel.onRegisterClick(onRegisterSuccess) },
        onBackClick = onBackClick,
        onSignInClick = onSignInClick,
        onGoogleClick = onGoogleClick
    )
}

// ── Screen (stateless) ─────────────────────────────────────────────────────────

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onTogglePasswordVisibility: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val fieldShape = RoundedCornerShape(16.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .imePadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        // ── Status bar spacing ─────────────────────────────────────────────────
        Spacer(modifier = Modifier.height(48.dp))

        // ── Top row: back arrow + app logo ─────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = BackArrowIcon,
                    contentDescription = "Back",
                    tint = DarkGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
            AppLogo(size = 40.dp)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Eyebrow ───────────────────────────────────────────────────────────
        Text(
            text = "WELCOME HOME",
            color = PrimaryGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppFont,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Title ──────────────────────────────────────────────────────────────
        Text(
            text = "Create your account",
            color = DarkGreen,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppFont
        )

        Spacer(modifier = Modifier.height(6.dp))

        // ── Subtitle ──────────────────────────────────────────────────────────
        Text(
            text = "Start making shared living simpler.",
            color = SubtitleGray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = AppFont
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Field 1: Your name ────────────────────────────────────────────────
        FieldLabel("Your name")
        Spacer(modifier = Modifier.height(6.dp))
        RegisterTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            placeholder = "e.g. Aayush",
            isError = uiState.nameError != null,
            shape = fieldShape,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        ErrorText(uiState.nameError)

        Spacer(modifier = Modifier.height(16.dp))

        // ── Field 2: Email address ────────────────────────────────────────────
        FieldLabel("Email address")
        Spacer(modifier = Modifier.height(6.dp))
        RegisterTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = "you@example.com",
            isError = uiState.emailError != null,
            shape = fieldShape,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        ErrorText(uiState.emailError)

        Spacer(modifier = Modifier.height(16.dp))

        // ── Field 3: Password ─────────────────────────────────────────────────
        FieldLabel("Password")
        Spacer(modifier = Modifier.height(6.dp))
        RegisterTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholder = "At least 6 characters",
            isError = uiState.passwordError != null,
            shape = fieldShape,
            visualTransformation = if (uiState.passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (uiState.passwordVisible) EyeIcon else EyeOffIcon,
                        contentDescription = if (uiState.passwordVisible) "Hide password" else "Show password",
                        tint = PlaceholderGray,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        )
        ErrorText(uiState.passwordError)

        Spacer(modifier = Modifier.height(28.dp))

        // ── Create account button ─────────────────────────────────────────────
        Button(
            onClick = onCreateAccountClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryGreen,
                contentColor = ButtonText
            )
        ) {
            Text(
                text = "Create account",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFont
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Divider row ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = DividerColor
            )
            Text(
                text = "or",
                color = SubtitleGray,
                fontSize = 14.sp,
                fontFamily = AppFont,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 1.dp,
                color = DividerColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Google button ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .border(
                    width = 1.dp,
                    color = PrimaryGreen,
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onGoogleClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GoogleIcon(size = 20.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Continue with Google",
                    color = PrimaryGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = AppFont
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Bottom text ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val annotated = buildAnnotatedString {
                withStyle(SpanStyle(color = SubtitleGray, fontWeight = FontWeight.Normal)) {
                    append("Already have an account? ")
                }
                pushStringAnnotation(tag = "SIGNIN", annotation = "signin")
                withStyle(SpanStyle(color = DarkGreen, fontWeight = FontWeight.Bold)) {
                    append("Sign in")
                }
                pop()
            }
            Text(
                text = annotated,
                fontSize = 14.sp,
                fontFamily = AppFont,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSignInClick
                )
            )
        }

        // ── Bottom cream space as in the image ───────────────────────────────
        Spacer(modifier = Modifier.height(40.dp))
    }
}

// ── Private composable helpers ─────────────────────────────────────────────────

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = DarkGreen,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = AppFont
    )
}

@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    shape: RoundedCornerShape,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = PlaceholderGray,
                fontSize = 15.sp,
                fontFamily = AppFont
            )
        },
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        shape = shape,
        textStyle = TextStyle(
            color = DarkGreen,
            fontSize = 15.sp,
            fontFamily = AppFont
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBackground,
            unfocusedContainerColor = InputBackground,
            errorContainerColor = InputBackground,
            focusedBorderColor = PrimaryGreen,
            unfocusedBorderColor = InputBorder,
            errorBorderColor = ErrorRed,
            cursorColor = PrimaryGreen
        )
    )
}

@Composable
private fun ErrorText(error: String?) {
    if (error != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = error,
            color = ErrorRed,
            fontSize = 12.sp,
            fontFamily = AppFont
        )
    }
}
