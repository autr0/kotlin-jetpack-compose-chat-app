package com.devautro.firebasechatapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.devautro.firebasechatapp.chatScreen.presentation.ChatScreenViewModel
import com.devautro.firebasechatapp.chatsHome.presentation.ChatsHomeViewModel
import com.devautro.firebasechatapp.core.presentation.viewModels.SharedChatViewModel
import com.devautro.firebasechatapp.core.presentation.viewModels.OnlineTrackerViewModel
import com.devautro.firebasechatapp.navigation.presentation.MainFunction
import com.devautro.firebasechatapp.profile.presentation.ProfileViewModel
import com.devautro.firebasechatapp.sign_in.data.GoogleAuthUIClient
import com.devautro.firebasechatapp.ui.theme.FirebaseChatAppTheme
import com.devautro.firebasechatapp.users.presentation.UsersScreenViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val googleAuthUIClient by lazy {
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private val profileVm: ProfileViewModel by viewModels()
    private val usersVm: UsersScreenViewModel by viewModels()
    private val chatsHomeVm: ChatsHomeViewModel by viewModels()
    private val sharedVm: SharedChatViewModel by viewModels()
    private val chatScreenVm: ChatScreenViewModel by viewModels()
    private val onlineTrackVm: OnlineTrackerViewModel by viewModels()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//      line below + 'adjustResize' in Manifest = good ime behavior
        WindowCompat.setDecorFitsSystemWindows(window, false)

//      for user's online information if he is signed in only
        if (googleAuthUIClient.getSignedInUser() != null) {
            onlineTrackVm.updateOnlineStatus(true)
        }

        setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionState = rememberPermissionState(
                    permission = Manifest.permission.POST_NOTIFICATIONS
                )

                LaunchedEffect(key1 = true) {
                    if (!permissionState.status.isGranted) {
                        permissionState.launchPermissionRequest()
                    }
                }

            }

            FirebaseChatAppTheme {
                MainFunction(
                    googleAuthUIClient = googleAuthUIClient,
                    context = this@MainActivity,
                    lifecycleScope = lifecycleScope,
                    profileVm = profileVm,
                    usersVm = usersVm,
                    chatsHomeVm = chatsHomeVm,
                    sharedVm = sharedVm,
                    chatScreenVm = chatScreenVm,
                    onlineTrackVm = onlineTrackVm
                )
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (googleAuthUIClient.getSignedInUser() != null) {
            onlineTrackVm.updateOnlineStatus(true)
        }
    }

    override fun onStop() {
        super.onStop()

        if (googleAuthUIClient.getSignedInUser() != null) {
            onlineTrackVm.updateOnlineStatus(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (googleAuthUIClient.getSignedInUser() != null) {
            onlineTrackVm.updateOnlineStatus(false)
        }
    }

}

