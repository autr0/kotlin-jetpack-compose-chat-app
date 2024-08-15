package com.devautro.firebasechatapp.chatsHome.presentation.screens.companionsScreen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.chatsHome.presentation.ChatsHomeViewModel
import com.devautro.firebasechatapp.core.data.model.UserData
import com.devautro.firebasechatapp.core.presentation.AutoResizedText
import com.devautro.firebasechatapp.core.presentation.SharedChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanionsScreen(
    navigateBack: () -> Unit,
    navigateToChat: () -> Unit,
    openUsersScreen: () -> Unit,
    vm: ChatsHomeViewModel = hiltViewModel(),
    sharedVM: SharedChatViewModel = viewModel(),
    bottomNavPadding: PaddingValues
) {

    val dbCompanions = vm.companionsList.collectAsStateWithLifecycle()
    val companionsList = remember { dbCompanions.value }

    val dbChats = vm.chatsList.collectAsStateWithLifecycle()
    val chatsList = remember { dbChats.value }
    val companionsInChat = chatsList.map { it.companion }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.my_companions))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navigateBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Color.White,
                            contentDescription = "back arrow"
                        )
                    }
                }
            )
        }) { innerPadding ->
        if (companionsList.isEmpty()) {
            val configuration = LocalConfiguration.current
            val isLandScape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            if (isLandScape) {
                Column(
                    modifier = Modifier
                        .consumeWindowInsets(innerPadding)
                        .padding(top = innerPadding.calculateTopPadding(),
                            bottom = bottomNavPadding.calculateBottomPadding()
                        )
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(270.dp))
                    AutoResizedText(text = stringResource(id = R.string.companions_not_found))
                    Spacer(modifier = Modifier.height(8.dp))
                    AutoResizedText(text = stringResource(id = R.string.click_below))
                    Spacer(modifier = Modifier.height(8.dp))
                    AutoResizedText(text = stringResource(id = R.string.users_section_add_companions))
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { openUsersScreen() }
                    ) {
                        Text(text = stringResource(id = R.string.open_users))
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            } else {
                Column(
                    modifier = Modifier
                        .consumeWindowInsets(innerPadding)
                        .padding(top = innerPadding.calculateTopPadding(),
                            bottom = bottomNavPadding.calculateBottomPadding())
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AutoResizedText(text = stringResource(id = R.string.companions_not_found))
                    Spacer(modifier = Modifier.height(8.dp))
                    AutoResizedText(text = stringResource(id = R.string.click_below))
                    Spacer(modifier = Modifier.height(8.dp))
                    AutoResizedText(text = stringResource(id = R.string.users_section_add_companions))
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { openUsersScreen() }
                    ) {
                        Text(text = stringResource(id = R.string.open_users))
                    }
                }
            }

        } else {
            LazyColumn(
                modifier = Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(top = innerPadding.calculateTopPadding(),
                        bottom = bottomNavPadding.calculateBottomPadding())
                    .fillMaxWidth()
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Card(
                            shape = RectangleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                contentAlignment = Alignment.TopStart
                            ) {
                                Text(
                                    text = stringResource(id = R.string.companions),
                                    color = Color.LightGray,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
                itemsIndexed(companionsList) { index, userData ->
                    CompanionCard(
                        userData = userData,
                        navigateToChat = { navigateToChat() },
                        chatExist = companionsInChat.contains(userData),
                        chatsVm = vm,
                        sharedVM = sharedVM
                    )
                    if (index != companionsList.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth(),
                            color = MaterialTheme.colorScheme.background
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompanionCard(
    userData: UserData,
    navigateToChat: () -> Unit,
    chatExist: Boolean,
    chatsVm: ChatsHomeViewModel,
    sharedVM: SharedChatViewModel = viewModel()
) {

    val messageDialogState = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 2.dp, end = 2.dp)
            .clickable {
                if (chatExist) {
                    sharedVM.updateCompanion(userData)
                    navigateToChat()
                } else {
                    messageDialogState.value = true
                }
            },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            if (userData.profilePictureUrl != null) {
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(48.dp),
                    contentScale = ContentScale.Crop,
                    model = userData.profilePictureUrl,
                    contentDescription = "User card Image"
                )
            } else {
                Icon(
                    modifier = Modifier
                        .size(66.dp),
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "User card Image",
                    tint = MaterialTheme.colorScheme.background
                )
            }

            Spacer(modifier = Modifier.width(15.dp))
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                color = MaterialTheme.colorScheme.background
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                AutoResizedText(
                    modifier = Modifier.padding(top = 8.dp),
                    text = userData.username ?: stringResource(id = R.string.unknown_user),
                    style = MaterialTheme.typography.displayLarge

                )
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "jstIcon",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(top = 6.dp, bottom = 8.dp),
                )
            }
        }
    }

    if (messageDialogState.value) {
        MessageDialog(
            dialogState = messageDialogState,
            userData = userData,
            vm = chatsVm
        )
    }

}