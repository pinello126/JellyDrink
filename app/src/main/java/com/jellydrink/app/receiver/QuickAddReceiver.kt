package com.jellydrink.app.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jellydrink.app.data.repository.WaterRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuickAddReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface QuickAddEntryPoint {
        fun waterRepository(): WaterRepository
    }

    override fun onReceive(context: Context, intent: Intent) {
        val amountMl = intent.getIntExtra("amount_ml", 0)
        val notifId = intent.getIntExtra("notif_id", 0)
        if (amountMl <= 0) return

        val appContext = context.applicationContext

        // Chiudi la notifica solo se non è la lock screen (che è persistente e si aggiorna)
        if (notifId != com.jellydrink.app.notification.WaterNotificationHelper.NOTIFICATION_ID) {
            (appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .cancel(notifId)
        }

        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            try {
                val repository = EntryPointAccessors
                    .fromApplication(appContext, QuickAddEntryPoint::class.java)
                    .waterRepository()
                // Limite 10L: non aggiungere se il totale giornaliero ha già raggiunto il massimo
                val currentTotal = repository.getTodayTotal().first()
                if (currentTotal >= 10000) return@launch
                // Aggiunge acqua con tutta la logica (XP, streak, widget, notifica)
                repository.addWaterIntake(amountMl)
                // Controlla e assegna badge; se sbloccato, lo salva in DataStore
                // così il popup apparirà alla prossima apertura della home
                val newTotal = repository.getTodayTotal().first()
                val goal = repository.getDailyGoal().first()
                val badge = repository.checkAndAwardBadges(newTotal, goal)
                if (badge != null) {
                    repository.setPendingBadge(badge.type)
                }
            } catch (e: Exception) {
                // silently fail
            } finally {
                pendingResult.finish()
            }
        }
    }
}
