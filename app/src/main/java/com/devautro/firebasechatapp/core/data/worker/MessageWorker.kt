package com.devautro.firebasechatapp.core.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.devautro.firebasechatapp.chatsHome.data.model.ChatStatus
import com.devautro.firebasechatapp.core.data.model.UserData
import com.devautro.firebasechatapp.core.data.notifications.NotificationService
import com.devautro.firebasechatapp.users.data.model.Request
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

private val database = Firebase.database
private val chatsRef = database.getReference("chats")
private val requestsRef = database.getReference("requests")
private val auth = Firebase.auth
val currentUser = UserData(
    auth.currentUser?.uid,
    auth.currentUser?.displayName,
    auth.currentUser?.photoUrl.toString()
)

@HiltWorker
class MessageWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationService: NotificationService
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        try {
            fetchNewMessages()
            fetchIncomingRequests()
            return Result.success()
        } catch (e: Exception) {
            Log.e("MessageWorker", "Error: ${e.localizedMessage}")
            return Result.failure()
        }

    }

    private suspend fun fetchNewMessages() {
        val snapshot = chatsRef.child(currentUser.userId!!).get().await()
        for (s in snapshot.children) {
            val chatStatus = s.getValue(ChatStatus::class.java)
            chatStatus?.lastMessage?.let { lastMessage ->
                if (lastMessage.dateTime != null && lastMessage.nameFrom != currentUser.username &&
                    !lastMessage.status.read
                ) {
                    notificationService.showNotification(
                        notificationId = lastMessage.message ?: "oops...smth went wrong",
                        name = lastMessage.nameFrom ?: "Unknown",
                        msg = lastMessage.message ?: "oops...smth went wrong"
                    )
                }
            }
        }
    }

    private suspend fun fetchIncomingRequests() {
        val snapshot = requestsRef.child(currentUser.userId!!).child("Incoming").get().await()
        for (s in snapshot.children) {
            val incomingRequest = s.getValue(Request::class.java)
            if (incomingRequest != null && !incomingRequest.isAnswered) {
                incomingRequest.from?.username?.let { username ->
                    notificationService.showRequestNotification(username)
                }
            }
        }
    }
}