package com.devautro.firebasechatapp.chatsHome.data

import android.util.Log
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatsHomeRepository @Inject constructor(
    private val auth: FirebaseAuth,
    database: FirebaseDatabase
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

    fun getCompanions(onCompanionsReceived: (List<UserData>) -> Unit) {
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

//                  add companions size:
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

    fun getHomeChats(onChatsReceived: (List<ChatStatus>) -> Unit) {
        chatsRef.child(currentUser.userId!!).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatsList = mutableListOf<ChatStatus>()
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

                    onChatsReceived(chatsList)

//                  get chats size:
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
    ) = coroutineScope {
        val key = messagesRef.push().key ?: return@coroutineScope

        try {
            val messageData = Message(
                nameFrom = currentUser.username,
                message = message,
                status = MessageStatus(received = true),
                timeSent = time,
                date = date,
                dateTime = dateTime,
                id = key
            )

//          Create chats branch for user_1 (userFrom)
            val createUserFromChatBranch = async {
                createChatsBranch(
                    currentUser = companion,    // 'cause he receive it as 'currentUser'
                    companion = currentUser,
                    lastMessage = messageData
                )
            }

//          Create chats branch for user_2 (userTo)
            val createUserToChatBranch = async {
                createChatsBranch(
                    currentUser = currentUser,
                    companion = companion,
                    lastMessage = messageData
                )
            }

            awaitAll(createUserFromChatBranch, createUserToChatBranch)

//          Create msgs branch for currentUser
            val createCurrentUserMessagesBranch = async {
                createMessagesBranch(
                    currentUser = currentUser,
                    companion = companion,
                    key = key,
                    message = messageData
                )
            }

//          Create msgs branch for companion
            val createCompanionMessagesBranch = async {
                createMessagesBranch(
                    currentUser = companion,
                    companion = currentUser,
                    key = key,
                    message = messageData
                )
            }

            awaitAll(createCurrentUserMessagesBranch, createCompanionMessagesBranch)
        } catch (e: Exception) {
            Log.e("MyLog", "Failed to create a chat: ${e.message}")
        }

    }

    private suspend fun createChatsBranch(
        currentUser: UserData,
        companion: UserData,
        lastMessage: Message
    ) {
        chatsRef.child("${currentUser.userId}")
            .child("${currentUser.username}_${companion.username}")
            .setValue(
                ChatStatus(
                    currentUser = currentUser,
                    companion = companion,
                    lastMessage = lastMessage
                )
            ).await()
    }

    private suspend fun createMessagesBranch(
        currentUser: UserData,
        companion: UserData,
        key: String,
        message: Message
    ) {
        messagesRef.child("${currentUser.userId}")
            .child("${currentUser.username}_${companion.username}")
            .child(key)
            .setValue(message)
            .await()
    }


    fun updateLastMessage(companion: UserData) {
        messagesRef.child("${currentUser.userId}")
            .child("${currentUser.username}_${companion.username}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.lastOrNull()?.getValue(Message::class.java)?.let { lastMsg ->
                        updateChat(
                            currentUser = currentUser,
                            companion = companion,
                            lastMessage = lastMsg
                        )
                        updateChat(
                            currentUser = companion,
                            companion = currentUser,
                            lastMessage = lastMsg
                        )
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyLog", "Failed to read value in 'updateLastMessage': $error")
                }
            })
    }

    private fun updateChat(
        currentUser: UserData,
        companion: UserData,
        lastMessage: Message
    ) {
        val chatStatus = ChatStatus(
            currentUser = currentUser,
            companion = companion,
            lastMessage = lastMessage
        )

        val updateCurrentUserChat = hashMapOf<String, Any>(
            "${currentUser.username}_${companion.username}" to chatStatus
        )

        chatsRef.child(currentUser.userId!!).updateChildren(updateCurrentUserChat)
    }

}