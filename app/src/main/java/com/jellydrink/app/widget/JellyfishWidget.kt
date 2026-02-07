package com.jellydrink.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jellydrink.app.MainActivity
import com.jellydrink.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Context.dataStore by preferencesDataStore(name = "settings")

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
         * Aggiorna tutti i widget attivi
         */
        fun updateAllWidgets(context: Context) {
            val intent = Intent(context, JellyfishWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, JellyfishWidget::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
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
            // Ottieni database e DAO
            val db = com.jellydrink.app.data.db.AppDatabase::class.java
                .getDeclaredMethod("getInstance", Context::class.java)
                .invoke(null, context) as com.jellydrink.app.data.db.AppDatabase

            val waterIntakeDao = db.waterIntakeDao()
            val dataStore = context.dataStore

            val today = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            // Ottieni dati reali
            val currentMl = waterIntakeDao.getTotalForDate(today).first()
            val goalMl = dataStore.data.first()[intPreferencesKey("daily_goal")] ?: 2000

            withContext(Dispatchers.Main) {
                updateWidgetView(context, appWidgetManager, appWidgetId, currentMl, goalMl)
            }
        } catch (e: Exception) {
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
        (currentMl.toFloat() / goalMl * 100).toInt().coerceIn(0, 100)
    } else {
        0
    }

    // Crea RemoteViews
    val views = RemoteViews(context.packageName, R.layout.widget_jellyfish)

    // Aggiorna la percentuale con i dati reali
    views.setTextViewText(R.id.widget_percentage, "$percentage%")

    // Aggiorna la ProgressBar per il riempimento (più affidabile di ClipDrawable)
    views.setProgressBar(R.id.widget_progress, 100, percentage, false)

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
