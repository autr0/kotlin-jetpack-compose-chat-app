package com.devautro.firebasechatapp.sign_in.data.model

import com.devautro.firebasechatapp.core.data.model.UserData

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)