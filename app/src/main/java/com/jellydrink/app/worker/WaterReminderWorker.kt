package com.jellydrink.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jellydrink.app.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalTime

@HiltWorker
class WaterReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val currentHour = LocalTime.now().hour

        // Only show notifications between 8:00 and 22:00
        if (currentHour in 8..21) {
            NotificationHelper.showWaterReminderNotification(context)
        }

        return Result.success()
    }
}
