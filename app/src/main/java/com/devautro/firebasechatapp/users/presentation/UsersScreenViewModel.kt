package com.devautro.firebasechatapp.users.presentation

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.sign_in.data.model.UserData
import com.devautro.firebasechatapp.users.data.UsersDataRepository
import com.devautro.firebasechatapp.users.data.model.Request
import com.devautro.firebasechatapp.users.data.model.UsersButtonState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersScreenViewModel @Inject constructor(
    private val usersDataRep: UsersDataRepository,
    @ApplicationContext val context: Context
)  : ViewModel() {

    private val _usersList = MutableStateFlow(SnapshotStateList<UserData>())
    val usersList = _usersList.asStateFlow()

    private val _outgoingRequestsList = MutableStateFlow(SnapshotStateList<Request>())
    val outgoingRequestsList = _outgoingRequestsList.asStateFlow()

    private val _incomingRequestsList = MutableStateFlow(SnapshotStateList<Request>())
    val incomingRequestsList = _incomingRequestsList.asStateFlow()

    private val _companionsList = MutableStateFlow(SnapshotStateList<UserData>())


    init {
        getCompanions()
        getUsersFromDB()
        getOutgoingRequests()
        getIncomingRequests()

        if (!_usersList.value.contains(usersDataRep.currentUser)) {
            addUser()
        }
    }

    fun removeIncomingRequest(req: Request) {
        _incomingRequestsList.value.remove(req)
    }

    fun removeOutgoingRequest(req: Request) {
        _outgoingRequestsList.value.remove(req)
    }

    fun getButtonState(userData: UserData): UsersButtonState {
        return when {
            _incomingRequestsList.value.any { it.from?.userId == userData.userId } -> {
                UsersButtonState(
                    isEnabled = false,
                    bText = context.getString(R.string.button_incoming_request)
                )
            }
            _outgoingRequestsList.value.any { it.to?.userId == userData.userId } -> {
                UsersButtonState(
                    isEnabled = false,
                    bText = context.getString(R.string.button_wait_response)
                )
            }
            _companionsList.value.any { it.userId == userData.userId } -> {
                UsersButtonState(
                    isEnabled = false,
                    bText = context.getString(R.string.button_companion)
                )
            }
            usersDataRep.currentUser.userId == userData.userId -> {
                UsersButtonState(
                    isEnabled = false,
                    bText = context.getString(R.string.button_you)
                )
            }
            else -> {
                UsersButtonState(
                    isEnabled = true,
                    bText = context.getString(R.string.button_request_dialog)
                )
            }
        }
    }

    private fun getUsersFromDB() {
        viewModelScope.launch {
            usersDataRep.getUsersFromDB(_usersList.value)
        }
    }

    fun sendRequest(userDataArg: UserData) {
        viewModelScope.launch {
            usersDataRep.sendNewRequest(userDataArg)
        }

    }

    private fun getOutgoingRequests() {
        viewModelScope.launch {
            usersDataRep.getOutgoingRequests(_outgoingRequestsList.value)

        }
    }

    private fun getIncomingRequests() {
        viewModelScope.launch {
            usersDataRep.getIncomingRequests(_incomingRequestsList.value)
        }
    }

    private fun getCompanions() {
        viewModelScope.launch {
            usersDataRep.getCompanions(_companionsList.value)
        }
    }

    fun updateIncomingRequests(
        userFrom: UserData,
        isApproved: Boolean
    ) {
        viewModelScope.launch {
            usersDataRep.updateIncomingRequest(
                userFrom = userFrom,
                isApproved = isApproved
            )
        }
    }

    fun cancelOutgoingRequest(userTo: UserData) {
        viewModelScope.launch {
            usersDataRep.cancelOutgoingRequest(userTo)
        }
    }

    private fun addUser() {
        viewModelScope.launch {
            usersDataRep.writeNewUserData()
        }
    }

    private fun updateCurrentUser() {
        usersDataRep.updateCurrentUser()
    }

    fun resetUsersState() {
        updateCurrentUser()
        _usersList.update { SnapshotStateList<UserData>() }
        _outgoingRequestsList.update { SnapshotStateList<Request>() }
        _incomingRequestsList.update { SnapshotStateList<Request>() }
        _companionsList.update { SnapshotStateList<UserData>() }
        getCompanions()
        getUsersFromDB()
        getOutgoingRequests()
        getIncomingRequests()
    }

}