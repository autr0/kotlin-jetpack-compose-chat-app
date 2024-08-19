package com.devautro.firebasechatapp.chatsHome.presentation.screens.companionsScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.chatsHome.presentation.ChatsHomeViewModel
import com.devautro.firebasechatapp.core.data.model.UserData
import com.devautro.firebasechatapp.core.presentation.AutoResizedText

@Composable
fun MessageDialog(
    dialogState: MutableState<Boolean>,
    userData: UserData,
    vm: ChatsHomeViewModel
) {
    var inputText by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = { dialogState.value = false }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Spacer(modifier = Modifier.height(10.dp))
                AutoResizedText(text = stringResource(id = R.string.enter_msg_text))
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.enter_msg_placeholder),
                            color = Color.DarkGray
                        )
                    },
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {
                            inputText = ""
                            dialogState.value = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = stringResource(id = R.string.cancel))
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Spacer(modifier = Modifier.width(50.dp))

                    TextButton(
                        onClick = {
                            vm.createChatStatus(
                                userData = userData,
                                message = inputText
                            )

                            inputText = ""
                            dialogState.value = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = stringResource(id = R.string.send))
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}