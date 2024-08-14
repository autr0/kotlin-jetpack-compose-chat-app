package com.devautro.firebasechatapp.sign_in.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.sign_in.data.model.SignInState
import com.devautro.firebasechatapp.ui.theme.darkThemeSignInButtonColor
import com.devautro.firebasechatapp.ui.theme.signInButtonStrokeDark
import com.devautro.firebasechatapp.ui.theme.signInButtonStrokeLight

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier
                .height(48.dp)
                .padding(horizontal = 12.dp),
            border = BorderStroke(
                1.dp,
                if (isSystemInDarkTheme()) {
                    signInButtonStrokeDark
                } else signInButtonStrokeLight
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) {
                    darkThemeSignInButtonColor
                } else Color.White,
                contentColor = if (isSystemInDarkTheme()) {
                    Color.White
                } else Color.Black
            ),
            onClick = {
                onSignInClick.invoke()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google_icon),
                contentDescription = "googleIcon",
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = stringResource(id = R.string.sign_in_google))
        }
    }
}