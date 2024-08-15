package com.devautro.firebasechatapp.core.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.devautro.firebasechatapp.core.data.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SharedChatViewModel @Inject constructor()
    : ViewModel() {
    private val _companion = MutableStateFlow(UserData())
    val companion = _companion.asStateFlow()

    fun updateCompanion(companion: UserData) {
        _companion.value = companion
    }

    fun resetSharedVmState() {
        _companion.update { UserData() }
    }

}