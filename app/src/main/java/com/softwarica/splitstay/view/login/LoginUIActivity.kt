package com.softwarica.splitstay.view.login

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softwarica.splitstay.R
import com.softwarica.splitstay.navigation.AppNavigator
import com.softwarica.splitstay.ui.theme.SplitStayTheme
import com.softwarica.splitstay.view.register.AppLogo

object SupabaseAuth {
    private const val SUPABASE_URL = "https://igdvaxdocuboppoayrzo.supabase.co"
    private const val SUPABASE_ANON_KEY = "sb_publishable_NSQf-rp6nIaUNm7-yzglUg_DfIq1fOy"

    sealed class Result {
        data class Success(val accessToken: String, val userId: String) : Result()
        data class Failure(val message: String) : Result()
    }

    suspend fun signIn(email: String, password: String): Result = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            conn = (URL("$SUPABASE_URL/auth/v1/token?grant_type=password")
                .openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15000
                readTimeout = 15000
                doOutput = true
                setRequestProperty("apikey", SUPABASE_ANON_KEY)
                setRequestProperty("Content-Type", "application/json")
            }
            val body = JSONObject()
                .put("email", email.trim())
                .put("password", password)
                .toString()
            conn.outputStream.use { it.write(body.toByteArray()) }

            val code = conn.responseCode
            val text = (if (code in 200..299) conn.inputStream else conn.errorStream)
                ?.bufferedReader()?.use { it.readText() }.orEmpty()
            val json = if (text.isNotBlank()) JSONObject(text) else JSONObject()

            if (code in 200..299) {
                Result.Success(
                    accessToken = json.optString("access_token"),
                    userId = json.optJSONObject("user")?.optString("id").orEmpty()
                )
            } else {
                val errorCode = json.optString("error_code")
                Result.Failure(
                    when (errorCode) {
                        "email_not_confirmed" -> "Please confirm your email first."
                        "invalid_credentials" -> "Incorrect email or password."
                        else -> json.optString("msg")
                            .ifBlank { json.optString("error_description") }
                            .ifBlank { "Incorrect email or password." }
                    }
                )
            }
        } catch (e: Exception) {
            Log.e("SupabaseAuth", "Sign-in failed", e)
            Result.Failure(
                when {
                    SUPABASE_URL.contains("YOUR-PROJECT-ID") ->
                        "Set your Supabase URL and anon key in SupabaseAuth."
                    e is SecurityException ->
                        "Add the INTERNET permission to AndroidManifest.xml."
                    e is UnknownHostException ->
                        "Can't find the server. Check the Supabase URL and your internet."
                    e is SocketTimeoutException ->
                        "The request timed out. Please try again."
                    e is JSONException ->
                        "Unexpected response from the server. Check the Supabase URL."
                    else -> "Sign-in failed: ${e.javaClass.simpleName}"
                }
            )
        } finally {
            conn?.disconnect()
        }
    }
}

class LoginUIActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val scope = rememberCoroutineScope()
            SplitStayTheme {
                LoginBody(
                    // Back icon -> Welcome
                    onBackClick = { AppNavigator.toWelcome(this, finishCurrent = true) },
                    // "Create account" -> Register
                    onCreateAccountClick = { AppNavigator.toRegister(this, finishCurrent = true) },
                    // Called only after local validation passes.
                    // Hook up your real auth here and call onInvalidCredentials()
                    // when the server says the email/password is wrong.
                    onSignInClick = { email, password, onFailure, onSuccess ->
                        scope.launch {
                            when (val result = SupabaseAuth.signIn(email, password)) {
                                is SupabaseAuth.Result.Success -> {
                                    onSuccess()
                                    Toast.makeText(
                                        this@LoginUIActivity,
                                        "Login successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // TODO: navigate to your home screen here, e.g.
                                    // AppNavigator.toHome(this@LoginUIActivity, finishCurrent = true)
                                }
                                is SupabaseAuth.Result.Failure -> onFailure(result.message)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LoginBody(
    onBackClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
    onSignInClick: (
        email: String,
        password: String,
        onFailure: (message: String) -> Unit,
        onSuccess: () -> Unit
    ) -> Unit = { _, _, _, _ -> }
) {
    var authFailed by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Error messages (null = no error)
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

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

    fun validateAndSubmit() {
        val trimmedEmail = email.trim()

        emailError = when {
            trimmedEmail.isEmpty() -> "Email is required."
            !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() ->
                "Enter a valid email address."
            else -> null
        }

        passwordError = when {
            password.isEmpty() -> "Password is required."
            password.length < 6 -> "Password must be at least 6 characters."
            else -> null
        }

        if (emailError == null && passwordError == null && !isLoading) {
            isLoading = true
            onSignInClick(
                trimmedEmail,
                password,
                { message ->
                    // Server rejected it: turn both fields red
                    isLoading = false
                    authFailed = true
                    emailError = " " // keeps the field red without a duplicate message
                    passwordError = message
                },
                { isLoading = false }
            )
        }
    }

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
            value = email,
            onValueChange = {
                email = it
                emailError = null
                // Clear the shared server error too
                if (authFailed) {
                    authFailed = false
                    passwordError = null
                }
            },
            placeholder = { Text("you@example.com", fontSize = 12.sp, color = Color.Gray) },
            textStyle = TextStyle(fontSize = 14.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            isError = emailError != null,
            supportingText = {
                val msg = emailError
                if (!msg.isNullOrBlank()) {
                    Text(text = msg, fontSize = 12.sp, color = errorColor)
                }
            },
            colors = fieldColors
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Password", fontSize = 12.sp, color = Color.DarkGray)
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
                if (authFailed) {
                    authFailed = false
                    emailError = null
                }
            },
            placeholder = { Text("At least 6 characters", fontSize = 12.sp, color = Color.Gray) },
            textStyle = TextStyle(fontSize = 14.sp),
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            isError = passwordError != null,
            supportingText = {
                val msg = passwordError
                if (!msg.isNullOrBlank()) {
                    Text(text = msg, fontSize = 12.sp, color = errorColor)
                }
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    if (passwordVisible) {
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
            onClick = { validateAndSubmit() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(top = 15.dp),
            shape = RoundedCornerShape(9.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text(
                text = if (isLoading) "Signing in..." else "Sign in",
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
        LoginBody()
    }
}