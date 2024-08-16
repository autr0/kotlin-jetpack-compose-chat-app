package com.devautro.firebasechatapp.chatScreen.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.chatScreen.presentation.ChatScreenViewModel
import com.devautro.firebasechatapp.ui.theme.blueDone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    openState: MutableState<Boolean>,
    textState: String,
    vm: ChatScreenViewModel,
    goToMessage: MutableState<Boolean>,
    closeKeyboard: () -> Unit
) {

    TopAppBar(
        title = {
            TextField(
                value = textState,
                onValueChange = { vm.updateSearchText(it) },
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(id = R.string.search), color = Color.LightGray)
                },
                colors = TextFieldDefaults.colors(
                    focusedLabelColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = blueDone
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        closeKeyboard()
                        vm.searchMessages()
                        goToMessage.value = true // when click on search tab --> scroll to msg
                    }
                )
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { openState.value = false }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = Color.White,
                    contentDescription = "arrow back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White
        ),
        actions = {
            AnimatedVisibility(visible = textState.isNotEmpty()) {
                IconButton(onClick = { vm.resetSearchText() }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        tint = Color.White,
                        contentDescription = "clear textField"
                    )
                }
            }
        }
    )
}

@Composable
fun SearchBottomBar(
    searchSize: Int,
    nextMatchIndex: Int,
    startScroll: MutableState<Boolean>,
    vm: ChatScreenViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .imePadding()
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(visible = searchSize > 0) {
            Row(
                modifier = Modifier.fillMaxWidth(0.5f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${nextMatchIndex + 1} ${stringResource(id = R.string.out_of)} $searchSize",
                    color = Color.White
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                modifier = Modifier.padding(end = 4.dp),
                onClick = {
                    vm.goToNextMatch()
                    startScroll.value = true
                },
                enabled = nextMatchIndex != searchSize - 1 && searchSize > 0
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "up",
                    tint = if (nextMatchIndex != searchSize - 1 && searchSize > 0) Color.White else Color.Gray
                )
            }

            IconButton(
                modifier = Modifier.padding(end = 4.dp),
                onClick = {
                    vm.goToPreviousMatch()
                    startScroll.value = true
                },
                enabled = nextMatchIndex != 0 && searchSize > 0
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "down",
                    tint = if (nextMatchIndex != 0 && searchSize > 0) {
                        Color.White
                    } else Color.Gray
                )
            }
        }
    }
}