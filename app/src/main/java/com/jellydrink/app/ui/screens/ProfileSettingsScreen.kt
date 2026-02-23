package com.jellydrink.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import android.app.Activity
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.jellydrink.app.util.LanguagePreference
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jellydrink.app.R
import com.jellydrink.app.ui.components.XpBar
import com.jellydrink.app.viewmodel.ProfileViewModel
import com.jellydrink.app.viewmodel.SettingsViewModel

private fun formatLiters(ml: Int): String {
    return when {
        ml % 1000 == 0 -> "${ml / 1000}L"
        ml % 100 == 0 -> "%.1fL".format(ml / 1000f)
        else -> "%.2fL".format(ml / 1000f)
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val profileState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var editingGlassIndex by remember { mutableStateOf(-1) }
    var editGlassInput by remember { mutableStateOf("") }

    LaunchedEffect(settingsState.resetDone) {
        if (settingsState.resetDone) {
            snackbarHostState.showSnackbar(context.getString(R.string.snackbar_data_deleted))
            settingsViewModel.dismissResetDone()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Profile Header (level + XP) ---
            item {
                ProfileHeader(
                    level = profileState.level,
                    xp = profileState.xp,
                    xpForCurrentLevel = profileState.xpForCurrentLevel,
                    xpForNextLevel = profileState.xpForNextLevel
                )
            }

            // --- Statistics grid ---
            item {
                StatisticsSection(
                    totalLiters = profileState.totalLiters,
                    bestStreak = profileState.bestStreak,
                    activeDays = profileState.activeDays,
                    dailyRecord = profileState.dailyRecord
                )
            }

            // --- Obiettivo giornaliero ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.settings_daily_goal),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatLiters(settingsState.dailyGoal),
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = settingsState.dailyGoal.toFloat(),
                            onValueChange = { settingsViewModel.updateDailyGoal(it.toInt()) },
                            valueRange = 500f..5000f,
                            steps = 17
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.settings_goal_min), style = MaterialTheme.typography.bodySmall)
                            Text(stringResource(R.string.settings_goal_max), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // --- Bicchieri predefiniti ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.settings_glasses_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.settings_glasses_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            settingsState.customGlasses.forEachIndexed { index, glass ->
                                InputChip(
                                    selected = false,
                                    onClick = {
                                        editingGlassIndex = index
                                        editGlassInput = "%.2f".format(glass / 1000f)
                                    },
                                    label = { Text(formatLiters(glass)) }
                                )
                            }
                        }
                    }
                }
            }

            // --- Notifiche ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.settings_reminders_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = stringResource(R.string.settings_reminders_subtitle),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                        Switch(
                            checked = settingsState.notificationsEnabled,
                            onCheckedChange = { settingsViewModel.toggleNotifications(it) }
                        )
                    }
                }
            }

            // --- Lingua ---
            item {
                LanguageSelector()
            }

            // --- Reset dati ---
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { settingsViewModel.showResetConfirm() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.settings_delete_data))
                }
            }

            item { Spacer(modifier = Modifier.height(56.dp)) }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    // Dialog modifica bicchiere
    if (editingGlassIndex >= 0) {
        AlertDialog(
            onDismissRequest = { editingGlassIndex = -1 },
            title = { Text(stringResource(R.string.dialog_edit_glass_title)) },
            text = {
                OutlinedTextField(
                    value = editGlassInput,
                    onValueChange = { input ->
                        val filtered = input.replace(',', '.')
                            .filter { c -> c.isDigit() || c == '.' }
                        val parts = filtered.split(".")
                        editGlassInput = when {
                            parts.size <= 1 -> filtered
                            else -> parts[0] + "." + parts[1].take(2)
                        }
                    },
                    label = { Text(stringResource(R.string.dialog_edit_glass_label)) },
                    suffix = { Text("L") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val liters = editGlassInput.replace(',', '.').toFloatOrNull()
                        if (liters != null && liters > 0f) {
                            val ml = (liters * 1000).toInt()
                            settingsViewModel.updateGlassAt(editingGlassIndex, ml)
                        }
                        editingGlassIndex = -1
                    }
                ) {
                    Text(stringResource(R.string.dialog_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { editingGlassIndex = -1 }) {
                    Text(stringResource(R.string.dialog_cancel))
                }
            }
        )
    }

    // Dialog conferma reset
    if (settingsState.showResetConfirm) {
        AlertDialog(
            onDismissRequest = { settingsViewModel.dismissResetConfirm() },
            title = { Text(stringResource(R.string.dialog_reset_title)) },
            text = {
                Text(stringResource(R.string.dialog_reset_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        settingsViewModel.resetAllData()
                        settingsViewModel.dismissResetConfirm()
                    }
                ) {
                    Text(stringResource(R.string.dialog_reset_confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { settingsViewModel.dismissResetConfirm() }) {
                    Text(stringResource(R.string.dialog_cancel))
                }
            }
        )
    }
}

@Composable
private fun ProfileHeader(
    level: Int,
    xp: Int,
    xpForCurrentLevel: Int,
    xpForNextLevel: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFD700), Color(0xFFFF9800))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$level",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.profile_level, level),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            XpBar(
                level = level,
                currentXp = xp,
                xpForCurrentLevel = xpForCurrentLevel,
                xpForNextLevel = xpForNextLevel
            )
        }
    }
}

@Composable
private fun StatisticsSection(
    totalLiters: Float,
    bestStreak: Int,
    activeDays: Int,
    dailyRecord: Int
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.WaterDrop,
                iconColor = Color(0xFF2196F3),
                title = stringResource(R.string.stat_total_liters),
                value = String.format("%.1fL", totalLiters)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalFireDepartment,
                iconColor = Color(0xFFFF5722),
                title = stringResource(R.string.stat_best_streak),
                value = stringResource(R.string.stat_streak_value, bestStreak)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Today,
                iconColor = Color(0xFF4CAF50),
                title = stringResource(R.string.stat_active_days),
                value = "$activeDays"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Star,
                iconColor = Color(0xFFFFD700),
                title = stringResource(R.string.stat_daily_record),
                value = formatLiters(dailyRecord)
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

private data class LanguageOption(val tag: String, val label: String)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LanguageSelector() {
    val context = LocalContext.current
    val languages = listOf(
        LanguageOption("it", stringResource(R.string.lang_it)),
        LanguageOption("en", stringResource(R.string.lang_en)),
        LanguageOption("fr", stringResource(R.string.lang_fr)),
        LanguageOption("es", stringResource(R.string.lang_es))
    )

    var currentTag by remember { mutableStateOf(LanguagePreference.getCurrentTag(context)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.settings_language_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                languages.forEach { lang ->
                    val selected = currentTag == lang.tag
                    InputChip(
                        selected = selected,
                        onClick = {
                            LanguagePreference.setTag(context, lang.tag)
                            currentTag = lang.tag
                            (context as? Activity)?.recreate()
                        },
                        label = { Text(lang.label) }
                    )
                }
            }
        }
    }
}
