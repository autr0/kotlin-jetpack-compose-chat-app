package com.devautro.firebasechatapp.chatScreen.data

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.devautro.firebasechatapp.chatScreen.data.model.MessagesDate
import com.devautro.firebasechatapp.core.data.dateTimeToLocalTimeZone
import com.devautro.firebasechatapp.core.data.model.Message
import com.devautro.firebasechatapp.core.data.model.MessageStatus
import com.devautro.firebasechatapp.core.data.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) {
    private val messagesRef = database.getReference("messages")
    var currentUser = UserData(
        auth.currentUser?.uid,
        auth.currentUser?.displayName,
        auth.currentUser?.photoUrl.toString()
    )
    private val onlineStateRef = database.getReference("onlineState")

    fun updateCurrentUser() {
        val user = auth.currentUser
        currentUser = UserData(
            user?.uid,
            user?.displayName,
            user?.photoUrl.toString()
        )
    }

    suspend fun getMessages(
        msgsList: SnapshotStateList<Message>,
        companion: UserData,
        msgsDateList: SnapshotStateList<MessagesDate>
    ) {
        messagesRef.child("${currentUser.userId}")
            .child("${currentUser.username}_${companion.username}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    msgsList.clear()
                    msgsDateList.clear()
                    val sLast = snapshot.children.lastOrNull()
                    val lastMsg = sLast?.getValue(Message::class.java)
                    for (s in snapshot.children) {
                        val msg = s.getValue(Message::class.java)
                        if (msg != null && !msgsList.contains(msg)) {
                            val dt = dateTimeToLocalTimeZone(msg.dateTime!!)
                            msgsList.add(
                                msg.copy(
                                    date = dt.date,
                                    timeSent = dt.time
                                )
                            )

//                          Check this out!
                            if (msg == lastMsg) {
                                msgsDateList.addAll(
                                    msgsList.groupBy { it.date }.map {
                                        MessagesDate(
                                            date = it.key.toString(),
                                            messages = it.value
                                        )
                                    }
                                )
                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyLog", "Failed to read value in 'getMessages': $error")
                }
            })


    }

    suspend fun updateMessageStatus(
        msgsList: SnapshotStateList<Message>,
        companion: UserData
    ) {
        val updatedMessageStatus = hashMapOf<String, Any>()

        msgsList.forEach {msg ->
            if (!msg.status.read && msg.nameFrom != currentUser.username) {
                val dt = dateTimeToLocalTimeZone(msg.dateTime!!)
                updatedMessageStatus.putIfAbsent(
                    msg.id!!,
                    msg.copy(
                        date = dt.date,
                        timeSent = dt.time,
                        status = MessageStatus(read = true)
                    )
                )
            }
        }

        messagesRef.child(companion.userId!!)
            .child("${companion.username}_${currentUser.username}")
            .updateChildren(updatedMessageStatus)

        messagesRef.child(currentUser.userId!!)
            .child("${currentUser.username}_${companion.username}")
            .updateChildren(updatedMessageStatus)

    }


    suspend fun addMessage(
        companion: UserData,
        message: String,
        time: String,
        date: String,
        dateTime: String
    ) {
        val key = messagesRef.push().key

        messagesRef.child("${currentUser.userId}")
            .child("${currentUser.username}_${companion.username}")
            .child(key /*messagesRef.push().key*/ ?: "unknownMessage")
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

        messagesRef.child("${companion.userId}")
            .child("${companion.username}_${currentUser.username}")
            .child(key/*messagesRef.push().key*/ ?: "unknownMessage")
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

    suspend fun deleteMessage(
        companion: UserData,
        key: String,
        removeFromCompanion: Boolean
    ) {
        messagesRef.child(currentUser.userId!!)
            .child("${currentUser.username}_${companion.username}")
            .child(key)
            .removeValue()

        if (removeFromCompanion) {
            messagesRef.child(companion.userId!!)
                .child("${companion.username}_${currentUser.username}")
                .child(key)
                .removeValue()
        }

    }

    suspend fun getCompanionOnlineStatus(onlineState: SnapshotStateList<Boolean>, companionId: String) {
        if (companionId.isNotEmpty()) {
            onlineStateRef.child(companionId).addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        onlineState.clear()
                        val companionStatus = snapshot.getValue(Boolean::class.java)
                        if (companionStatus != null && companionId.isNotEmpty()) {
                            onlineState.add(companionStatus)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("MyLog", "Failed to read value in 'getCompanionOnlineStatus': $error")
                    }
                }
            )
        }
    }

}