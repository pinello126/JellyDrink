package com.jellydrink.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jellydrink.app.data.repository.WaterRepository
import com.jellydrink.app.notification.WaterNotificationHelper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BootEntryPoint {
        fun waterRepository(): WaterRepository
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            MidnightResetReceiver.scheduleMidnightAlarm(context.applicationContext)

            val pendingResult = goAsync()
            val appContext = context.applicationContext
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            scope.launch {
                try {
                    val repository = EntryPointAccessors
                        .fromApplication(appContext, BootEntryPoint::class.java)
                        .waterRepository()

                    if (repository.getNotificationsEnabled().first()) {
                        ReminderScheduler.scheduleAll(appContext)
                    }

                    val currentMl = repository.getTodayTotal().first()
                    val goal = repository.getDailyGoal().first()
                    WaterNotificationHelper.showWaterProgressNotification(appContext, currentMl, goal)
                } catch (e: Exception) {
                    WaterNotificationHelper.showWaterProgressNotification(appContext, 0, 2000)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
