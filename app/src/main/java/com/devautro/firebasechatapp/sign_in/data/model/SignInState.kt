package com.devautro.firebasechatapp.sign_in.data.model

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)