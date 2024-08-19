package com.devautro.firebasechatapp.chatScreen.data

import android.util.Log
import com.devautro.firebasechatapp.core.data.dateTimeToLocalTimeZone
import com.devautro.firebasechatapp.core.data.model.Message
import com.devautro.firebasechatapp.core.data.model.MessageStatus
import com.devautro.firebasechatapp.core.data.model.UserData
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

class ChatRepository @Inject constructor(
    private val auth: FirebaseAuth,
    database: FirebaseDatabase
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

    fun getMessages(
        companion: UserData,
        onMessagesReceived: (List<Message>) -> Unit
    ) {
        messagesRef.child("${currentUser.userId}")
            .child("${currentUser.username}_${companion.username}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val msgsList = snapshot.children.mapNotNull { s ->
                        s.getValue(Message::class.java)?.let { msg ->
                            val dt = dateTimeToLocalTimeZone(msg.dateTime!!)
                            msg.copy(
                                date = dt.date,
                                timeSent = dt.time
                            )
                        }
                    }.distinctBy { it.id }

                    onMessagesReceived(msgsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MyLog", "Failed to read value in 'getMessages': $error")
                }
            })


    }

    suspend fun updateMessageStatus(
        msgsList: List<Message>,
        companion: UserData
    ) = coroutineScope {
        val updatedMessageStatus = hashMapOf<String, Any>()

        try {
            msgsList.mapNotNull { msg ->
                if (!msg.status.read && msg.nameFrom != currentUser.username) {
                    val dt = dateTimeToLocalTimeZone(msg.dateTime ?: return@mapNotNull null)

                    msg.id?.let { id ->
                        updatedMessageStatus[id] = msg.copy(
                            date = dt.date,
                            timeSent = dt.time,
                            status = MessageStatus(read = true)
                        )
                    }
                } else {
                    null
                }
            }

            val updateMessageStatusForCurrentUser = async {
                updateMessagesForUser(
                    currentUser = currentUser,
                    companion = companion,
                    updatedMessageStatus = updatedMessageStatus
                )
            }

            val updateMessageStatusForCompanion = async {
                updateMessagesForUser(
                    currentUser = companion,
                    companion = currentUser,
                    updatedMessageStatus = updatedMessageStatus
                )
            }

            awaitAll(updateMessageStatusForCurrentUser, updateMessageStatusForCompanion)
        } catch (e: Exception) {
            Log.e("MyLog", "Failed to update message status: ${e.message}")
        }

    }

    private suspend fun updateMessagesForUser(
        currentUser: UserData,
        companion: UserData,
        updatedMessageStatus: HashMap<String, Any>
    ) {
        messagesRef.child(currentUser.userId!!)
            .child("${currentUser.username}_${companion.username}")
            .updateChildren(updatedMessageStatus).await()
    }


    suspend fun addMessage(
        companion: UserData,
        message: String,
        time: String,
        date: String,
        dateTime: String
    ) = coroutineScope {
        val key = messagesRef.push().key ?: return@coroutineScope

        val messageData = Message(
            nameFrom = currentUser.username,
            message = message,
            status = MessageStatus(received = true),
            timeSent = time,
            date = date,
            dateTime = dateTime,
            id = key
        )

//      nested fun
        suspend fun saveMessage(
            currentUser: UserData,
            companion: UserData,
            key: String
        ) {
            try {
                messagesRef.child("${currentUser.userId}")
                    .child("${currentUser.username}_${companion.username}")
                    .child(key)
                    .setValue(messageData)
                    .await()
            } catch (e: Exception) {
                Log.e("MyLog", "Failed to add message: ${e.message}")
            }
        }

        val addMessageToCurrentUser = async {
            saveMessage(
                currentUser = currentUser,
                companion = companion,
                key = key
            )
        }

        val addMessageToCompanion = async {
            saveMessage(
                currentUser = companion,
                companion = currentUser,
                key = key
            )
        }

        awaitAll(addMessageToCurrentUser, addMessageToCompanion)
    }

    suspend fun deleteMessage(
        companion: UserData,
        key: String,
        removeFromCompanion: Boolean
    ) {
        try {
            removeMessage(
                currentUser = currentUser,
                companion = companion,
                key = key
            )

            if (removeFromCompanion) {
                removeMessage(
                    currentUser = companion,
                    companion = currentUser,
                    key = key
                )
            }
        } catch (e: Exception) {
            Log.e("MyLog", "Failed to delte message: ${e.message}")
        }

    }

    private suspend fun removeMessage(
        currentUser: UserData,
        companion: UserData,
        key: String
    ) {
        messagesRef.child(currentUser.userId!!)
            .child("${currentUser.username}_${companion.username}")
            .child(key)
            .removeValue()
            .await()
    }

    fun getCompanionOnlineStatus(
        companionId: String,
        onOnlineStatusReceived: (Boolean) -> Unit
    ) {
        if (companionId.isNotEmpty()) {
            onlineStateRef.child(companionId).addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val companionStatus = snapshot.getValue(Boolean::class.java)
                        onOnlineStatusReceived(companionStatus ?: false)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("MyLog", "Failed to read value in 'getCompanionOnlineStatus': $error")
                    }
                }
            )
        }
    }

}