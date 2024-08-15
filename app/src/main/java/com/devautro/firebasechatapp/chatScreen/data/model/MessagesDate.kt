package com.devautro.firebasechatapp.chatScreen.data.model

import com.devautro.firebasechatapp.core.data.model.Message

data class MessagesDate(
    val date: String,
    val messages: List<Message>
)