package com.devautro.firebasechatapp.core.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.devautro.firebasechatapp.core.data.notifications.NotificationService
import javax.inject.Inject

class CustomWorkerFactory @Inject constructor(
    private val notificationService: NotificationService
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            MessageWorker::class.java.name -> MessageWorker(appContext, workerParameters, notificationService)
            else -> null
        }
    }
}