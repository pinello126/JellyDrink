package com.jellydrink.app.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object ReminderScheduler {

    val REMINDER_HOURS = listOf(11, 14, 17, 21)
    private val REMINDER_MINUTES = listOf(0, 0, 0, 0)

    fun scheduleAll(context: Context) {
        cancelAll(context)
        REMINDER_HOURS.forEachIndexed { index, hour -> schedule(context, hour, index) }
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (i in 0..3) {
            val intent = Intent(context, WaterReminderReceiver::class.java)
            val pi = PendingIntent.getBroadcast(
                context, i, intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pi != null) alarmManager.cancel(pi)
        }
    }

    fun schedule(context: Context, hour: Int, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val minute = REMINDER_MINUTES.getOrElse(requestCode) { 0 }
        val triggerTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis

        val pendingIntent = buildPendingIntent(context, hour, requestCode)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    private fun buildPendingIntent(context: Context, hour: Int, requestCode: Int): PendingIntent {
        val intent = Intent(context, WaterReminderReceiver::class.java).apply {
            putExtra("hour", hour)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
