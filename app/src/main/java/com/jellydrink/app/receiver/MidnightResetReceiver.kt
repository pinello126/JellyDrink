package com.jellydrink.app.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.datastore.preferences.core.intPreferencesKey
import com.jellydrink.app.data.repository.dataStore
import com.jellydrink.app.notification.WaterNotificationHelper
import com.jellydrink.app.widget.JellyfishWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Ricevitore che resetta la notifica lock screen a mezzanotte.
 * Usa AlarmManager per garantire esecuzione precisa anche in Doze mode.
 */
class MidnightResetReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            try {
                val dataStore = context.dataStore
                val goalMl = dataStore.data.first()[intPreferencesKey("daily_goal")] ?: 2000

                // A mezzanotte il totale di oggi è 0
                WaterNotificationHelper.showWaterProgressNotification(
                    context.applicationContext,
                    0,
                    goalMl
                )

                // Aggiorna anche il widget
                JellyfishWidget.updateAllWidgets(context.applicationContext)
            } catch (e: Exception) {
                WaterNotificationHelper.showWaterProgressNotification(
                    context.applicationContext,
                    0,
                    2000
                )
            } finally {
                pendingResult.finish()
            }
        }

        // Ri-schedula per la prossima mezzanotte
        scheduleMidnightAlarm(context.applicationContext)
    }

    companion object {
        fun scheduleMidnightAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, MidnightResetReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Prossima mezzanotte
            val midnight = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 5) // 5 secondi dopo mezzanotte per sicurezza
                set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_MONTH, 1)
            }

            // Su Android 12+ setExactAndAllowWhileIdle richiede il permesso SCHEDULE_EXACT_ALARM
            // che l'utente deve concedere esplicitamente. Se non disponibile, fallback a inesatto.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, midnight.timeInMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, midnight.timeInMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, midnight.timeInMillis, pendingIntent)
            }
        }
    }
}
