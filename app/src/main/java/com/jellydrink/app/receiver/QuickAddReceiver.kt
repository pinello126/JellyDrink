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

        // Chiudi la notifica immediatamente
        (appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .cancel(notifId)

        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            try {
                val repository = EntryPointAccessors
                    .fromApplication(appContext, QuickAddEntryPoint::class.java)
                    .waterRepository()
                // Aggiunge acqua con tutta la logica (XP, badge, streak, widget, notifica)
                repository.addWaterIntake(amountMl)
            } catch (e: Exception) {
                // silently fail
            } finally {
                pendingResult.finish()
            }
        }
    }
}
