package com.devautro.firebasechatapp.chatsHome.data.model

import com.devautro.firebasechatapp.core.data.model.Message
import com.devautro.firebasechatapp.core.data.model.UserData

data class ChatStatus(
    val currentUser: UserData? = null,
    val companion: UserData? = null,
    val lastMessage: Message? = null,
)