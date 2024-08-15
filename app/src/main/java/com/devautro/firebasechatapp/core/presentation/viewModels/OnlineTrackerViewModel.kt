package com.devautro.firebasechatapp.core.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class OnlineTrackerViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ViewModel() {

    fun updateOnlineStatus(isOnline: Boolean) {
        val currentUserId = auth.currentUser?.uid ?: return
        val onlineStateRef = database.getReference("onlineState")
        viewModelScope.launch {
            try {
                onlineStateRef.child(currentUserId).setValue(isOnline).await()
            } catch (e: Exception) {
                Log.e("MyLog", "Error updating online status: ${e.message}")
            }
        }
    }

}