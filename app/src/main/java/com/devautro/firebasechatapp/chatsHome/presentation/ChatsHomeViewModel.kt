package com.devautro.firebasechatapp.chatsHome.presentation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devautro.firebasechatapp.chatsHome.data.ChatsHomeRepository
import com.devautro.firebasechatapp.chatsHome.data.model.ChatStatus
import com.devautro.firebasechatapp.core.data.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsHomeViewModel @Inject constructor(
    private val chatsHomeRepo: ChatsHomeRepository
) : ViewModel() {

    private val _companionsList = MutableStateFlow(SnapshotStateList<UserData>())
    val companionsList: StateFlow<SnapshotStateList<UserData>> = _companionsList

    private val _chatsList = MutableStateFlow(SnapshotStateList<ChatStatus>())
    val chatsList: StateFlow<SnapshotStateList<ChatStatus>> = _chatsList

    init {
        getCompanions()
        getChats()
    }

    private fun getCompanions() {
        viewModelScope.launch {
            chatsHomeRepo.getCompanions(_companionsList.value)
        }

    }

    private fun getChats() {
        viewModelScope.launch {
            chatsHomeRepo.getHomeChats(_chatsList.value)
        }

    }

    fun createChatStatus(
        userData: UserData,
        message: String,
        dateTime: String,
        time: String,
        date: String
    ) {
        viewModelScope.launch {
            chatsHomeRepo.createChat(
                companion = userData,
                message = message,
                dateTime = dateTime,
                time = time,
                date = date
            )
        }
    }

    fun updateLastMessage(companion: UserData) {
        viewModelScope.launch {
            chatsHomeRepo.updateLastMessage(companion)
        }
    }

    private fun updateCurrentUser() {
        chatsHomeRepo.updateCurrentUser()
    }

    fun resetChatsHomeVmState() {
        updateCurrentUser()
        _companionsList.update { SnapshotStateList<UserData>() }
        _chatsList.update { SnapshotStateList<ChatStatus>() }
        getCompanions()
        getChats()
    }

}