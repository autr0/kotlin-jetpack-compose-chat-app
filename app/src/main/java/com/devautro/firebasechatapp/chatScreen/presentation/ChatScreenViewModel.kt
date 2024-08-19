package com.devautro.firebasechatapp.chatScreen.presentation

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
import android.icu.text.DateFormat
import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    val chatRepo: ChatRepository
) : ViewModel() {

    private val _msgsList = MutableStateFlow(listOf<Message>())
    val msgsList = _msgsList.asStateFlow()

    private val _msgsDateList = MutableStateFlow(listOf<MessagesDate>())
    val msgsDateList = _msgsDateList.asStateFlow()

    private val _companionOnlineStatus = MutableStateFlow(false)
    val companionOnlineStatus = _companionOnlineStatus.asStateFlow()

    private val _msgsDateFlatList = MutableStateFlow<List<String>>(emptyList())

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _matchingMessageIndices = MutableStateFlow<List<Int>>(emptyList())
    val matchingMessageIndices = _matchingMessageIndices.asStateFlow()

    private val _matchingMessageCount = MutableStateFlow(0)
    val matchingMessageCount = _matchingMessageCount.asStateFlow()

    private val _currentMatchIndex = MutableStateFlow(0)
    val currentMatchIndex = _currentMatchIndex.asStateFlow()


    fun getMessages(companion: UserData) {
        viewModelScope.launch {
            chatRepo.getMessages(companion) { msgs ->
                _msgsList.value = msgs
                _msgsDateList.value = msgs.groupBy { it.date }.map {
                    MessagesDate(
                        date = it.key.toString(),
                        messages = it.value
                    )
                }
            }
        }
    }

    fun addNewMessage(
        userData: UserData,
        msg: String
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

    fun resetMessagesLists() {
        _msgsList.update { listOf() }
        _msgsDateList.update { listOf() }
    }

    fun getCompanionOnlineStatus(companionId: String) {
        viewModelScope.launch {
            chatRepo.getCompanionOnlineStatus(companionId) { isOnline ->
                _companionOnlineStatus.value = isOnline
            }
        }
    }

    fun resetCompanionOnlineStatus() {
        _companionOnlineStatus.update { false }
    }

    fun updateSearchText(newText: String) {
        _searchText.value = newText
    }

    fun resetSearchText() {
        _searchText.value = ""
        _matchingMessageIndices.value = emptyList()
        _matchingMessageCount.value = 0
        _currentMatchIndex.value = 0
    }

    fun shouldPink(msg: String): Boolean {
        return if (searchText.value.isNotEmpty()) msg.contains(searchText.value, ignoreCase = true)
        else false
    }

    fun searchMessages() {
        makeFlatMsgsDateList(_msgsDateList.value)

        val index = _msgsDateFlatList.value.withIndex()
            .filter { it.value.contains(searchText.value, ignoreCase = true) }
            .map { it.index }

        _matchingMessageIndices.value = index.reversed()
        _matchingMessageCount.value = index.size

    }

    private fun makeFlatMsgsDateList(msgsDate: List<MessagesDate>) {
        val resList = mutableListOf<String>()
        for (i in msgsDate) {
            resList.add(i.date)
            resList.addAll(i.messages.mapNotNull { it.message })
        }

        if (resList.isNotEmpty()) _msgsDateFlatList.value = resList
    }

    fun goToNextMatch() {
        if (_matchingMessageIndices.value.isNotEmpty()) {
            _currentMatchIndex.value =
                (_currentMatchIndex.value + 1) % _matchingMessageIndices.value.size
        }
    }

    fun goToPreviousMatch() {
        if (_matchingMessageIndices.value.isNotEmpty()) {
            _currentMatchIndex.value =
                if (_currentMatchIndex.value - 1 < 0) _matchingMessageIndices.value.size - 1
                else _currentMatchIndex.value - 1
        }

    }

    private fun updateCurrentUser() {
        viewModelScope.launch {
            chatRepo.updateCurrentUser()
        }
    }

    fun resetChatState() {
        updateCurrentUser()
        resetMessagesLists()
        resetCompanionOnlineStatus()
    }

}