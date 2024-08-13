package com.devautro.firebasechatapp.navigation

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.profile.ProfileViewModel
import com.devautro.firebasechatapp.profile.screens.ExitDialog
import com.devautro.firebasechatapp.profile.screens.ProfileScreen
import com.devautro.firebasechatapp.sign_in.SignInScreen
import com.devautro.firebasechatapp.sign_in.SignInViewModel
import com.devautro.firebasechatapp.sign_in.model.GoogleAuthUIClient
import kotlinx.coroutines.launch

@Composable
fun MainFunction(
    googleAuthUIClient: GoogleAuthUIClient,
    context: Context,
    lifecycleScope: LifecycleCoroutineScope,
    profileVm: ProfileViewModel
) {
    val profileTab = TabBarItem(
        title = stringResource(id = R.string.Profile),
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
    val chatTab = TabBarItem(
        title = stringResource(id = R.string.Chats),
        selectedIcon = Icons.Filled.Email,
        unselectedIcon = Icons.Outlined.Email,
//                badgeAmount = 7 // pass num of the new msgs here!
    )
    val usersTab = TabBarItem(
        title = stringResource(id = R.string.Users),
        selectedIcon = Icons.Filled.Face,
        unselectedIcon = Icons.Outlined.Face
    )

    val tabBarItems = listOf(profileTab, chatTab, usersTab)

    val navController = rememberNavController()
    var showBottomBar by rememberSaveable { mutableStateOf(true) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    showBottomBar = when (navBackStackEntry?.destination?.route) {
        "chatScreen" -> false
        "sign_in" -> false
        "companionsList" -> false
        else -> true
    }

    val dialogState = remember {
        mutableStateOf(false)
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                TabView(
                    tabBarItems = tabBarItems,
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { bottomNavPadding ->
        val lifecycleOwner = LocalLifecycleOwner.current
        CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {

            NavHost(
                navController = navController,
                startDestination = if (googleAuthUIClient.getSignedInUser() != null) {
                    profileTab.title
                } else "sign_in"
            ) {
                composable("sign_in") {
                    val viewModel = viewModel<SignInViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartIntentSenderForResult(),
                        onResult = { result ->
                            if (result.resultCode == RESULT_OK) {
                                lifecycleScope.launch {
                                    val signInResult =
                                        googleAuthUIClient.signInWithIntent(
                                            intent = result.data ?: return@launch
                                        )
                                    viewModel.onSignInResult(signInResult)
                                }
                            }
                        }
                    )
                    LaunchedEffect(key1 = state.isSignInSuccessful) {
                        if (state.isSignInSuccessful) {
                            Toast.makeText(
                                context,
                                R.string.sign_in_success,
                                Toast.LENGTH_SHORT
                            ).show()

                            navController.navigate(profileTab.title)
                            viewModel.resetState()
                        }
                    }
                    SignInScreen(
                        state = state,
                        onSignInClick = {
                            lifecycleScope.launch {
                                val signInIntentSender = googleAuthUIClient.sighIn()
                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        signInIntentSender ?: return@launch
                                    ).build()
                                )
                            }
                        }
                    )
                }

                composable(profileTab.title) {
                    val activity = (LocalContext.current as? Activity)

                    ProfileScreen(
                        userData = googleAuthUIClient.getSignedInUser(),
                        onSignOut = {
                            dialogState.value = true
                        },
                        openChatScreen = {
                            navController.navigate(chatTab.title) {
                                popUpTo(chatTab.title)
                            }
                        },
                        openCompanionsScreen = {
                            navController.navigate("companionsList") {
                                popUpTo("companionsList")
                            }
                        },
                        goToRequests = {
                            navController.navigate(usersTab.title) {
                                popUpTo(usersTab.title)
                            }
                        },
                        vm = profileVm,
                        bottomNavPadding = bottomNavPadding
                    )
                    if (dialogState.value) {
                        ExitDialog(
                            dialogState = dialogState,
                            onSignOut = {
                                lifecycleScope.launch {
                                    googleAuthUIClient.signOut()
                                    Toast.makeText(
                                        context,
                                        R.string.sign_out_success,
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("sign_in") {
                                        popUpTo("sign_in")
                                    }

                                }
                                dialogState.value = false
                            }
                        )
                    }

                    BackHandler {
                        activity?.finish()
                    }
                }

            }
        }

    }
}