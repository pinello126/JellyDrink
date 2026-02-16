package com.jellydrink.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jellydrink.app.data.repository.WaterRepository
import com.jellydrink.app.notification.WaterNotificationHelper
import com.jellydrink.app.widget.JellyfishWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class MidnightResetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: WaterRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val goalMl = repository.getDailyGoal().first()

            // A mezzanotte il totale di oggi è 0
            WaterNotificationHelper.showWaterProgressNotification(
                context,
                0,
                goalMl
            )

            // Aggiorna anche il widget
            JellyfishWidget.updateAllWidgets(context)

            Result.success()
        } catch (e: Exception) {
            WaterNotificationHelper.showWaterProgressNotification(
                context,
                0,
                2000
            )
            Result.success()
        }
    }
}
