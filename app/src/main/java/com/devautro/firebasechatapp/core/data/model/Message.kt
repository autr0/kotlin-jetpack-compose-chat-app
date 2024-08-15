package com.devautro.firebasechatapp.core.data.model

data class Message(
    val nameFrom: String? = null,
    val message: String? = null,
    val status: MessageStatus = MessageStatus(pending = true),
    val timeSent: String? = null,
    val date: String? = null,
    val dateTime: String? = null,
    val id: String? = null
)