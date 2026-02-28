package com.jellydrink.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.jellydrink.app.MainActivity
import com.jellydrink.app.R

object WaterNotificationHelper {

    private const val CHANNEL_ID = "water_progress_channel"
    private const val NOTIFICATION_ID = 1001

    /**
     * Converte ml in litri con precisione decimale
     * Esempi: 240ml -> "0,24", 1270ml -> "1,27", 2000ml -> "2"
     */
    private fun mlToLitersFormatted(ml: Int): String {
        val liters = ml / 1000.0
        // Formatta con 2 decimali
        val formatted = String.format("%.2f", liters)
        // Rimuovi zeri finali e separatori decimali inutili (2,00 -> 2, 1,50 -> 1,5, 0,24 -> 0,24)
        return formatted.trimEnd('0').trimEnd('.').trimEnd(',')
    }

    /**
     * Crea il canale di notifica (necessario per Android 8.0+)
     */
    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Progresso Idratazione",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Mostra il progresso dell'obiettivo giornaliero di acqua"
            setShowBadge(false)
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Mostra o aggiorna la notifica persistente con il progresso
     */
    fun showWaterProgressNotification(
        context: Context,
        currentMl: Int,
        goalMl: Int
    ) {
        createNotificationChannel(context)

        val percentage = if (goalMl > 0) {
            (currentMl.toFloat() / goalMl * 100).toInt().coerceAtLeast(0)
        } else {
            0
        }

        // Intent per aprire l'app quando si clicca sulla notifica
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Layout personalizzato per la notifica
        val notificationLayout = RemoteViews(context.packageName, R.layout.notification_water_progress).apply {
            setTextViewText(R.id.notification_percentage, "$percentage%")
            setTextViewText(R.id.notification_text, "${mlToLitersFormatted(currentMl)} / ${mlToLitersFormatted(goalMl)} L")
        }

        // Costruisci la notifica
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_jellyfish) // Icona piccola nella barra
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Non può essere rimossa dall'utente
            .setPriority(NotificationCompat.PRIORITY_LOW) // Non disturba
            .setShowWhen(false)
            .setAutoCancel(false)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

}
