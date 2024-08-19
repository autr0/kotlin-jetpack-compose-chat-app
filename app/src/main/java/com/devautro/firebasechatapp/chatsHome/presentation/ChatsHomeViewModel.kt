package com.devautro.firebasechatapp.chatsHome.presentation

import android.icu.text.DateFormat
import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devautro.firebasechatapp.chatsHome.data.ChatsHomeRepository
import com.devautro.firebasechatapp.chatsHome.data.model.ChatStatus
import com.devautro.firebasechatapp.core.data.model.UserData
import com.devautro.firebasechatapp.core.data.timeDateToLocalTimeZone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChatsHomeViewModel @Inject constructor(
    private val chatsHomeRepo: ChatsHomeRepository
) : ViewModel() {

    private val _companionsList = MutableStateFlow(listOf<UserData>())
    val companionsList: StateFlow<List<UserData>> = _companionsList.asStateFlow()

    private val _chatsList = MutableStateFlow(listOf<ChatStatus>())
    val chatsList: StateFlow<List<ChatStatus>> = _chatsList.asStateFlow()

    init {
        if (chatsHomeRepo.currentUser.userId != null) {
            getCompanions()
            getChats()
        }
    }

    private fun getCompanions() {
        viewModelScope.launch {
            chatsHomeRepo.getCompanions { companions ->
                _companionsList.value = companions
            }
        }

    }

    private fun getChats() {
        viewModelScope.launch {
            chatsHomeRepo.getHomeChats { chats ->
                val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
                val currentDateTime = LocalDateTime.now()
                _chatsList.value = chats.sortedByDescending { chatStatus ->
                    timeDateToLocalTimeZone(
                        chatStatus.lastMessage?.dateTime
                            ?: currentDateTime.format(dateTimeFormatter)
                    )
                }
            }
        }

    }

    fun createChatStatus(
        userData: UserData,
        message: String
    ) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance().time
            val time = DateFormat.getPatternInstance(DateFormat.HOUR24_MINUTE).format(calendar)

            val sdf = SimpleDateFormat("dd LLL yyyy", Locale.getDefault())
            val date = sdf.format(calendar).toString()

            // Transform current time and date into string with format ISO 8601
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val currentDateTime = LocalDateTime.now()
            val dateTime = currentDateTime.atZone(ZoneId.systemDefault()).format(formatter)

            chatsHomeRepo.createChat(
                companion = userData,
                message = message,
                dateTime = dateTime,
                time = time,
                date = date
            )
        }
    }

    fun calculateTodayDate(): String {
        val calendar = Calendar.getInstance().time
        val sdf = SimpleDateFormat("dd LLL yyyy", Locale.getDefault())
        val today = sdf.format(calendar).toString()
        return today
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
        _companionsList.update { listOf() }
        _chatsList.update { listOf() }
        if (chatsHomeRepo.currentUser.userId != null) {
            getCompanions()
            getChats()
        }
    }

}