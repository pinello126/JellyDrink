package com.jellydrink.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jellydrink.app.data.repository.WaterRepository
import com.jellydrink.app.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class StreakDangerWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: WaterRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Streak danger notifications disabled (keeping only lock screen notification)
        return Result.success()
    }
}
