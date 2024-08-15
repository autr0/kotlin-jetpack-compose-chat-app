package com.devautro.firebasechatapp.chatsHome.data

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.devautro.firebasechatapp.chatsHome.data.model.ChatStatus
import com.devautro.firebasechatapp.core.data.dateTimeToLocalTimeZone
import com.devautro.firebasechatapp.core.data.model.Message
import com.devautro.firebasechatapp.core.data.model.MessageStatus
import com.devautro.firebasechatapp.core.data.model.UserData
import com.devautro.firebasechatapp.profile.data.model.ProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ChatsHomeRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) {
    private val companionsRef = database.getReference("companions")
    private val chatsRef = database.getReference("chats")
    private val messagesRef = database.getReference("messages")
    private val profileRef = database.getReference("profile")
    var currentUser = UserData(
        auth.currentUser?.uid,
        auth.currentUser?.displayName,
        auth.currentUser?.photoUrl.toString(),
    )

    fun updateCurrentUser() {
        val user = auth.currentUser
        currentUser = UserData(
            user?.uid,
            user?.displayName,
            user?.photoUrl.toString()
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
//                      add companions size:
                    profileRef.child("${ currentUser.userId }").child("companionsSize")
                        .setValue(
                            ProfileData(
                                name = "companionsSize",
                                size = companionsList.size
                            )
                        )
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyLog", "Failed to read value in 'getCompanions': $error")
                }

            }
        )
    }

    suspend fun getHomeChats(chatsList: SnapshotStateList<ChatStatus>) {
        chatsRef.child(currentUser.userId!!).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatsList.clear()

                    for (s in snapshot.children) {
                        val chatStatus = s.getValue(ChatStatus::class.java)
                        if (chatStatus?.lastMessage?.dateTime != null &&
                            !chatsList.contains(chatStatus)
                        ) {
                            val dt = dateTimeToLocalTimeZone(chatStatus.lastMessage.dateTime)
                            chatsList.add(
                                chatStatus.copy(
                                    lastMessage = chatStatus.lastMessage.copy(
                                        timeSent = dt.time,
                                        date = dt.date
                                    )
                                )
                            )
                        }
                    }

//                      get chats size:
                    profileRef.child("${ currentUser.userId }").child("chatsSize")
                        .setValue(
                            ProfileData(
                                name = "chatsSize",
                                size = chatsList.size
                            )
                        )
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyLog", "Failed to read value in 'getHomeChats': $error")
                }

            }
        )
    }

    suspend fun createChat(
        companion: UserData,
        message: String,
        dateTime: String,
        time: String,
        date: String
    ) {
        val key = messagesRef.push().key

//          Create chats branch for user_1 (userFrom)
        chatsRef.child("${companion.userId}")
            .child("${companion.username}_${currentUser.username}")
            .setValue(
                ChatStatus(
                    currentUser = companion, // 'cause he receive it as 'currentUser'
                    companion = currentUser,
                    lastMessage = Message(
                        nameFrom = currentUser.username,
                        message = message,
                        status = MessageStatus(received = true),
                        timeSent = time,
                        date = date,
                        dateTime = dateTime,
                        id = key
                    )
                )
            )

//          Create chats branch for user_2 (userTo)
        chatsRef.child("${currentUser.userId}")
            .child("${currentUser.username}_${companion.username}")
            .setValue(
                ChatStatus(
                    currentUser = currentUser,
                    companion = companion,
                    lastMessage = Message(
                        nameFrom = currentUser.username,
                        message = message,
                        status = MessageStatus(received = true),
                        timeSent = time,
                        date = date,
                        dateTime = dateTime,
                        id = key
                    )
                )
            )


//          Create msgs branch for currentUser
        messagesRef.child("${currentUser.userId}")
            .child("${currentUser.username}_${companion.username}")
            .child(key ?: "unknown message")
            .setValue(
                Message(
                    nameFrom = currentUser.username,
                    message = message,
                    status = MessageStatus(received = true),
                    timeSent = time,
                    date = date,
                    dateTime = dateTime,
                    id = key
                )
            )

//          Create msgs branch for companion
        messagesRef.child("${companion.userId}")
            .child("${companion.username}_${currentUser.username}")
            .child(key ?: "unknown message")
            .setValue(
                Message(
                    nameFrom = currentUser.username,
                    message = message,
                    status = MessageStatus(received = true),
                    timeSent = time,
                    date = date,
                    dateTime = dateTime,
                    id = key
                )
            )


    }

    suspend fun updateLastMessage(companion: UserData) {
        messagesRef.child("${currentUser.userId}")
            .child("${currentUser.username}_${companion.username}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lastSnapshot = snapshot.children.lastOrNull()
                    if (lastSnapshot != null) {
                        val lastMsg = lastSnapshot.getValue(Message::class.java)


                        if (lastMsg != null) {
                            val updateCurrentUserChat = hashMapOf<String, Any>(
                                "${currentUser.username}_${companion.username}" to
                                        ChatStatus(
                                            currentUser = currentUser,
                                            companion = companion,
                                            lastMessage = lastMsg
                                        )
                            )
                            chatsRef.child(currentUser.userId!!)
                                .updateChildren(updateCurrentUserChat)

                            val updateCompanionChat = hashMapOf<String, Any>(
                                "${companion.username}_${currentUser.username}" to
                                        ChatStatus(
                                            currentUser = companion,
                                            companion = currentUser,
                                            lastMessage = lastMsg
                                        )
                            )
                            chatsRef.child(companion.userId!!).updateChildren(updateCompanionChat)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyLog", "Failed to read value in 'updateLastMessage': $error")
                }
            })
    }

}