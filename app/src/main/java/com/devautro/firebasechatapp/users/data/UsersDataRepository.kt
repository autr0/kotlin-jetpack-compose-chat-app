package com.devautro.firebasechatapp.users.data

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.devautro.firebasechatapp.profile.data.model.ProfileData
import com.devautro.firebasechatapp.core.data.model.UserData
import com.devautro.firebasechatapp.users.data.model.Request
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class UsersDataRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
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
    val currentUserName = currentUser.username

    fun updateCurrentUser() {
        val user = auth.currentUser
        currentUser = UserData(
            user?.uid,
            user?.displayName,
            user?.photoUrl.toString()
        )
    }

    suspend fun getUsersFromDB(dbUsersList: SnapshotStateList<UserData>) {
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s in snapshot.children) {
                    val userData = s.getValue(UserData::class.java)
                    if (userData != null && !dbUsersList.contains(userData)
                    ) {
                        dbUsersList.add(userData)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MyLog", "Failed to get users list: $error")
            }

        })
    }

    suspend fun sendNewRequest(userData: UserData) {
        if (userData.username != auth.currentUser?.displayName) {
            if (currentUserName != null && userData.username != null) {
                requestsRef.child(currentUser.userId ?: "userUnknown")
                    .child("Outgoing")
                    .child("$currentUserName${userData.username}")
                    .setValue(
                        Request(
                            from = currentUser,
                            to = userData,
                            isApproved = false,
                            isAnswered = false
                        )
                    )
                requestsRef.child(userData.userId ?: "userUnknown2")
                    .child("Incoming")
                    .child("$currentUserName${userData.username}")
                    .setValue(
                        Request(
                            from = currentUser,
                            to = userData,
                            isApproved = false,
                            isAnswered = false
                        )
                    )
            }
        }

    }

    suspend fun getIncomingRequests(incomingRequestsList: SnapshotStateList<Request>) {
        requestsRef.child(currentUser.userId!!).child("Incoming").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (s in snapshot.children) {
                        val incomingRequest = s.getValue(Request::class.java)
                        if (incomingRequest != null && !incomingRequest.isAnswered &&
                            !incomingRequestsList.contains(incomingRequest)
                        ) {
                            incomingRequestsList.add(incomingRequest)
                        }
                    }

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

    suspend fun getOutgoingRequests(outgoingRequestsList: SnapshotStateList<Request>) {
        requestsRef.child(currentUser.userId!!).child("Outgoing").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (s in snapshot.children) {
                        val outgoingRequest = s.getValue(Request::class.java)
                        if (outgoingRequest != null && !outgoingRequest.isAnswered &&
                            !outgoingRequestsList.contains(outgoingRequest)) {
                            outgoingRequestsList.add(outgoingRequest)
                        }
                    }
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

    suspend fun getCompanions(companionsList: SnapshotStateList<UserData>) {
        companionsRef.child(currentUser.userId!!).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (s in snapshot.children) {
                        val companion = s.getValue(UserData::class.java)
                        if (companion != null && !companionsList.contains(companion)) {
                            companionsList.add(companion)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyLog", "Failed to read value in 'getCompanions': $error")
                }

            }
        )
    }

    suspend fun writeNewUserData() {
        usersRef.child(currentUser.userId ?: "userUnknown")
            .setValue(
                currentUser
            )
    }

    suspend fun updateIncomingRequest(
        userFrom: UserData,
        isApproved: Boolean,
    ) {
//        remove incoming request for currentUser
        requestsRef.child(currentUser.userId!!)
            .child("Incoming")
            .child("${userFrom.username}${currentUser.username}")
            .removeValue()

//        remove outgoing request for userFrom
        requestsRef.child(userFrom.userId!!)
            .child("Outgoing")
            .child("${userFrom.username}${currentUser.username}")
            .removeValue()

        if (isApproved) {
            companionsRef.child("${userFrom.userId}")
                .child("${currentUser.username}")
                .setValue(
                    UserData(
                        userId = currentUser.userId,
                        username = currentUser.username,
                        profilePictureUrl = currentUser.profilePictureUrl
                    )
                )

            companionsRef.child("${currentUser.userId}")
                .child("${userFrom.username}")
                .setValue(
                    UserData(
                        userId = userFrom.userId,
                        username = userFrom.username,
                        profilePictureUrl = userFrom.profilePictureUrl
                    )
                )
        }
    }

    suspend fun cancelOutgoingRequest(
        userTo: UserData
    ) {
//            delete outgoing request from currentUser outgoingRequestsList
        requestsRef.child("${currentUser.userId}")
            .child("Outgoing")
            .child("${currentUser.username}${userTo.username}")
            .removeValue()

//            delete incoming request from userTo incomingRequestsList
        requestsRef.child("${userTo.userId}")
            .child("Incoming")
            .child("${currentUser.username}${userTo.username}")
            .removeValue()
    }
}