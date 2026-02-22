package com.jellydrink.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.jellydrink.app.data.repository.WaterRepository
import com.jellydrink.app.notification.WaterNotificationHelper
import com.jellydrink.app.ui.navigation.JellyDrinkNavGraph
import com.jellydrink.app.ui.theme.JellyDrinkTheme
import com.jellydrink.app.receiver.MidnightResetReceiver
import com.jellydrink.app.worker.StreakDangerWorker
import com.jellydrink.app.worker.WaterReminderWorker
import com.jellydrink.app.util.LanguagePreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var waterRepository: WaterRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scheduleNotificationWorkers()
            initializeWaterProgressNotification()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val tag = LanguagePreference.getStoredTag(newBase)
        if (tag.isEmpty()) {
            super.attachBaseContext(newBase)
        } else {
            super.attachBaseContext(LanguagePreference.applyLocale(newBase, tag))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    scheduleNotificationWorkers()
                    initializeWaterProgressNotification()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            scheduleNotificationWorkers()
            initializeWaterProgressNotification()
        }

        setContent {
            JellyDrinkTheme {
                JellyDrinkNavGraph()
            }
        }
    }

    private fun scheduleNotificationWorkers() {
        val workManager = WorkManager.getInstance(applicationContext)

        // Water reminder every 2 hours
        val reminderRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            2, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "water_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )

        // Streak danger check - schedule for 21:00
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 21)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (currentTime.after(targetTime)) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        val initialDelay = targetTime.timeInMillis - currentTime.timeInMillis

        val streakRequest = PeriodicWorkRequestBuilder<StreakDangerWorker>(
            1, TimeUnit.DAYS
        ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS).build()

        workManager.enqueueUniquePeriodicWork(
            "streak_danger",
            ExistingPeriodicWorkPolicy.KEEP,
            streakRequest
        )

        // Midnight reset - usa AlarmManager per esecuzione precisa a mezzanotte
        MidnightResetReceiver.scheduleMidnightAlarm(applicationContext)
    }

    private fun initializeWaterProgressNotification() {
        scope.launch {
            try {
                val currentTotal = waterRepository.getTodayTotal().first()
                val goal = waterRepository.getDailyGoal().first()

                WaterNotificationHelper.showWaterProgressNotification(
                    applicationContext,
                    currentTotal,
                    goal
                )
            } catch (e: Exception) {
                // Fallback con valori di default
                WaterNotificationHelper.showWaterProgressNotification(
                    applicationContext,
                    0,
                    2000
                )
            }
        }
    }
}
