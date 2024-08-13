package com.devautro.firebasechatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.devautro.firebasechatapp.navigation.MainFunction
import com.devautro.firebasechatapp.profile.ProfileViewModel
import com.devautro.firebasechatapp.sign_in.model.GoogleAuthUIClient
import com.devautro.firebasechatapp.ui.theme.FirebaseChatAppTheme
import com.google.android.gms.auth.api.identity.Identity

class MainActivity : ComponentActivity() {

    private val googleAuthUIClient by lazy {
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private val profileVm: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FirebaseChatAppTheme {
                MainFunction(
                    googleAuthUIClient = googleAuthUIClient,
                    context = this@MainActivity,
                    lifecycleScope = lifecycleScope,
                    profileVm = profileVm
                )
            }
        }

    }
}

