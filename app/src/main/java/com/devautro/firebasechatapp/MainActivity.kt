package com.devautro.firebasechatapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.devautro.firebasechatapp.chatScreen.presentation.ChatScreenViewModel
import com.devautro.firebasechatapp.chatsHome.presentation.ChatsHomeViewModel
import com.devautro.firebasechatapp.core.presentation.SharedChatViewModel
import com.devautro.firebasechatapp.navigation.presentation.MainFunction
import com.devautro.firebasechatapp.profile.presentation.ProfileViewModel
import com.devautro.firebasechatapp.sign_in.data.GoogleAuthUIClient
import com.devautro.firebasechatapp.ui.theme.FirebaseChatAppTheme
import com.devautro.firebasechatapp.users.presentation.UsersScreenViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val googleAuthUIClient by lazy {
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

//  this one to update currentUserData after re-sign in
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        if (user != null) {
            profileVm.resetProfileVmState()
            usersVm.resetUsersState()
            chatsHomeVm.resetChatsHomeVmState()
            sharedVm.resetSharedVmState()
            chatScreenVm.resetChatState()
            Log.d("AuthCheckState", "Hello from authStateListener!")
        }
    }

    private val profileVm: ProfileViewModel by viewModels()
    private val usersVm: UsersScreenViewModel by viewModels()
    private val chatsHomeVm: ChatsHomeViewModel by viewModels()
    private val sharedVm: SharedChatViewModel by viewModels()
    private val chatScreenVm: ChatScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//      line below + 'adjustResize' in Manifest = good ime behavior
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            FirebaseChatAppTheme {
                MainFunction(
                    googleAuthUIClient = googleAuthUIClient,
                    context = this@MainActivity,
                    lifecycleScope = lifecycleScope,
                    profileVm = profileVm,
                    usersVm = usersVm,
                    chatsHomeVm = chatsHomeVm,
                    sharedVM = sharedVm,
                    chatScreenVM = chatScreenVm
                )
            }
        }

    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }

}

