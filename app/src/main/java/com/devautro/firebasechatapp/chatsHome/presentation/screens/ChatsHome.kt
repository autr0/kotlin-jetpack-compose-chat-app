package com.devautro.firebasechatapp.chatsHome.presentation.screens

import android.content.res.Configuration
import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.chatsHome.data.model.ChatStatus
import com.devautro.firebasechatapp.chatsHome.presentation.ChatsHomeViewModel
import com.devautro.firebasechatapp.core.data.timeDateToLocalTimeZone
import com.devautro.firebasechatapp.core.presentation.AutoResizedText
import com.devautro.firebasechatapp.core.presentation.SharedChatViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsHome(
    onWindowClick: () -> Unit,
    onFloatingButtonClick: () -> Unit,
    vm: ChatsHomeViewModel = hiltViewModel(),
    sharedVM: SharedChatViewModel = viewModel(),
    bottomNavPadding: PaddingValues
) {

    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
    val currentDateTime = LocalDateTime.now()

    val dbChats = vm.chatsList.collectAsStateWithLifecycle()
    val chatsList = remember { dbChats.value }

    val sortedChatsList = chatsList.sortedByDescending { chatStatus ->
        timeDateToLocalTimeZone(
            chatStatus.lastMessage?.dateTime ?: currentDateTime.format(dateTimeFormatter)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.my_chats))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 90.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                onClick = {
                    onFloatingButtonClick()
                },
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        if (chatsList.isEmpty()) {
            val configuration = LocalConfiguration.current
            val isLandScape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            if (isLandScape) {
                Column(
                    modifier = Modifier
                        .consumeWindowInsets(innerPadding)
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            bottom = bottomNavPadding.calculateBottomPadding()
                        )
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(250.dp))
                    AutoResizedText(text = stringResource(id = R.string.chats_not_found))
                    Spacer(modifier = Modifier.height(8.dp))
                    AutoResizedText(text = stringResource(id = R.string.click_on_floating))
                    Spacer(modifier = Modifier.height(8.dp))
                    AutoResizedText(text = stringResource(id = R.string.new_chat_with_smile))
                    Spacer(modifier = Modifier.height(16.dp))
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
                    AutoResizedText(text = stringResource(id = R.string.chats_not_found))
                    Spacer(modifier = Modifier.height(8.dp))
                    AutoResizedText(text = stringResource(id = R.string.click_on_floating))
                    Spacer(modifier = Modifier.height(8.dp))
                    AutoResizedText(text = stringResource(id = R.string.new_chat_with_smile))
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(top = innerPadding.calculateTopPadding(),
                        bottom = bottomNavPadding.calculateBottomPadding())
                    .fillMaxHeight()
            ) {
                itemsIndexed(
                    sortedChatsList
                ) { _, chatStatus ->
                    if (chatStatus.companion != null) {
                        ChatWindow(
                            chatStatus = chatStatus,
                            onClick = { onWindowClick() },
                            updateLastMessage = { vm.updateLastMessage(chatStatus.companion) },
                            sharedVM = sharedVM
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatWindow(
    chatStatus: ChatStatus,
    onClick: () -> Unit,
    updateLastMessage: () -> Unit,
    sharedVM: SharedChatViewModel = viewModel()
) {
    LaunchedEffect(key1 = Unit) {
        updateLastMessage.invoke()
    }

    val calendar = Calendar.getInstance().time
    val sdf = SimpleDateFormat("dd LLL yyyy")
    val today = sdf.format(calendar).toString()

    val userData = chatStatus.companion!!
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 2.dp, end = 2.dp)
            .clickable {
                sharedVM.updateCompanion(userData)
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (chatStatus.lastMessage!!.status.read ||
                chatStatus.lastMessage.nameFrom == chatStatus.currentUser!!.username
            ) {
                MaterialTheme.colorScheme.primary
            } else MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(15.dp),

        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (userData.profilePictureUrl != null) {
                AsyncImage(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clip(CircleShape)
                        .size(48.dp),
                    contentScale = ContentScale.Crop,
                    model = userData.profilePictureUrl,
                    contentDescription = "Chats card Image"
                )
            } else {
                Icon(
                    modifier = Modifier
                        .size(66.dp)
                        .padding(start = 16.dp),
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "Chat Image"
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        contentAlignment = Alignment.TopStart
                    ) {
                        AutoResizedText(
                            text = userData.username ?: stringResource(id = R.string.unknown_user),
                            modifier = Modifier
                                .padding(start = 10.dp, end = 7.dp, top = 5.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Text(
                            modifier = Modifier.padding(end = 10.dp, top = 5.dp),
                            fontSize = 16.sp,
                            color = Color.LightGray,
                            text = if (chatStatus.lastMessage.date == today) {
                                chatStatus.lastMessage.timeSent ?: stringResource(id = R.string.def_time)
                            } else {
                                chatStatus.lastMessage.date ?: stringResource(id = R.string.def_date)
                            }
                        )
                    }

                }

                Box {
                    Text(
                        modifier = Modifier
                            .padding(start = 10.dp, end = 7.dp, bottom = 7.dp, top = 7.dp),
                        color = Color.LightGray,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        text = chatStatus.lastMessage.message ?: stringResource(id = R.string.smthng_wrong)
                    )
                }
            }
        }
    }
}