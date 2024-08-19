package com.devautro.firebasechatapp.users.data

import android.util.Log
import com.devautro.firebasechatapp.profile.data.model.ProfileData
import com.devautro.firebasechatapp.core.data.model.UserData
import com.devautro.firebasechatapp.users.data.model.Request
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsersDataRepository @Inject constructor(
    private val auth: FirebaseAuth,
    database: FirebaseDatabase
) {
    private val usersRef = database.getReference("users")
    private val requestsRef = database.getReference("requests")
    private val companionsRef = database.getReference("companions")
    private val profileRef = database.getReference("profile")

    var currentUser = UserData(
        auth.currentUser?.uid,
        auth.currentUser?.displayName,
        auth.currentUser?.photoUrl.toString()
    )
    private val currentUserName = currentUser.username

    fun updateCurrentUser() {
        val user = auth.currentUser
        currentUser = UserData(
            user?.uid,
            user?.displayName,
            user?.photoUrl.toString()
        )
    }

    fun getUsersFromDB(onUsersReceived: (List<UserData>) -> Unit) {
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dbUsersList = mutableListOf<UserData>()
                for (s in snapshot.children) {
                    val userData = s.getValue(UserData::class.java)
                    if (userData != null && !dbUsersList.contains(userData)
                    ) {
                        dbUsersList.add(userData)
                    }
                }
                onUsersReceived(dbUsersList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MyLog", "Failed to get users list: $error")
            }

        })
    }

    suspend fun sendNewRequest(userData: UserData) = coroutineScope {
        try {
            if (userData.username != auth.currentUser?.displayName) {
                if (currentUserName != null && userData.username != null) {

                    val request = Request(
                        from = currentUser,
                        to = userData,
                        isApproved = false,
                        isAnswered = false
                    )

                    val setOutgoingRequest = async {
                        requestsRef.child(currentUser.userId ?: "userUnknown")
                            .child("Outgoing")
                            .child("$currentUserName${userData.username}")
                            .setValue(request).await()
                    }

                    val setIncomingRequest = async { requestsRef.child(userData.userId ?: "userUnknown2")
                        .child("Incoming")
                        .child("$currentUserName${userData.username}")
                        .setValue(request).await()
                    }

                    awaitAll(setOutgoingRequest, setIncomingRequest)
                } else {
                    Log.e("MyLog", "Current User or User from the list is null!")
                }
            } else {
                Log.w("MyLog", "current user and user in the list are the same!")
            }
        } catch (e: Exception) {
            Log.e("MyLog", "Failed to send new request: ${e.message}")
        }

    }

    fun getIncomingRequests(onIncomingRequestsReceived: (MutableList<Request>) -> Unit) {
        requestsRef.child(currentUser.userId!!).child("Incoming").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val incomingRequestsList = mutableListOf<Request>()
                    for (s in snapshot.children) {
                        val incomingRequest = s.getValue(Request::class.java)
                        if (incomingRequest != null && !incomingRequest.isAnswered &&
                            !incomingRequestsList.contains(incomingRequest)
                        ) {
                            incomingRequestsList.add(incomingRequest)
                        }
                    }

                    onIncomingRequestsReceived(incomingRequestsList)

                    // incoming size:
                    profileRef.child("${currentUser.userId}").child("incomingSize")
                        .setValue(
                            ProfileData(
                                name = "incomingSize",
                                size = incomingRequestsList.size
                            )

                        )
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyLog", "Failed to read value in incomingRequests: $error")
                }

            }
        )
    }

    fun getOutgoingRequests(onOutgoingRequestsReceived: (MutableList<Request>) -> Unit) {
        requestsRef.child(currentUser.userId!!).child("Outgoing").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val outgoingRequestsList = mutableListOf<Request>()
                    for (s in snapshot.children) {
                        val outgoingRequest = s.getValue(Request::class.java)
                        if (outgoingRequest != null && !outgoingRequest.isAnswered &&
                            !outgoingRequestsList.contains(outgoingRequest)
                        ) {
                            outgoingRequestsList.add(outgoingRequest)
                        }
                    }

                    onOutgoingRequestsReceived(outgoingRequestsList)

                    // outgoing size:
                    profileRef.child("${currentUser.userId}").child("outgoingSize")
                        .setValue(
                            ProfileData(
                                name = "outgoingSize",
                                size = outgoingRequestsList.size
                            )
                        )
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyLog", "Failed to read value in outgoingRequests: $error")
                }

            }
        )
    }

    fun getCompanions(onCompanionsReceived: (MutableList<UserData>) -> Unit) {
        companionsRef.child(currentUser.userId!!).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val companionsList = mutableListOf<UserData>()
                    for (s in snapshot.children) {
                        val companion = s.getValue(UserData::class.java)
                        if (companion != null && !companionsList.contains(companion)) {
                            companionsList.add(companion)
                        }
                    }
                    onCompanionsReceived(companionsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyLog", "Failed to read value in 'getCompanions': $error")
                }

            }
        )
    }

    suspend fun writeNewUserData() {
        try {
            usersRef.child(currentUser.userId ?: "userUnknown")
                .setValue(currentUser).await()
        } catch (e: Exception) {
            Log.e("MyLog", "Failed to write new user data: ${e.message}")
        }
    }

    suspend fun updateIncomingRequest(
        userFrom: UserData,
        isApproved: Boolean,
    ) = coroutineScope {

        try {
//          remove incoming request for currentUser
            val removeIncomingRequest = async {
                requestsRef.child(currentUser.userId!!)
                    .child("Incoming")
                    .child("${userFrom.username}${currentUser.username}")
                    .removeValue().await()
            }


//          remove outgoing request for userFrom
            val removeOutgoingRequest = async {
                requestsRef.child(userFrom.userId!!)
                    .child("Outgoing")
                    .child("${userFrom.username}${currentUser.username}")
                    .removeValue().await()
            }

            awaitAll(removeIncomingRequest, removeOutgoingRequest)

            if (isApproved) {
                val addCurrentUser = async {
                    companionsRef.child("${userFrom.userId}")
                        .child("${currentUser.username}")
                        .setValue(
                            UserData(
                                userId = currentUser.userId,
                                username = currentUser.username,
                                profilePictureUrl = currentUser.profilePictureUrl
                            )
                        ).await()
                }

                val addCompanion = async {
                    companionsRef.child("${currentUser.userId}")
                        .child("${userFrom.username}")
                        .setValue(
                            UserData(
                                userId = userFrom.userId,
                                username = userFrom.username,
                                profilePictureUrl = userFrom.profilePictureUrl
                            )
                        ).await()
                }

                awaitAll(addCurrentUser, addCompanion)
            } else { /*nothing to do*/ }

        } catch (e: Exception) {
            Log.e("MyLog", "Error updating incoming request: ${e.message}")
        }
    }


    suspend fun cancelOutgoingRequest(
        userTo: UserData
    ) = coroutineScope {
        try {
//          delete outgoing request from currentUser outgoingRequestsList
            val cancelCurrentUserRequest = async {
                requestsRef.child("${currentUser.userId}")
                    .child("Outgoing")
                    .child("${currentUser.username}${userTo.username}")
                    .removeValue().await()
            }

//          delete incoming request from userTo incomingRequestsList
            val cancelFromCompanion = async {
                requestsRef.child("${userTo.userId}")
                    .child("Incoming")
                    .child("${currentUser.username}${userTo.username}")
                    .removeValue().await()
            }

            awaitAll(cancelCurrentUserRequest, cancelFromCompanion)

        } catch (e: Exception) {
            Log.e("MyLog", "Error canceling outgoing request: ${e.message}")
        }
    }
}