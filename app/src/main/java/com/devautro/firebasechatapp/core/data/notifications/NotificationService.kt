package com.devautro.firebasechatapp.core.data.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.devautro.firebasechatapp.MainActivity
import com.devautro.firebasechatapp.R
import javax.inject.Inject
import javax.inject.Singleton

const val NOTIFICATION_CHANNEL_ID = "ch-1"
const val NOTIFICATION_CHANNEL_NAME = "Chat Notifications"
const val REQUEST_CODE = 200
const val NOTIFICATION_CHANNEL_ID_2 = "ch-2"
const val NOTIFICATION_CHANNEL_NAME_2 = "Request Notifications"

@Singleton
class NotificationService @Inject constructor(
    private val context: Context,
    private val notificationPreferences: NotificationPreferences
) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val myIntent = Intent(context, MainActivity::class.java)
    private val pendingIntent = PendingIntent.getActivity(
        context,
        REQUEST_CODE,
        myIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    fun showNotification(notificationId: String, name: String, msg: String) {
        if (notificationPreferences.isNotificationSent(notificationId, NOTIFICATION_CHANNEL_ID)) {
            return
        }

        val notification =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.message_icon)
                .setContentTitle(name)
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()

        notificationManager.notify(notificationId.hashCode() + notificationId.count(), notification)
        notificationPreferences.setNotificationSent(notificationId, NOTIFICATION_CHANNEL_ID)

    }

    fun showRequestNotification(notificationId: String) {
        if (notificationPreferences.isNotificationSent(notificationId, NOTIFICATION_CHANNEL_ID_2)) {
            return
        }

        val notification =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_2)
                .setSmallIcon(R.drawable.person_add)
                .setContentTitle("${R.string.notification_request} $notificationId")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build()

        notificationManager.notify(notificationId.hashCode(), notification)
        notificationPreferences.setNotificationSent(notificationId, NOTIFICATION_CHANNEL_ID_2)
    }
}