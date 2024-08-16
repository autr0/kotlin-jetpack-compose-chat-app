package com.devautro.firebasechatapp.chatScreen.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.core.data.model.Message
import com.devautro.firebasechatapp.ui.theme.blueDone

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CurrentUserMessageBox(
    msg: Message,
    companionName: String,
    checkedState: MutableState<Boolean>,
    remove: () -> Unit
) {
    val deleteState = remember {
        mutableStateOf(false)
    }

    if (deleteState.value) {
        DeleteDialog(
            deleteState = deleteState,
            companionName = companionName,
            checkedState = checkedState,
            removeMessage = remove
        )
    }

    val openState = remember {
        mutableStateOf(false)
    }

    if (openState.value) {
        MessageEditDialog(
            openState = openState,
            msg = msg.message ?: stringResource(id = R.string.smthng_wrong),
            drawEditField = true,
            deleteState = deleteState
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Card(
            modifier = Modifier
                .padding(
                    start = 60.dp,
                    end = 10.dp,
                    top = 1.dp,
                    bottom = 3.dp
                )
                .clickable {
                    openState.value = true
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(15.dp),

            ) {

            Text(
                modifier = Modifier
                    .padding(start = 10.dp, end = 7.dp, top = 5.dp),
                color = Color.LightGray,
                text = msg.nameFrom ?: stringResource(id = R.string.unknown_user),
                fontSize = 14.sp
            )
            FlowRow(
                modifier = Modifier
                    .padding(
                        start = 10.dp, end = 7.dp,
                        bottom = 5.dp
                    ),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    color = Color.White,
                    text = msg.message ?: stringResource(id = R.string.smthng_wrong)
                )
                Row(
                    modifier = Modifier.align(Alignment.Bottom),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        color = Color.LightGray,
                        text = msg.timeSent ?: stringResource(id = R.string.def_time),
                        modifier = Modifier
                            .padding(start = 7.dp, top = 2.dp),
                        textAlign = TextAlign.End,
                        fontSize = 14.sp
                    )

                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(start = 4.dp),
                        painter = painterResource(
                            id = when {
                                msg.status.pending -> {
                                    R.drawable.access_time_icon
                                }

                                msg.status.received -> {
                                    R.drawable.done_icon
                                }

                                else -> R.drawable.done_all_icon
                            }
                        ),
                        tint = if (msg.status.read) blueDone
                        else Color.White,
                        contentDescription = "messageStatus"
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompanionMessageBox(
    msg: Message,
    companionName: String,
    checkedState: MutableState<Boolean>,
    remove: () -> Unit
) {
    val deleteState = remember {
        mutableStateOf(false)
    }

    if (deleteState.value) {
        DeleteDialog(
            deleteState = deleteState,
            companionName = companionName,
            checkedState = checkedState,
            removeMessage = remove
        )
    }

    val openState = remember {
        mutableStateOf(false)
    }

    if (openState.value) {
        MessageEditDialog(
            openState = openState,
            msg = msg.message ?: stringResource(id = R.string.smthng_wrong),
            drawEditField = false,
            deleteState = deleteState
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Card(
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    end = 60.dp,
                    top = 1.dp,
                    bottom = 3.dp
                )
                .clickable {
                    openState.value = true
                },
            colors = CardDefaults.cardColors(
                containerColor = if (isSystemInDarkTheme()) {
                    MaterialTheme.colorScheme.surfaceBright
                } else Color.DarkGray.copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(15.dp),

            ) {

            Text(
                modifier = Modifier
                    .padding(start = 10.dp, end = 7.dp, top = 5.dp),
                color = Color.LightGray,
                text = msg.nameFrom ?: stringResource(id = R.string.unknown_user),
                fontSize = 14.sp
            )
            FlowRow(
                modifier = Modifier.padding(
                    start = 10.dp, end = 7.dp,
                    bottom = 5.dp
                ),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    color = Color.White,
                    text = msg.message ?: stringResource(id = R.string.smthng_wrong)
                )
                Row(
                    modifier = Modifier.align(Alignment.Bottom),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        color = Color.LightGray,
                        text = msg.timeSent ?: stringResource(id = R.string.def_time),
                        modifier = Modifier.padding(start = 7.dp, top = 2.dp),
                        textAlign = TextAlign.End,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}