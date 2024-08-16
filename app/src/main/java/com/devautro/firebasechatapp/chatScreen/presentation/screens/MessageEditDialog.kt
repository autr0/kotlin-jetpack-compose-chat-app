package com.devautro.firebasechatapp.chatScreen.presentation.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.core.presentation.AutoResizedText

@Composable
fun MessageEditDialog(
    openState: MutableState<Boolean>,
    msg: String,
    drawEditField: Boolean,
    deleteState: MutableState<Boolean>
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = { openState.value = false }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (drawEditField) {
                DialogRowTemplate(
                    icon = Icons.Filled.Edit,
                    iconDescription = "edit msg",
                    text = stringResource(id = R.string.edit),
                    openState = openState
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        val clipboardManager =
                            context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData: ClipData = ClipData.newPlainText("text", msg)
                        clipboardManager.setPrimaryClip(clipData)
                        Toast
                            .makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT)
                            .show()

                        openState.value = false
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(20.dp))
                Icon(
                    painter = painterResource(id = R.drawable.copy),
                    contentDescription = "copy msg text"
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = stringResource(id = R.string.copy_to_clipboard))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable {
                        openState.value = false
                        deleteState.value = true
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(20.dp))
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "delete msg"
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(text = stringResource(id = R.string.delete))
            }

        }
    }
}

@Composable
fun DialogRowTemplate(
    icon: ImageVector,
    iconDescription: String,
    text: String,
    openState: MutableState<Boolean>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { openState.value = false },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Icon(
            imageVector = icon,
            contentDescription = iconDescription
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = text)
    }
}


@Composable
fun DeleteDialog(
    deleteState: MutableState<Boolean>,
    companionName: String,
    checkedState: MutableState<Boolean>,
    removeMessage: () -> Unit
) {

    AlertDialog(
        onDismissRequest = { deleteState.value = false },
        confirmButton = {
            TextButton(onClick = {
                removeMessage()
                deleteState.value = false
            }) {
                Text(text = stringResource(id = R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = { deleteState.value = false }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        title = { AutoResizedText(text = stringResource(id = R.string.delete_msg_question)) },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        checkedState.value = !checkedState.value
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checkedState.value,
                    onCheckedChange = { checkedState.value = it }
                )

                AutoResizedText(text = "${stringResource(id = R.string.also_delete_for)} $companionName")
            }
        },
        shape = RoundedCornerShape(8.dp)
    )
}