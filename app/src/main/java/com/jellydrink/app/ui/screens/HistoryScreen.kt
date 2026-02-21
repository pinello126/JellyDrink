package com.jellydrink.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jellydrink.app.data.db.dao.BeerDailySummary
import com.jellydrink.app.data.db.dao.DailySummary
import com.jellydrink.app.ui.theme.JellyBlue
import com.jellydrink.app.ui.theme.SuccessGreen
import com.jellydrink.app.viewmodel.HistoryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun formatLiters(ml: Int): String = when {
    ml % 1000 == 0 -> "${ml / 1000}L"
    ml % 100 == 0  -> "%.1fL".format(ml / 1000f)
    else           -> "%.2fL".format(ml / 1000f)
}

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Titolo
        item {
            Text(
                text = "Storico",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Toggle Acqua / Birra
        item {
            ModeToggle(
                showBeer = uiState.showBeer,
                onToggle = { viewModel.toggleMode() }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (!uiState.showBeer) {
            // Grafico settimanale acqua
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ultimi 7 giorni",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        WeeklyChart(
                            summaries = uiState.weekSummaries,
                            goalMl = uiState.goalMl,
                            goalPerDay = uiState.goalPerDay,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dettaglio giornaliero",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (uiState.dailySummaries.isEmpty()) {
                item {
                    Text(
                        text = "Nessun dato disponibile. Inizia a bere!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(uiState.dailySummaries) { summary ->
                    DaySummaryCard(
                        summary = summary,
                        goalMl = uiState.goalPerDay[summary.date] ?: uiState.goalMl
                    )
                }
            }
        } else {
            // Grafico settimanale birra
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ultimi 7 giorni",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        BeerWeeklyChart(
                            summaries = uiState.beerWeekSummaries,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dettaglio giornaliero",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            if (uiState.beerDailySummaries.isEmpty()) {
                item {
                    Text(
                        text = "Nessun dato disponibile. Inizia a tracciare la birra!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(uiState.beerDailySummaries) { summary ->
                    BeerDaySummaryCard(summary = summary)
                }
            }
        }

        // Spazio in fondo per la bottom nav
        item { Spacer(modifier = Modifier.height(56.dp)) }
    }
}

@Composable
private fun ModeToggle(
    showBeer: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = if (!showBeer) JellyBlue else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { if (showBeer) onToggle() }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Acqua",
                fontWeight = if (!showBeer) FontWeight.Bold else FontWeight.Normal,
                color = if (!showBeer) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = if (showBeer) Color(0xFFFF9800) else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { if (!showBeer) onToggle() }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Birra",
                fontWeight = if (showBeer) FontWeight.Bold else FontWeight.Normal,
                color = if (showBeer) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun BeerDaySummaryCard(
    summary: BeerDailySummary,
    modifier: Modifier = Modifier
) {
    val displayDate = try {
        val date = LocalDate.parse(summary.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ITALIAN))
    } catch (e: Exception) {
        summary.date
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF9800).copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${summary.totalCl} cl",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = if (summary.totalCl >= 100) "🍺🍺" else if (summary.totalCl > 0) "🍺" else "-",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun BeerWeeklyChart(
    summaries: List<BeerDailySummary>,
    modifier: Modifier = Modifier
) {
    val beerColor = Color(0xFFFF9800)

    Canvas(modifier = modifier) {
        val barWidth = size.width / (summaries.size * 2f)
        val maxCl = maxOf(100, summaries.maxOfOrNull { it.totalCl } ?: 0)
        val chartHeight = size.height - 30f
        val chartBottom = chartHeight

        summaries.forEachIndexed { index, summary ->
            val barHeight = (summary.totalCl.toFloat() / maxCl) * chartHeight
            val x = (index * 2 + 0.5f) * barWidth

            if (barHeight > 0f) {
                drawRoundRect(
                    color = beerColor,
                    topLeft = Offset(x, chartBottom - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }

            val dayLabel = try {
                val date = LocalDate.parse(summary.date)
                date.dayOfWeek.name.take(2)
            } catch (e: Exception) {
                ""
            }

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    dayLabel,
                    x + barWidth / 2f,
                    size.height,
                    android.graphics.Paint().apply {
                        this.color = android.graphics.Color.GRAY
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

@Composable
private fun DaySummaryCard(
    summary: DailySummary,
    goalMl: Int,
    modifier: Modifier = Modifier
) {
    val percentage = if (goalMl > 0) (summary.totalMl.toFloat() / goalMl * 100).toInt() else 0
    val isGoalMet = summary.totalMl >= goalMl
    val displayDate = try {
        val date = LocalDate.parse(summary.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ITALIAN))
    } catch (e: Exception) {
        summary.date
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isGoalMet)
                SuccessGreen.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${formatLiters(summary.totalMl)} / ${formatLiters(goalMl)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isGoalMet) SuccessGreen
                    else MaterialTheme.colorScheme.onSurface
                )
                if (isGoalMet) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Obiettivo raggiunto",
                        tint = SuccessGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyChart(
    summaries: List<DailySummary>,
    goalMl: Int,
    goalPerDay: Map<String, Int> = emptyMap(),
    modifier: Modifier = Modifier
) {
    val barColor = JellyBlue
    val goalLineColor = SuccessGreen
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    // Use the average of per-day goals for the goal line (fallback to global goal)
    val avgGoal = if (goalPerDay.isNotEmpty()) {
        val relevantGoals = summaries.map { goalPerDay[it.date] ?: goalMl }
        relevantGoals.average().toInt()
    } else {
        goalMl
    }

    Canvas(modifier = modifier) {
        val barWidth = size.width / (summaries.size * 2f)
        val maxMl = maxOf(avgGoal, summaries.maxOfOrNull { it.totalMl } ?: avgGoal)
        val chartHeight = size.height - 30f
        val chartBottom = chartHeight

        // Linea obiettivo (media dei goal del periodo)
        val goalY = chartBottom - (avgGoal.toFloat() / maxMl) * chartHeight
        drawLine(
            color = goalLineColor.copy(alpha = 0.5f),
            start = Offset(0f, goalY),
            end = Offset(size.width, goalY),
            strokeWidth = 2f,
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                floatArrayOf(10f, 10f)
            )
        )

        // Barre
        summaries.forEachIndexed { index, summary ->
            val dayGoal = goalPerDay[summary.date] ?: goalMl
            val barHeight = (summary.totalMl.toFloat() / maxMl) * chartHeight
            val x = (index * 2 + 0.5f) * barWidth
            val color = if (summary.totalMl >= dayGoal) SuccessGreen else barColor

            drawRoundRect(
                color = color,
                topLeft = Offset(x, chartBottom - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Label giorno
            val dayLabel = try {
                val date = LocalDate.parse(summary.date)
                date.dayOfWeek.name.take(2)
            } catch (e: Exception) {
                ""
            }

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    dayLabel,
                    x + barWidth / 2f,
                    size.height,
                    android.graphics.Paint().apply {
                        this.color = android.graphics.Color.GRAY
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}
