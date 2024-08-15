package com.devautro.firebasechatapp.chatScreen.presentation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devautro.firebasechatapp.chatScreen.data.ChatRepository
import com.devautro.firebasechatapp.chatScreen.data.model.MessagesDate
import com.devautro.firebasechatapp.core.data.model.Message
import com.devautro.firebasechatapp.core.data.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    val chatRepo: ChatRepository
) : ViewModel() {

    private val _msgsList = MutableStateFlow(SnapshotStateList<Message>())
    val msgsList = _msgsList.asStateFlow()

    private val _msgsDateList = MutableStateFlow(SnapshotStateList<MessagesDate>())
    val msgsDateList = _msgsDateList.asStateFlow()


    fun getMessages(companion: UserData) {
        viewModelScope.launch {
            chatRepo.getMessages(_msgsList.value, companion, _msgsDateList.value)
        }
    }

    fun addNewMessage(
        userData: UserData,
        msg: String,
        time: String,
        date: String,
        dateTime: String
    ) {
        viewModelScope.launch {
            chatRepo.addMessage(
                companion = userData,
                message = msg,
                time = time,
                date = date,
                dateTime = dateTime
            )
        }
    }

    fun updateMessageStatus(companion: UserData) {
        viewModelScope.launch {
            chatRepo.updateMessageStatus(
                msgsList = _msgsList.value,
                companion = companion
            )
        }
    }

    fun deleteMessage(
        companion: UserData,
        key: String,
        remove: Boolean
    ) {
        viewModelScope.launch {
            chatRepo.deleteMessage(
                companion = companion,
                key = key,
                removeFromCompanion = remove
            )
        }
    }

    private fun updateCurrentUser() {
        viewModelScope.launch {
            chatRepo.updateCurrentUser()
        }
    }

    fun resetChatState() {
        updateCurrentUser()
        _msgsList.update { SnapshotStateList<Message>() }
        _msgsDateList.update { SnapshotStateList<MessagesDate>() }
    }

}