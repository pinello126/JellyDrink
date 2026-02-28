package com.jellydrink.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.room.Room
import com.jellydrink.app.MainActivity
import com.jellydrink.app.R
import com.jellydrink.app.data.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Widget che mostra la medusa e il progresso dell'obiettivo giornaliero
 */
class JellyfishWidget : AppWidgetProvider() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Aggiorna tutti i widget
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Widget aggiunto per la prima volta
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context) {
        // Ultimo widget rimosso
        super.onDisabled(context)
    }

    companion object {
        /**
         * Aggiorna tutti i widget attivi IMMEDIATAMENTE
         */
        fun updateAllWidgets(context: Context) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val ids = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, JellyfishWidget::class.java)
                )

                android.util.Log.d("JellyfishWidget", "Aggiornamento forzato di ${ids.size} widget(s)")

                if (ids.isNotEmpty()) {
                    // Aggiornamento diretto e immediato
                    val intent = Intent(context, JellyfishWidget::class.java).apply {
                        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                    }
                    context.sendBroadcast(intent)

                    // Backup: update manuale immediato
                    ids.forEach { widgetId ->
                        updateAppWidget(context, appWidgetManager, widgetId)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("JellyfishWidget", "Errore update widget", e)
            }
        }
    }
}

/**
 * Aggiorna il contenuto del widget
 */
private fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Ottieni i dati dal database usando coroutines
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    scope.launch {
        try {
            // Piccolo delay per assicurarsi che il DB sia aggiornato
            delay(100)

            // Ottieni database correttamente (come in AppModule)
            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "jellydrink_db"
            )
                .addMigrations(
                    AppDatabase.MIGRATION_1_2,
                    AppDatabase.MIGRATION_2_3,
                    AppDatabase.MIGRATION_3_4,
                    AppDatabase.MIGRATION_4_5,
                    AppDatabase.MIGRATION_5_6
                )
                .build()

            val waterIntakeDao = db.waterIntakeDao()

            val today = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            android.util.Log.d("JellyfishWidget", "Data query: $today")

            // Ottieni dati reali
            val currentMl = waterIntakeDao.getTotalForDate(today).first()
            val goalMl = 2000 // Default goal value

            android.util.Log.d("JellyfishWidget", "Dati DB: currentMl=$currentMl, goalMl=$goalMl, today=$today")

            withContext(Dispatchers.Main) {
                updateWidgetView(context, appWidgetManager, appWidgetId, currentMl, goalMl)
            }
        } catch (e: Exception) {
            // Log errore per debug
            android.util.Log.e("JellyfishWidget", "Errore aggiornamento widget", e)
            // Fallback con valori placeholder
            withContext(Dispatchers.Main) {
                updateWidgetView(context, appWidgetManager, appWidgetId, 0, 2000)
            }
        }
    }
}

private fun updateWidgetView(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    currentMl: Int,
    goalMl: Int
) {
    val percentage = if (goalMl > 0) {
        (currentMl.toFloat() / goalMl * 100).toInt().coerceAtLeast(0)
    } else {
        0
    }

    // Crea RemoteViews
    val views = RemoteViews(context.packageName, R.layout.widget_jellyfish)

    // Aggiorna la percentuale con i dati reali
    views.setTextViewText(R.id.widget_percentage, "$percentage%")

    // Log per debug
    android.util.Log.d("JellyfishWidget", "Widget aggiornato: $currentMl/$goalMl ml = $percentage%")

    // Intent per aprire l'app quando si clicca sul widget
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

    // Aggiorna il widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
