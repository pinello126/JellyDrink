package com.jellydrink.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jellydrink.app.ui.components.AquariumBackground
import com.jellydrink.app.ui.components.ChallengeCard
import com.jellydrink.app.ui.components.JellyFishView
import com.jellydrink.app.ui.components.WaterGlassSelector
import com.jellydrink.app.ui.components.WaterProgressBar
import com.jellydrink.app.ui.components.XpBar
import com.jellydrink.app.ui.theme.GoldBadge
import com.jellydrink.app.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Particella per l'effetto burst
private data class BurstParticle(
    val startX: Float,
    val startY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val radius: Float,
    val color: Color
)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToShop: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val newBadge by viewModel.newBadge.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // State for water FAB expansion
    var waterMenuExpanded by remember { mutableStateOf(false) }

    // Stato particelle
    var particles by remember { mutableStateOf<List<BurstParticle>>(emptyList()) }
    val particleProgress = remember { Animatable(0f) }

    // Funzione per generare burst di particelle
    fun triggerParticleBurst() {
        val centerX = 0.5f
        val centerY = 0.4f
        val newParticles = List(18) { i ->
            val angle = (i.toFloat() / 18f) * 2f * Math.PI.toFloat() + Random.nextFloat() * 0.3f
            val speed = 0.08f + Random.nextFloat() * 0.12f
            BurstParticle(
                startX = centerX,
                startY = centerY,
                velocityX = cos(angle) * speed,
                velocityY = sin(angle) * speed - 0.02f,
                radius = 3f + Random.nextFloat() * 5f,
                color = listOf(
                    Color(0xFF60E0F0),
                    Color(0xFF80C8FF),
                    Color(0xFFA0E0FF),
                    Color(0xFF90D0F8),
                    Color(0xFFB0F0FF)
                ).random()
            )
        }
        particles = newParticles
        scope.launch {
            particleProgress.snapTo(0f)
            particleProgress.animateTo(
                1f,
                animationSpec = tween(800, easing = LinearEasing)
            )
            particles = emptyList()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Sfondo acquario con decorazioni
        AquariumBackground(
            placedDecorations = uiState.placedDecorations
        )

        // Layer particelle (sopra sfondo, sotto UI)
        if (particles.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val t = particleProgress.value
                val alpha = (1f - t).coerceIn(0f, 1f)

                particles.forEach { p ->
                    val px = (p.startX + p.velocityX * t) * w
                    val py = (p.startY + p.velocityY * t + 0.05f * t * t) * h
                    val r = p.radius * (1f - t * 0.5f)
                    drawCircle(
                        color = p.color.copy(alpha = alpha * 0.7f),
                        radius = r,
                        center = Offset(px, py)
                    )
                    // Alone intorno alla particella
                    drawCircle(
                        color = p.color.copy(alpha = alpha * 0.2f),
                        radius = r * 2f,
                        center = Offset(px, py)
                    )
                }
            }
        }

        // Contenuto
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // XP Bar
            XpBar(
                level = uiState.level,
                currentXp = uiState.xp,
                xpForCurrentLevel = uiState.xpForCurrentLevel,
                xpForNextLevel = uiState.xpForNextLevel
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Daily Challenge Card
            ChallengeCard(
                challenge = uiState.todayChallenge
            )

            // Medusa + barra progresso verticale affiancata - ORA OCCUPA TUTTO LO SPAZIO!
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Medusa — occupa tutto lo spazio disponibile
                JellyFishView(
                    fillPercentage = uiState.percentage,
                    species = uiState.selectedJellyfishSpecies,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                )

                // Barra progresso verticale sul lato destro
                WaterProgressBar(
                    currentMl = uiState.currentMl,
                    goalMl = uiState.goalMl,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }

            // Streak info compatto in basso (opzionale, molto piccolo)
            if (uiState.streak > 0) {
                Text(
                    text = "🔥 ${uiState.streak} giorni",
                    style = MaterialTheme.typography.labelMedium,
                    color = GoldBadge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        // FAB for Shop (top)
        FloatingActionButton(
            onClick = onNavigateToShop,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 200.dp),
            containerColor = Color(0xFF2196F3)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Shop",
                tint = Color.White
            )
        }

        // FAB for Water (below shop button) - NUOVO!
        FloatingActionButton(
            onClick = { waterMenuExpanded = !waterMenuExpanded },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 130.dp),
            containerColor = Color(0xFF03A9F4)
        ) {
            Text(
                text = "💧",
                fontSize = 28.sp
            )
        }

        // Expanded water glasses menu
        AnimatedVisibility(
            visible = waterMenuExpanded,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 80.dp, bottom = 115.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                uiState.glasses.forEach { amount ->
                    FloatingActionButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            triggerParticleBurst()
                            viewModel.addWater(amount)
                            waterMenuExpanded = false // Chiudi dopo selezione
                        },
                        modifier = Modifier.size(64.dp),
                        containerColor = Color(0xFF4FC3F7)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Icone bicchiere/lattina/bottiglia
                            Text(
                                text = when {
                                    amount >= 1000 -> "🍾" // Bottiglia
                                    amount >= 500 -> "🥤"  // Lattina/bicchiere medio
                                    else -> "🥛"           // Bicchiere piccolo
                                },
                                fontSize = 24.sp
                            )
                            // Formato in litri
                            Text(
                                text = when {
                                    amount % 1000 == 0 -> "${amount / 1000}L"
                                    else -> "%.1fL".format(amount / 1000f)
                                },
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Overlay scuro durante popup
        if (newBadge != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )
        }

        // Popup nuovo badge
        AnimatedVisibility(
            visible = newBadge != null,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            newBadge?.let { badge ->
                Card(
                    modifier = Modifier.padding(32.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Badge",
                            tint = GoldBadge,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nuovo Badge!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = badge.description,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { viewModel.dismissBadge() }) {
                            Text("Fantastico!")
                        }
                    }
                }
            }
        }
    }
}
