package com.devautro.firebasechatapp.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devautro.firebasechatapp.profile.data.ProfileDataUploader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileData: ProfileDataUploader
): ViewModel() {

    private val _sizes = MutableStateFlow(mapOf<String, Int>())
    val sizes = _sizes.asStateFlow()

    init {
        if (profileData.currentUser.userId != null) {
            getSizes()
        }

    }

    private fun getSizes() {
        viewModelScope.launch {
            profileData.getProfileData{ data ->
                _sizes.value = data
            }
        }
    }

    private fun updateCurrentUser() {
        profileData.updateCurrentUser()
    }

    fun resetProfileVmState() {
        updateCurrentUser()
        _sizes.update { mutableMapOf() }
        if (profileData.currentUser.userId != null) {
            getSizes()
        }
    }

}