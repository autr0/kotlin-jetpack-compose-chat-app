package com.devautro.firebasechatapp.core.data.model

data class MessageStatus(
    val pending: Boolean = false,
    val received: Boolean = false,
    val read: Boolean = false
)