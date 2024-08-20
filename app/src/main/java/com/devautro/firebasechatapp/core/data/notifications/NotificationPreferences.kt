package com.devautro.firebasechatapp.core.data.notifications

import android.content.Context
import javax.inject.Inject

class NotificationPreferences @Inject constructor(
    context: Context
) {
    private val sharedPreferences = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)

    fun isNotificationSent(notificationId: String, channelId: String): Boolean {
        return sharedPreferences.getBoolean("$notificationId:$channelId", false)
    }

    fun setNotificationSent(notificationId: String, channelId: String) {
        sharedPreferences.edit().putBoolean("$notificationId:$channelId", true).apply()
    }
}