package com.example.nomoosugar.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        NotificationHelper.showReminder(
            applicationContext,
            "Sugar Reminder",
            "Don't forget to track your daily sugar intake!"
        )
        return Result.success()
    }
}
