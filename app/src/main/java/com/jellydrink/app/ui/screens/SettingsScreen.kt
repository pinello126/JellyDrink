package com.jellydrink.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var editingGlassIndex by remember { mutableStateOf(-1) }
    var editGlassInput by remember { mutableStateOf("") }

    LaunchedEffect(uiState.resetDone) {
        if (uiState.resetDone) {
            snackbarHostState.showSnackbar("Tutti i dati sono stati cancellati")
            viewModel.dismissResetDone()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Impostazioni",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Obiettivo giornaliero ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Obiettivo giornaliero",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatLiters(uiState.dailyGoal),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = uiState.dailyGoal.toFloat(),
                    onValueChange = { viewModel.updateDailyGoal(it.toInt()) },
                    valueRange = 500f..5000f,
                    steps = 17 // intervalli da 250ml
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0.5L", style = MaterialTheme.typography.bodySmall)
                    Text("5L", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Bicchieri predefiniti ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Bicchieri predefiniti",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Tocca per modificare",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    uiState.customGlasses.forEachIndexed { index, glass ->
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

        Spacer(modifier = Modifier.height(16.dp))

        // --- Notifiche ---
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Promemoria",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Ricevi notifiche per ricordarti di bere",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
                Switch(
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = { viewModel.toggleNotifications(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Reset dati ---
        Button(
            onClick = { viewModel.showResetConfirm() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cancella tutti i dati")
        }

        // Spazio in fondo per la bottom nav
        Spacer(modifier = Modifier.height(72.dp))
    }

    // Dialog modifica bicchiere
    if (editingGlassIndex >= 0) {
        AlertDialog(
            onDismissRequest = { editingGlassIndex = -1 },
            title = { Text("Modifica bicchiere") },
            text = {
                OutlinedTextField(
                    value = editGlassInput,
                    onValueChange = { input ->
                        // Allow digits, dot, and comma; max 4 chars (e.g. "1.50")
                        val filtered = input.replace(',', '.')
                            .filter { c -> c.isDigit() || c == '.' }
                        // Allow at most one dot and max 2 decimal places
                        val parts = filtered.split(".")
                        editGlassInput = when {
                            parts.size <= 1 -> filtered
                            else -> parts[0] + "." + parts[1].take(2)
                        }
                    },
                    label = { Text("Valore in litri") },
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
                            viewModel.updateGlassAt(editingGlassIndex, ml)
                        }
                        editingGlassIndex = -1
                    }
                ) {
                    Text("Salva")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingGlassIndex = -1 }) {
                    Text("Annulla")
                }
            }
        )
    }

    // Dialog conferma reset
    if (uiState.showResetConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissResetConfirm() },
            title = { Text("Conferma cancellazione") },
            text = {
                Text("Sei sicuro di voler cancellare tutti i dati? Questa azione non puo' essere annullata.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllData()
                        viewModel.dismissResetConfirm()
                    }
                ) {
                    Text("Cancella tutto", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissResetConfirm() }) {
                    Text("Annulla")
                }
            }
        )
    }

    // Snackbar
    SnackbarHost(hostState = snackbarHostState)
}
