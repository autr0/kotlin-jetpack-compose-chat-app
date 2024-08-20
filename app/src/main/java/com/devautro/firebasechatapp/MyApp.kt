package com.devautro.firebasechatapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.devautro.firebasechatapp.core.data.notifications.NOTIFICATION_CHANNEL_ID
import com.devautro.firebasechatapp.core.data.notifications.NOTIFICATION_CHANNEL_ID_2
import com.devautro.firebasechatapp.core.data.notifications.NOTIFICATION_CHANNEL_NAME
import com.devautro.firebasechatapp.core.data.notifications.NOTIFICATION_CHANNEL_NAME_2
import com.devautro.firebasechatapp.core.data.worker.CustomWorkerFactory
import com.devautro.firebasechatapp.core.data.worker.MessageWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MyApp: Application(), Configuration.Provider {

    @Inject
    lateinit var customWorkerFactory: CustomWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(customWorkerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        WorkManager.initialize(this, workManagerConfiguration) // look into 'Manifest' file
        createNotificationChannels()
        startWorker()
    }

    private fun createNotificationChannels() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel)

        val notificationChannel2 = NotificationChannel(
            NOTIFICATION_CHANNEL_ID_2,
            NOTIFICATION_CHANNEL_NAME_2,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(notificationChannel2)
    }

    private fun startWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<MessageWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "NotificationWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}