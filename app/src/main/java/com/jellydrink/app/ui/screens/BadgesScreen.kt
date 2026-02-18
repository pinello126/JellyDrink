package com.jellydrink.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jellydrink.app.data.repository.WaterRepository
import com.jellydrink.app.data.repository.WaterRepository.Companion.BADGE_CATEGORIES_ORDER
import com.jellydrink.app.ui.components.BadgeCard
import com.jellydrink.app.viewmodel.ProfileViewModel

@Composable
fun BadgesScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val totalBadges = uiState.badges.size
    val earnedBadges = uiState.badges.count { it.isEarned }
    val badgesByCategory = uiState.badges.groupBy { it.category }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header with total progress
        item {
            Spacer(modifier = Modifier.height(4.dp))
            BadgesHeader(earned = earnedBadges, total = totalBadges)
        }

        // Sections by category
        BADGE_CATEGORIES_ORDER.forEach { category ->
            val badges = badgesByCategory[category] ?: return@forEach
            val categoryEarned = badges.count { it.isEarned }

            item(key = "header_$category") {
                CategoryHeader(
                    name = category,
                    earned = categoryEarned,
                    total = badges.size,
                    icon = categoryIcon(category)
                )
            }

            items(badges, key = { it.type }) { badge ->
                BadgeCard(badge = badge)
            }
        }

        item { Spacer(modifier = Modifier.height(56.dp)) }
    }
}

@Composable
private fun BadgesHeader(earned: Int, total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Circular progress
            Box(
                modifier = Modifier.size(72.dp),
                contentAlignment = Alignment.Center
            ) {
                val progress = if (total > 0) earned.toFloat() / total else 0f
                val primaryColor = MaterialTheme.colorScheme.primary
                val trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)

                Canvas(modifier = Modifier.size(72.dp)) {
                    val strokeWidth = 8.dp.toPx()
                    // Track
                    drawCircle(
                        color = trackColor,
                        radius = (size.minDimension - strokeWidth) / 2f,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // Progress
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "$earned/$total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column {
                Text(
                    text = "Badge e Traguardi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (earned == total && total > 0) "Tutti sbloccati!" else "Completa gli obiettivi per sbloccarli tutti!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun CategoryHeader(
    name: String,
    earned: Int,
    total: Int,
    icon: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "$earned/$total",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (earned == total) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { if (total > 0) earned.toFloat() / total else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

private fun categoryIcon(category: String): String = when (category) {
    WaterRepository.CAT_PRIMI_PASSI -> "\uD83D\uDC23" // hatching chick
    WaterRepository.CAT_STREAK -> "\uD83D\uDD25"      // fire
    WaterRepository.CAT_LITRI -> "\uD83D\uDCA7"       // droplet
    WaterRepository.CAT_GIORNI -> "\uD83D\uDCC5"       // calendar
    WaterRepository.CAT_LIVELLI -> "\u2B50"            // star
    WaterRepository.CAT_SFIDE -> "\uD83C\uDFC6"       // trophy
    else -> "\uD83C\uDFC5"                              // medal
}
