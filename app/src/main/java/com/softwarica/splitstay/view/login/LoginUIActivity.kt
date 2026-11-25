package com.softwarica.splitstay.view.login

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.softwarica.splitstay.R
import com.softwarica.splitstay.navigation.AppNavigator
import com.softwarica.splitstay.ui.theme.SplitStayTheme
import com.softwarica.splitstay.view.register.AppLogo
import com.softwarica.splitstay.viewmodel.login.LoginUiState
import com.softwarica.splitstay.viewmodel.login.LoginViewModel

class LoginUIActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitStayTheme {
                LoginScreen(
                    // Back icon -> Welcome
                    onBackClick = { AppNavigator.toWelcome(this, finishCurrent = true) },
                    // "Create account" -> Register
                    onCreateAccountClick = { AppNavigator.toRegister(this, finishCurrent = true) },
                    // Login worked
                    onLoginSuccess = {
                        // TODO: navigate to your home screen, e.g.
                        // AppNavigator.toHome(this, finishCurrent = true)
                    }
                )
            }
        }
    }
}

/**
 * Stateful wrapper: connects the ViewModel to the UI.
 */
@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
            viewModel.onLoginSuccessHandled()
        }
    }

    LoginBody(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onSignInClick = viewModel::onSignInClick,
        onBackClick = onBackClick,
        onCreateAccountClick = onCreateAccountClick
    )
}

/**
 * Stateless UI: draws whatever LoginUiState says. No logic in here.
 */
@Composable
fun LoginBody(
    state: LoginUiState,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onTogglePasswordVisibility: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFFEF9F0)
    val primaryColor = Color(0xFF0F625C)
    val errorColor = Color(0xFFC62828)

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        errorContainerColor = Color.White,
        focusedBorderColor = primaryColor,
        unfocusedBorderColor = Color(0xFFD8D8D8),
        errorBorderColor = errorColor,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        errorTextColor = Color.Black,
        errorCursorColor = errorColor,
        errorSupportingTextColor = errorColor
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

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
            text = "WELCOME BACK",
            fontSize = 12.sp,
            color = primaryColor,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Text(
            text = "Sign in to SplitStay",
            fontSize = 22.sp,
            color = Color(0xFF163B38),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = "Your household is waiting for you.",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 5.dp)
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Email field
        Text(text = "Email address", fontSize = 12.sp, color = Color.DarkGray)
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            placeholder = { Text("you@example.com", fontSize = 12.sp, color = Color.Gray) },
            textStyle = TextStyle(fontSize = 14.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            isError = state.isEmailError,
            supportingText = {
                state.emailError?.let {
                    Text(text = it, fontSize = 12.sp, color = errorColor)
                }
            },
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Password field
        Text(text = "Password", fontSize = 12.sp, color = Color.DarkGray)
        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            placeholder = { Text("At least 6 characters", fontSize = 12.sp, color = Color.Gray) },
            textStyle = TextStyle(fontSize = 14.sp),
            visualTransformation = if (state.passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            isError = state.isPasswordError,
            supportingText = {
                (state.passwordError ?: state.authError)?.let {
                    Text(text = it, fontSize = 12.sp, color = errorColor)
                }
            },
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    if (state.passwordVisible) {
                        Image(
                            painter = painterResource(id = R.drawable.pwvisible),
                            contentDescription = "Hide password",
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.pwhide),
                            contentDescription = "Show password",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            colors = fieldColors
        )

        Text(
            text = "Forgot password?",
            fontSize = 12.sp,
            color = primaryColor,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )

        Button(
            onClick = onSignInClick,
            enabled = !state.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(top = 15.dp),
            shape = RoundedCornerShape(9.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                disabledContainerColor = primaryColor.copy(alpha = 0.6f)
            )
        ) {
            Text(
                text = if (state.isLoading) "Signing in..." else "Sign in",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

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

        // Google button (UI only)
        Card(
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "New to SplitStay? ", fontSize = 11.sp, color = Color.Gray)
            Text(
                text = "Create account",
                fontSize = 11.sp,
                color = primaryColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onCreateAccountClick() }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    SplitStayTheme {
        LoginBody(state = LoginUiState())
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginErrorPreview() {
    SplitStayTheme {
        LoginBody(
            state = LoginUiState(
                email = "aayush@example.com",
                password = "wrongpass",
                authError = "Incorrect email or password."
            )
        )
    }
}