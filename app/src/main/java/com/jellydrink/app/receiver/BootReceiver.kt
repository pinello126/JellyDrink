package com.jellydrink.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.jellydrink.app.data.db.AppDatabase
import com.jellydrink.app.notification.WaterNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "settings")

/**
 * Ricevitore che mostra la notifica lock screen al boot del dispositivo
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Mostra la notifica lock screen con i dati correnti
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            scope.launch {
                try {
                    // Ottieni database
                    val db = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "jellydrink_db"
                    )
                        .addMigrations(
                            AppDatabase.MIGRATION_1_2,
                            AppDatabase.MIGRATION_2_3,
                            AppDatabase.MIGRATION_3_4,
                            AppDatabase.MIGRATION_4_5
                        )
                        .build()

                    val waterIntakeDao = db.waterIntakeDao()
                    val dataStore = context.dataStore

                    val today = java.time.LocalDate.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                    // Ottieni dati reali
                    val currentMl = waterIntakeDao.getTotalForDate(today).first()
                    val goalMl = dataStore.data.first()[intPreferencesKey("daily_goal")] ?: 2000

                    // Mostra la notifica
                    WaterNotificationHelper.showWaterProgressNotification(
                        context.applicationContext,
                        currentMl,
                        goalMl
                    )
                } catch (e: Exception) {
                    // Fallback con valori di default
                    WaterNotificationHelper.showWaterProgressNotification(
                        context.applicationContext,
                        0,
                        2000
                    )
                }
            }
        }
    }
}
