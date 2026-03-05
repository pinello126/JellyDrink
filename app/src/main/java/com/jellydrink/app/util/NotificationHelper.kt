package com.jellydrink.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.jellydrink.app.R
import com.jellydrink.app.util.LanguagePreference

object NotificationHelper {
    const val CHANNEL_ID_REMINDERS = "water_reminders"

    fun createNotificationChannel(context: Context) {
        val tag = LanguagePreference.getStoredTag(context)
        val localizedContext = if (tag.isNotEmpty()) LanguagePreference.applyLocale(context, tag) else context

        val channel = NotificationChannel(
            CHANNEL_ID_REMINDERS,
            localizedContext.getString(R.string.channel_name_reminders),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = localizedContext.getString(R.string.channel_desc_reminders)
            enableVibration(true)
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
