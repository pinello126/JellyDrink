package com.jellydrink.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationHelper {
    const val CHANNEL_ID_REMINDERS = "water_reminders"
    private const val CHANNEL_NAME_REMINDERS = "Promemoria Acqua"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID_REMINDERS,
            CHANNEL_NAME_REMINDERS,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Promemoria per bere acqua"
            enableVibration(true)
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
