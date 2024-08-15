package com.devautro.firebasechatapp.users.data.model

import com.devautro.firebasechatapp.core.data.model.UserData

data class Request(
    val from: UserData? = null,
    val to: UserData? = null,
    var isApproved: Boolean = false,
    var isAnswered: Boolean = false
)