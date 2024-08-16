package com.devautro.firebasechatapp.chatScreen.presentation.screens

import android.icu.text.DateFormat
import android.icu.util.Calendar
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.chatScreen.presentation.ChatScreenViewModel
import com.devautro.firebasechatapp.chatScreen.presentation.utils.rememberScrollContext
import com.devautro.firebasechatapp.core.presentation.AutoResizedText
import com.devautro.firebasechatapp.core.presentation.viewModels.SharedChatViewModel
import com.devautro.firebasechatapp.ui.theme.blueDone
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    vm: ChatScreenViewModel = viewModel(),
    onIconCLick: () -> Unit,
    sharedVM: SharedChatViewModel = viewModel()
) {
    val companion by sharedVM.companion.collectAsStateWithLifecycle()

    val isOnlineState = vm.companionOnlineStatus.collectAsStateWithLifecycle()
    val isOnline = remember { isOnlineState.value }
    if (isOnlineState.value.isEmpty()) vm.getCompanionOnlineStatus(companion.userId ?: "")

    val dbMsgs = vm.msgsList.collectAsStateWithLifecycle()
    val messages = remember { dbMsgs.value }
    if (messages.isEmpty()) vm.getMessages(companion) // need it, to write the path name

    val firstUnreadMessage = messages
        .firstOrNull { !it.status.read && it.nameFrom != vm.chatRepo.currentUser.username }

    val dbSorted = vm.msgsDateList.collectAsStateWithLifecycle()
    val sortedMessages = remember { dbSorted.value }

    val listState = rememberLazyListState()

    val scrollContext = rememberScrollContext(listState)

    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    var previousTextFieldHeight by remember { mutableFloatStateOf(0f) }
    val textFieldSize = remember { mutableStateOf(Size.Zero) }

    val layoutInfo = remember { derivedStateOf { listState.layoutInfo } }

    LaunchedEffect(key1 = layoutInfo.value.totalItemsCount) {
        if (listState.layoutInfo.totalItemsCount > 0) {
            listState.scrollToItem(listState.layoutInfo.totalItemsCount - 1)
        }
    }

    LaunchedEffect(key1 = keyboardHeight) {
        listState.scrollBy(keyboardHeight.toFloat())
    }

    LaunchedEffect(textFieldSize.value) {
        val size = textFieldSize.value
        if (size.height > previousTextFieldHeight && scrollContext.isBottom) {
            listState.scrollBy(size.height)
        }
        previousTextFieldHeight = size.height
    }

    val clickOnSearch = remember {
        mutableStateOf(false)
    }

    val searchText by vm.searchText.collectAsStateWithLifecycle()
    val matchingMessageIndices by vm.matchingMessageIndices.collectAsStateWithLifecycle()
    val currentMatchIndex by vm.currentMatchIndex.collectAsStateWithLifecycle()
    val matchMsgsSize by vm.matchingMessageCount.collectAsStateWithLifecycle()

    val goToMessage = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(goToMessage.value, currentMatchIndex) {
        if (goToMessage.value && matchingMessageIndices.isNotEmpty()) {

            val index = matchingMessageIndices.getOrNull(currentMatchIndex)
            if (index != null) {
                listState.animateScrollToItem(index)
            }
            goToMessage.value = false

        }
    }

    BackHandler {
        if (clickOnSearch.value) {
            clickOnSearch.value = false
        } else {
            if (firstUnreadMessage != null) vm.updateMessageStatus(companion)
            vm.resetCompanionOnlineStatus() // reset online state when you left the chat screen
            vm.resetMessagesLists()
            onIconCLick()
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    var inputText by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance().time
    val time = DateFormat.getPatternInstance(DateFormat.HOUR24_MINUTE).format(calendar)
    val sdf = SimpleDateFormat("dd LLL yyyy", Locale.getDefault())
    val date = sdf.format(calendar).toString()
    val currentDateTime = LocalDateTime.now()
    // Transform current time and date into string with format ISO 8601
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val timeDate = currentDateTime.atZone(ZoneId.systemDefault()).format(formatter)

    val checkedState = remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            if (clickOnSearch.value) {
                SearchTopBar(
                    clickOnSearch,
                    searchText,
                    vm,
                    goToMessage
                ) { keyboardController?.hide() }
            } else {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (companion.profilePictureUrl != null) {
                                AsyncImage(
                                    modifier = Modifier
                                        .clip(CircleShape),
                                    model = companion.profilePictureUrl,
                                    contentDescription = "companion picture"
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.AccountCircle,
                                    contentDescription = "nullCaseImage",
                                    modifier = Modifier
                                        .clip(CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(verticalArrangement = Arrangement.Center) {
                                AutoResizedText(
                                    text = companion.username
                                        ?: stringResource(id = R.string.unknown_user),
                                    style = MaterialTheme.typography.displayMedium,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (isOnline.isNotEmpty() && isOnline[0]) {
                                    AutoResizedText(
                                        text = stringResource(id = R.string.online),
                                        style = MaterialTheme.typography.displaySmall
                                    )
                                } else {
                                    AutoResizedText(
                                        text = stringResource(id = R.string.offline),
                                        style = MaterialTheme.typography.displaySmall,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (firstUnreadMessage != null) vm.updateMessageStatus(companion)
                                keyboardController?.hide() // close the keyboard
                                vm.resetCompanionOnlineStatus() // reset online state when you left the chat screen
                                vm.resetMessagesLists()
                                onIconCLick.invoke()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                tint = Color.White,
                                contentDescription = "arrow back"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { clickOnSearch.value = true }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                tint = Color.White,
                                contentDescription = "search"
                            )
                        }
                    },
                )
            }
        },
        bottomBar = {
            if (clickOnSearch.value) {
                SearchBottomBar(matchMsgsSize, currentMatchIndex, goToMessage, vm)
            } else {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding() // new line
                        .onGloballyPositioned {
                            textFieldSize.value = Size(
                                width = it.size.width.toFloat(),
                                height = it.size.height.toFloat()
                            )
                        },
                    placeholder = { Text(text = stringResource(id = R.string.enter_msg_placeholder)) },
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = blueDone
                    ),
                    maxLines = 4,
                    trailingIcon = {
                        IconButton(
                            modifier = Modifier.padding(end = 4.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            enabled = inputText.isNotEmpty() && inputText.isNotBlank(),
                            onClick = {
                                vm.addNewMessage(
                                    userData = companion,
                                    msg = inputText,
                                    time = time,
                                    date = date,
                                    dateTime = timeDate
                                )
                                inputText = ""
                                if (firstUnreadMessage != null) vm.updateMessageStatus(companion)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "sendButton",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .consumeWindowInsets(innerPadding)
                .navigationBarsPadding()
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)),
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                sortedMessages.forEach { msgDate ->
                    stickyHeader {
                        DateHeader(date = msgDate.date)
                    }
                    itemsIndexed(
                        items = msgDate.messages,
                        key = { index, msg ->
                            "${msg.dateTime}$index"     // is it unique enough?
                        }                               // get the key from msg
                    ) { _, msg ->
                        if (firstUnreadMessage != null && msg == firstUnreadMessage) {
                            NewMessagesText()
                        }

                        if (msg.nameFrom == vm.chatRepo.currentUser.username) {
                            CurrentUserMessageBox(
                                msg = msg,
                                companionName = companion.username ?: "null",
                                checkedState = checkedState,
                                remove = {
                                    vm.deleteMessage(
                                        companion = companion,
                                        key = msg.id!!,
                                        remove = checkedState.value
                                    )
                                },
                                vm = vm
                            )
                        } else {
                            CompanionMessageBox(
                                msg = msg,
                                companionName = companion.username ?: "null",
                                checkedState = checkedState,
                                remove = {
                                    vm.deleteMessage(
                                        companion = companion,
                                        key = msg.id!!,
                                        remove = checkedState.value
                                    )
                                },
                                vm = vm
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun NewMessagesText() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(top = 10.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.new_msgs),
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isSystemInDarkTheme()) {
                        MaterialTheme.colorScheme.surfaceBright
                    } else Color.DarkGray.copy(alpha = 0.85f)
                )
                .padding(4.dp),
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}