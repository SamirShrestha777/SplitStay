package com.softwarica.splitstay.view.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softwarica.splitstay.R
import com.softwarica.splitstay.navigation.AppNavigator
import com.softwarica.splitstay.ui.theme.SplitStayTheme
import com.softwarica.splitstay.view.register.AppLogo

class WelcomeUIActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SplitStayTheme {
                WelcomeBody(
                    onGetStartedClick = { AppNavigator.toRegister(this) },
                    onSignInClick = { AppNavigator.toLogin(this) }
                )
            }
        }
    }
}

@Composable
fun WelcomeBody(
    onGetStartedClick: () -> Unit = {},
    onSignInClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFFEF9F0)
    val primaryColor = Color(0xFF0F625C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppLogo(size = 40.dp)
        }

        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Shared costs, sorted.",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF183B38)
        )

        Text(
            text = "Track rent, groceries and bills with your\nhousemates.",
            fontSize = 12.sp,
            color = Color.Gray,
            lineHeight = 17.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.welcometoapp),
            contentDescription = "Housemates sharing expenses",
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .padding(top = 35.dp)
        )

        Spacer(modifier = Modifier.height(45.dp))

        // Get started -> Register screen
        Button(
            onClick = onGetStartedClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(9.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor
            )
        ) {
            Text(
                text = "Get started",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Sign in -> Login screen
        Button(
            onClick = onSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(top = 6.dp),
            shape = RoundedCornerShape(9.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor
            ),
            border = BorderStroke(
                1.dp, primaryColor
            )
        ) {
            Text(
                text = "Sign in",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = primaryColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google logo",
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = "Continue with Google",
                fontSize = 12.sp,
                color = primaryColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            text = "By continuing, you agree to our Terms and Privacy Policy.",
            fontSize = 8.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 10.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomePreview() {
    SplitStayTheme {
        WelcomeBody()
    }
}