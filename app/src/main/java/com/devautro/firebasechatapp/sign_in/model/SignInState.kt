package com.devautro.firebasechatapp.sign_in.model

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)