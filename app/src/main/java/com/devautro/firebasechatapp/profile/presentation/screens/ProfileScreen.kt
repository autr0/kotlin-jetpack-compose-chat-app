package com.devautro.firebasechatapp.profile.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.profile.presentation.ProfileViewModel
import com.devautro.firebasechatapp.core.data.model.UserData
import com.devautro.firebasechatapp.core.presentation.AutoResizedText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    openChatScreen: () -> Unit,
    openCompanionsScreen: () -> Unit,
    goToRequests: () -> Unit,
    vm: ProfileViewModel = viewModel(),
    bottomNavPadding: PaddingValues
) {
    var mDisplayMenu by remember { mutableStateOf(false) }

    val sizes by vm.sizes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.Profile))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {},
                actions = {
                    IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            tint = Color.White,
                            contentDescription = "settings"
                        )
                    }
                    DropdownMenu(
                        expanded = mDisplayMenu,
                        onDismissRequest = { mDisplayMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.sign_out)) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "exitAcc"
                                )
                            },
                            onClick = {
                                onSignOut.invoke()
                                mDisplayMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .consumeWindowInsets(innerPadding)
                .padding(top = innerPadding.calculateTopPadding(),
                    bottom = bottomNavPadding.calculateBottomPadding())
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 2.dp),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    if (userData?.profilePictureUrl != null) {
                        AsyncImage(
                            model = userData.profilePictureUrl,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = "testImage",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        AutoResizedText(
                            text = userData?.username ?: stringResource(id = R.string.unknown_user),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Spacer(modifier = Modifier.height(42.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp))

                ) {
                    Spacer(modifier = Modifier.height(50.dp))
                    TableCard(
                        text = stringResource(id = R.string.Chats),
                        num = sizes["chatsSize"] ?: 0,
                        zeroText = stringResource(id = R.string.no_chats),
                        heightFraction = 0.3f,
                        onCLick = openChatScreen
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    TableCard(
                        text = stringResource(id = R.string.companions),
                        num = sizes["companionsSize"] ?: 0,
                        zeroText = stringResource(id = R.string.no_companions),
                        heightFraction = 0.428571f,
                        onCLick = openCompanionsScreen
                    )
                    Spacer(modifier = Modifier.height(112.dp))
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp))
                ) {
                    Spacer(modifier = Modifier.height(50.dp))
                    TableCard(
                        text = stringResource(id = R.string.incoming),
                        num =  sizes["incomingSize"] ?: 0,
                        zeroText = stringResource(id = R.string.no_requests),
                        heightFraction = 0.3f,
                        onCLick = { goToRequests() }
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    TableCard(
                        text = stringResource(id = R.string.outgoing),
                        num = sizes["outgoingSize"] ?: 0,
                        zeroText = stringResource(id = R.string.no_requests),
                        heightFraction = 0.428571f,
                        onCLick = { goToRequests() }
                    )
                    Spacer(modifier = Modifier.height(112.dp))
                }
            }
        }
    }
}

@Composable
fun TableCard(
    text: String,
    num: Int,
    zeroText: String = "",
    heightFraction: Float,
    onCLick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
            .clip(RoundedCornerShape(5.dp))
            .fillMaxHeight(heightFraction)
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                onCLick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Spacer(modifier = Modifier.height(12.dp))
        AutoResizedText(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        AutoResizedText(
            text = if (num > 0) num.toString() else zeroText,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}