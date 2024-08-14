package com.devautro.firebasechatapp.users.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.core.utils.AutoResizedText
import com.devautro.firebasechatapp.sign_in.data.model.UserData
import com.devautro.firebasechatapp.ui.theme.iconPink
import com.devautro.firebasechatapp.users.presentation.UsersScreenViewModel

@Composable
fun UsersMainScreen(
    allUsersList: SnapshotStateList<UserData>,
    openChat: () -> Unit,
    vm: UsersScreenViewModel,
    bottomNavPadding: PaddingValues
) {
    if (allUsersList.isEmpty()) {
        val configuration = LocalConfiguration.current
        val isLandScape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (isLandScape) {
            Column(
                modifier = Modifier
                    .padding(bottom = bottomNavPadding.calculateBottomPadding())
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                Icon(
                    painterResource(id = R.drawable.no_wifi),
                    contentDescription = "no internet",
                    modifier = Modifier.size(66.dp),
                    tint = iconPink
                )

                AutoResizedText(
                    text = stringResource(id = R.string.users_not_found),
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                AutoResizedText(text = stringResource(id = R.string.went_wrong))
                Spacer(modifier = Modifier.height(8.dp))
                AutoResizedText(text = stringResource(id = R.string.check_internet))
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
        else {
            Column(
                modifier = Modifier
                    .padding(bottom = bottomNavPadding.calculateBottomPadding())
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painterResource(id = R.drawable.no_wifi),
                    contentDescription = "no internet",
                    modifier = Modifier.size(66.dp),
                    tint = iconPink
                )

                AutoResizedText(
                    text = stringResource(id = R.string.users_not_found),
                    style = MaterialTheme.typography.displayMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                AutoResizedText(text = stringResource(id = R.string.went_wrong))
                Spacer(modifier = Modifier.height(8.dp))
                AutoResizedText(text = stringResource(id = R.string.check_internet))
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    } else {
        LazyColumn(
            Modifier.fillMaxSize()
                .padding(bottom = bottomNavPadding.calculateBottomPadding())
        ) {
            itemsIndexed(allUsersList) { index, userData ->
                UserCardSecond(
                    userData = userData,
                    openChat = openChat,
                    vm = vm
                )
                if (index != allUsersList.lastIndex) {
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

@Composable
fun UserCardSecond(
    userData: UserData,
    openChat: () -> Unit,
    vm: UsersScreenViewModel = hiltViewModel()
) {
    val bState = vm.getButtonState(userData) // CHECK THIS OUT!

    var bText by remember { mutableStateOf(bState.bText) }
    var isEnabled by remember { mutableStateOf(bState.isEnabled) }

    val isApproved by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 2.dp, end = 2.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),

        ) {
        Row(
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (userData.profilePictureUrl != null) {
                AsyncImage(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    model = userData.profilePictureUrl,
                    contentDescription = "User card Image"
                )
            } else {
                Icon(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(start = 4.dp),
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "User card Image"
                )
            }

            AutoResizedText(
                modifier = Modifier.padding(start = 8.dp),
                text = userData.username ?: stringResource(id = R.string.unknown_user),
                style = MaterialTheme.typography.displayMedium
            )
        }

        Button(
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isApproved) {
                    MaterialTheme.colorScheme.secondary
                } else MaterialTheme.colorScheme.tertiary
            ),
            onClick = {
                if (bText == vm.context.getString(R.string.button_open_chat)) {
                    openChat()
                } else {
                    vm.sendRequest(userData)
                    isEnabled = false
                    bText = vm.context.getString(R.string.button_sent)
                }

            },
            enabled = isEnabled
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = bText,
                    color = if (isEnabled) Color.White else Color.LightGray
                )
            }
        }
    }
}