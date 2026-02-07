package com.jellydrink.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.jellydrink.app.MainActivity
import com.jellydrink.app.R

object NotificationHelper {
    const val CHANNEL_ID_REMINDERS = "water_reminders"
    const val CHANNEL_NAME_REMINDERS = "Promemoria Acqua"

    const val NOTIFICATION_ID_REMINDER = 1001
    const val NOTIFICATION_ID_STREAK_DANGER = 1002

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

    fun showWaterReminderNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val messages = listOf(
            "La tua medusa ha sete! 🪼💧",
            "È ora di bere un po' d'acqua! 💧",
            "Non dimenticare di idratarti! 🌊",
            "La tua medusa ti aspetta! 🪼",
            "Un bicchiere d'acqua fa bene! 💧"
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle("JellyDrink")
            .setContentText(messages.random())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_REMINDER, notification)
    }

    fun showStreakDangerNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentTitle("Streak in pericolo! 🔥")
            .setContentText("Non hai ancora raggiunto l'obiettivo di oggi!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_STREAK_DANGER, notification)
    }
}
