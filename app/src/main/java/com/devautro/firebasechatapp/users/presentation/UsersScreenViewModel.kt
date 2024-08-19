package com.devautro.firebasechatapp.users.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devautro.firebasechatapp.R
import com.devautro.firebasechatapp.core.data.model.UserData
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
    private val usersDataRepo: UsersDataRepository,
    @ApplicationContext val context: Context
)  : ViewModel() {

    private val _usersList = MutableStateFlow(listOf<UserData>())
    val usersList = _usersList.asStateFlow()

    private val _outgoingRequestsList = MutableStateFlow(mutableListOf<Request>())
    val outgoingRequestsList = _outgoingRequestsList.asStateFlow()

    private val _incomingRequestsList = MutableStateFlow(mutableListOf<Request>())
    val incomingRequestsList = _incomingRequestsList.asStateFlow()

    private val _companionsList = MutableStateFlow(listOf<UserData>())


    init {
        if (usersDataRepo.currentUser.userId != null) {
            updateCurrentUser()
            getCompanions()
            getUsersFromDB()
            getOutgoingRequests()
            getIncomingRequests()

            if (!_usersList.value.contains(usersDataRepo.currentUser)) {
                addUser()
            }
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
            usersDataRepo.currentUser.userId == userData.userId -> {
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
            usersDataRepo.getUsersFromDB { dbUsersList ->
                _usersList.value = dbUsersList
            }
        }
    }

    fun sendRequest(userDataArg: UserData) {
        viewModelScope.launch {
            usersDataRepo.sendNewRequest(userDataArg)
        }

    }

    private fun getOutgoingRequests() {
        viewModelScope.launch {
            usersDataRepo.getOutgoingRequests{ outgoingRequests ->
                _outgoingRequestsList.value = outgoingRequests
            }

        }
    }

    private fun getIncomingRequests() {
        viewModelScope.launch {
            usersDataRepo.getIncomingRequests{ incomingRequest ->
                _incomingRequestsList.value = incomingRequest
            }
        }
    }

    private fun getCompanions() {
        viewModelScope.launch {
            usersDataRepo.getCompanions{ companions ->
                _companionsList.value = companions
            }
        }
    }

    fun updateIncomingRequests(
        userFrom: UserData,
        isApproved: Boolean
    ) {
        viewModelScope.launch {
            usersDataRepo.updateIncomingRequest(
                userFrom = userFrom,
                isApproved = isApproved
            )
        }
    }

    fun cancelOutgoingRequest(userTo: UserData) {
        viewModelScope.launch {
            usersDataRepo.cancelOutgoingRequest(userTo)
        }
    }

    private fun addUser() {
        viewModelScope.launch {
            usersDataRepo.writeNewUserData()
        }
    }

    private fun updateCurrentUser() {
        usersDataRepo.updateCurrentUser()
    }

    fun resetUsersState() {
        updateCurrentUser()
        _usersList.update { listOf() }
        _outgoingRequestsList.update { mutableListOf() }
        _incomingRequestsList.update { mutableListOf() }
        _companionsList.update { listOf() }
        if (usersDataRepo.currentUser.userId != null) {
            getCompanions()
            getUsersFromDB()
            getOutgoingRequests()
            getIncomingRequests()
        }
    }

}