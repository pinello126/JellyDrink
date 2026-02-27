package com.jellydrink.app.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.jellydrink.app.MainActivity
import com.jellydrink.app.R
import com.jellydrink.app.data.repository.WaterRepository
import com.jellydrink.app.util.NotificationHelper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WaterReminderReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ReminderEntryPoint {
        fun waterRepository(): WaterRepository
    }

    override fun onReceive(context: Context, intent: Intent) {
        val hour = intent.getIntExtra("hour", 11)
        val appContext = context.applicationContext
        val pendingResult = goAsync()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val repository = EntryPointAccessors
                    .fromApplication(appContext, ReminderEntryPoint::class.java)
                    .waterRepository()

                if (!repository.getNotificationsEnabled().first()) return@launch

                val currentMl = repository.getTodayTotal().first()
                val goal = repository.getDailyGoal().first()
                if (currentMl >= goal) return@launch

                val glasses = repository.getCustomGlasses().first()
                showNotification(appContext, hour, glasses)
            } catch (e: Exception) {
                showNotification(appContext, hour, listOf(200, 500, 1000))
            } finally {
                scheduleNext(appContext, hour)
                pendingResult.finish()
            }
        }
    }

    private fun scheduleNext(context: Context, hour: Int) {
        val index = ReminderScheduler.REMINDER_HOURS.indexOf(hour)
        if (index >= 0) ReminderScheduler.schedule(context, hour, index)
    }

    private fun showNotification(context: Context, hour: Int, glasses: List<Int>) {
        NotificationHelper.createNotificationChannel(context)

        val message = when (hour) {
            11 -> context.getString(R.string.reminder_11)
            14 -> context.getString(R.string.reminder_14)
            17 -> context.getString(R.string.reminder_17)
            21 -> context.getString(R.string.reminder_21)
            else -> context.getString(R.string.reminder_11)
        }

        val notifId = 2000 + hour

        val openIntent = PendingIntent.getActivity(
            context,
            notifId,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification_jellyfish)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(openIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        glasses.take(3).forEachIndexed { i, ml ->
            val addIntent = Intent(context, QuickAddReceiver::class.java).apply {
                putExtra("amount_ml", ml)
                putExtra("notif_id", notifId)
            }
            val addPending = PendingIntent.getBroadcast(
                context,
                notifId * 10 + i,
                addIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(0, formatLiters(ml), addPending)
        }

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(notifId, builder.build())
    }

    private fun formatLiters(ml: Int): String = when {
        ml % 1000 == 0 -> "${ml / 1000}L"
        ml % 100 == 0 -> "%.1fL".format(ml / 1000f)
        else -> "%.2fL".format(ml / 1000f)
    }
}
