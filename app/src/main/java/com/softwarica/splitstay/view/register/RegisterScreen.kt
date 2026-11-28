package com.softwarica.splitstay.view.register

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softwarica.splitstay.R
import com.softwarica.splitstay.model.RegisterModel
import com.softwarica.splitstay.navigation.AppNavigator
import com.softwarica.splitstay.repository.AuthRepositoryImpl
import com.softwarica.splitstay.repository.startGoogleSignIn
import com.softwarica.splitstay.ui.theme.SplitStayTheme
import com.softwarica.splitstay.viewmodel.register.RegisterViewModel
import kotlinx.coroutines.launch
import com.softwarica.splitstay.repository.GoogleOrigin
class RegisterUIActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitStayTheme {
                RegisterRoute(
                    onRegisterSuccess = { _, email, _ ->
                        AppNavigator.toVerifyEmail(this, email)
                    },
                    onBackClick = { AppNavigator.back(this) },
                    onSignInClick = { AppNavigator.toLogin(this, finishCurrent = true) }
                )
            }
        }
    }
}

@Composable
fun RegisterRoute(
    onRegisterSuccess: (name: String, email: String, password: String) -> Unit,
    onBackClick: () -> Unit = {},
    onSignInClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val registerViewModel = remember { RegisterViewModel(AuthRepositoryImpl()) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    RegisterBody(
        uiState = RegisterModel(
            name = name,
            email = email,
            password = password,
            passwordVisible = passwordVisible,
            nameError = nameError,
            emailError = emailError,
            passwordError = passwordError,
            generalError = generalError,
            isLoading = loading
        ),
        onNameChange = {
            name = it
            nameError = null
            generalError = null
        },
        onEmailChange = {
            email = it
            emailError = null
            generalError = null
        },
        onPasswordChange = {
            password = it
            passwordError = null
            generalError = null
        },
        onTogglePasswordVisibility = {
            passwordVisible = !passwordVisible
        },
        onCreateAccountClick = {
            if (!loading) {
                val trimmedName = name.trim()
                val trimmedEmail = email.trim()

                nameError = if (trimmedName.isBlank()) "Name is required" else null
                emailError =
                    if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches())
                        "Enter a valid email" else null
                passwordError =
                    if (password.length < 6)
                        "Password must be at least 6 characters" else null
                generalError = null

                if (nameError == null && emailError == null && passwordError == null) {
                    loading = true
                    registerViewModel.signUp(trimmedName, trimmedEmail, password) { success, message ->
                        loading = false
                        if (success) {
                            onRegisterSuccess(trimmedName, trimmedEmail, password)
                        } else {
                            val msg = message.lowercase()
                            when {
                                "already" in msg ->
                                    emailError = "An account with this email already exists. Try signing in."

                                "invalid" in msg && "email" in msg ->
                                    emailError = "Enter a valid email address"

                                "rate" in msg || "too many" in msg || "429" in msg ->
                                    generalError = "Too many attempts. Please wait a few minutes and try again."

                                else ->
                                    generalError = "Sign up failed. Check your connection and try again."
                            }
                        }
                    }
                }
            }
        },
        onBackClick = onBackClick,
        onSignInClick = onSignInClick,
        onGoogleClick = {
            if (!loading) {
                generalError = null
                scope.launch {
                    startGoogleSignIn(GoogleOrigin.REGISTER).onFailure {
                        generalError = "Google sign-in failed. Check your connection and try again."
                    }
                }
            }
        }
    )
}

@Composable
fun RegisterBody(
    uiState: RegisterModel,
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

    val backgroundColor = Color(0xFFFEF9F0)
    val primaryColor = Color(0xFF0F625C)
    val errorColor = Color(0xFFD32F2F)

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        errorContainerColor = Color.White,
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = Color(0xFFD8D8D8),
        errorBorderColor = errorColor,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.backicon),
                contentDescription = "Back Icon",
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onBackClick() }
            )
            AppLogo(size = 40.dp)
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "WELCOME HOME",
            fontSize = 12.sp,
            color = primaryColor,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Text(
            text = "Create your account",
            fontSize = 22.sp,
            color = Color(0xFF163B38),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Start making shared living simpler.",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 5.dp)
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Name
        Text(text = "Your name", fontSize = 12.sp, color = Color.DarkGray)
        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            placeholder = { Text("e.g. Aayush", fontSize = 12.sp, color = Color.Gray) },
            textStyle = TextStyle(fontSize = 14.sp),
            isError = uiState.nameError != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = fieldColors
        )
        FieldError(uiState.nameError, errorColor)

        Spacer(modifier = Modifier.height(15.dp))

        // Email
        Text(text = "Email address", fontSize = 12.sp, color = Color.DarkGray)
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = { Text("you@example.com", fontSize = 12.sp, color = Color.Gray) },
            textStyle = TextStyle(fontSize = 14.sp),
            isError = uiState.emailError != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            colors = fieldColors
        )
        FieldError(uiState.emailError, errorColor)

        Spacer(modifier = Modifier.height(15.dp))

        // Password
        Text(text = "Password", fontSize = 12.sp, color = Color.DarkGray)
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            placeholder = { Text("At least 6 characters", fontSize = 12.sp, color = Color.Gray) },
            textStyle = TextStyle(fontSize = 14.sp),
            isError = uiState.passwordError != null,
            visualTransformation = if (uiState.passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Image(
                        painter = painterResource(
                            id = if (uiState.passwordVisible) R.drawable.pwvisible else R.drawable.pwhide
                        ),
                        contentDescription = if (uiState.passwordVisible) "Hide password" else "Show password",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            colors = fieldColors
        )
        FieldError(uiState.passwordError, errorColor)
        FieldError(uiState.generalError, errorColor)

        // Create account button
        Button(
            onClick = onCreateAccountClick,
            enabled = !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(top = 15.dp),
            shape = RoundedCornerShape(9.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Create account",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Divider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color(0xFFDADADA))
            )
            Text(
                text = "or",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color(0xFFDADADA))
            )
        }

        // Google button
        Card(
            onClick = onGoogleClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(top = 12.dp),
            shape = RoundedCornerShape(9.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            border = BorderStroke(1.dp, primaryColor)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Continue with Google",
                    fontSize = 13.sp,
                    color = primaryColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Already have an account? ", fontSize = 11.sp, color = Color.Gray)
            Text(
                text = "Sign in",
                fontSize = 11.sp,
                color = primaryColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onSignInClick() }
            )
        }
    }
}

@Composable
private fun FieldError(error: String?, color: Color) {
    if (error != null) {
        Text(
            text = error,
            color = color,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterPreview() {
    SplitStayTheme {
        RegisterBody(uiState = RegisterModel())
    }
}