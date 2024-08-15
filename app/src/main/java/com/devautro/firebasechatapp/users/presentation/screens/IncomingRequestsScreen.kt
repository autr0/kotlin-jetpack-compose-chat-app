package com.devautro.firebasechatapp.users.presentation.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.core.presentation.AutoResizedText
import com.devautro.firebasechatapp.core.data.model.UserData
import com.devautro.firebasechatapp.ui.theme.greenButton
import com.devautro.firebasechatapp.ui.theme.redButton
import com.devautro.firebasechatapp.users.data.model.Request
import com.devautro.firebasechatapp.users.presentation.UsersScreenViewModel

@Composable
fun IncomingRequestsScreen(
    incomingRequestsList: List<Request>,
    openUsers: () -> Unit,
    vm: UsersScreenViewModel = hiltViewModel(),
    bottomNavPadding: PaddingValues
) {

    if (incomingRequestsList.isEmpty()) {
        val configuration = LocalConfiguration.current
        val isLandScape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (isLandScape) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = bottomNavPadding.calculateBottomPadding())
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                AutoResizedText(text = stringResource(id = R.string.requests_not_found))
                Spacer(modifier = Modifier.height(8.dp))
                AutoResizedText(text = stringResource(id = R.string.click_below))
                Spacer(modifier = Modifier.height(8.dp))
                AutoResizedText(text = stringResource(id = R.string.users_section_add_companions))
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { openUsers() }
                ) {
                    Text(text = stringResource(id = R.string.open_users))
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(bottom = bottomNavPadding.calculateBottomPadding())
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AutoResizedText(text = stringResource(id = R.string.requests_not_found))
                Spacer(modifier = Modifier.height(8.dp))
                AutoResizedText(text = stringResource(id = R.string.click_below))
                Spacer(modifier = Modifier.height(8.dp))
                AutoResizedText(text = stringResource(id = R.string.users_section_add_companions))
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { openUsers() }
                ) {
                    Text(text = stringResource(id = R.string.open_users))
                }
            }
        }
    } else {
        LazyColumn(
            Modifier.fillMaxSize().padding(bottom = bottomNavPadding.calculateBottomPadding())
        ) {
            itemsIndexed(incomingRequestsList) { index, request ->
                if (request.from != null) {
                    IncomingRequestsCard(
                        userData = request.from,
                        removeItem = {
                            vm.removeIncomingRequest(request)
                        }
                    )
                }
                if (index != incomingRequestsList.lastIndex) {
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
fun IncomingRequestsCard(
    userData: UserData,
    removeItem: () -> Unit,
    vm: UsersScreenViewModel = hiltViewModel()
) {
    var visible by remember { mutableStateOf(true) }

    AnimatedVisibility(visible = visible) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 2.dp, end = 2.dp)
                .clickable { /*TODO*/ },
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
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
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp, bottom = 2.dp)
                        .weight(0.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = greenButton,
                        contentColor = Color.White
                    ),
                    onClick = {
                        vm.updateIncomingRequests(
                            userFrom = userData,
                            isApproved = true
                        )
                        removeItem()
                        visible = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "add Icon"
                    )

                }
                Button(
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp, bottom = 2.dp)
                        .weight(0.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = redButton,
                        contentColor = Color.White
                    ),
                    onClick = {
                        vm.updateIncomingRequests(
                            userFrom = userData,
                            isApproved = false
                        )
                        removeItem()
                        visible = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "reject Icon"
                    )
                }
            }
        }
    }
}