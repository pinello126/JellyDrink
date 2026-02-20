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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jellydrink.app.ui.components.AquariumBackground
import com.jellydrink.app.ui.components.BadgeMedalCanvas
import com.jellydrink.app.ui.components.ChallengeCard
import com.jellydrink.app.ui.components.JellyFishView
import com.jellydrink.app.ui.components.WaterGlassSelector
import com.jellydrink.app.ui.components.WaterProgressBar
import com.jellydrink.app.ui.components.XpBar
import com.jellydrink.app.data.repository.WaterRepository
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
                            Canvas(modifier = Modifier.size(32.dp)) {
                                when {
                                    amount >= 1000 -> drawWaterBottleLarge()
                                    amount >= 500 -> drawWaterBottleSmall()
                                    else -> drawWaterGlass()
                                }
                            }
                            Text(
                                text = when {
                                    amount % 1000 == 0 -> "${amount / 1000}L"
                                    amount % 100 == 0 -> "%.1fL".format(amount / 1000f)
                                    else -> "%.2fL".format(amount / 1000f)
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

        // Popup nuovo badge — Dark Premium
        AnimatedVisibility(
            visible = newBadge != null,
            enter = scaleIn(initialScale = 0.85f) + fadeIn(),
            exit = scaleOut(targetScale = 0.85f) + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            newBadge?.let { badge ->
                val definition = WaterRepository.ALL_BADGES.find { it.type == badge.type }
                BadgePopupCard(
                    category = definition?.category ?: "",
                    name = definition?.name ?: badge.description,
                    description = badge.description,
                    onDismiss = { viewModel.dismissBadge() }
                )
            }
        }
    }
}

@Composable
private fun BadgePopupCard(
    category: String,
    name: String,
    description: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 28.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header dorato
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF8B6914),
                                Color(0xFFFFD700),
                                Color(0xFFFFB800),
                                Color(0xFF8B6914)
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✦  BADGE SBLOCCATO  ✦",
                    color = Color(0xFF1A1A2E),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            BadgeMedalCanvas(
                category = category,
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 28.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700),
                    contentColor = Color(0xFF1A1A2E)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Stai volando!",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


// ═══════════════════════════════════════════════════════════════
//  ICONE ACQUA — Canvas-drawn, stile moderno
// ═══════════════════════════════════════════════════════════════

private val WaterBlue = Color(0xFF4FC3F7)
private val WaterBlueDark = Color(0xFF0288D1)
private val WaterBlueLight = Color(0xFFB3E5FC)
private val GlassBody = Color(0xFFE0F7FA)
private val GlassStroke = Color(0xFFFFFFFF)
private val BottleCap = Color(0xFFFFFFFF)
private val BottleBody = Color(0xFFE0F7FA)

// 0.2L — Bicchiere d'acqua (compatto)
private fun DrawScope.drawWaterGlass() {
    val w = size.width
    val h = size.height
    val cx = w / 2f

    val glassTop = h * 0.28f
    val glassBot = h * 0.90f
    val topHalf = w * 0.28f
    val botHalf = w * 0.20f

    val glassPath = Path().apply {
        moveTo(cx - topHalf, glassTop)
        lineTo(cx - botHalf, glassBot)
        lineTo(cx + botHalf, glassBot)
        lineTo(cx + topHalf, glassTop)
        close()
    }

    drawPath(
        glassPath,
        brush = Brush.verticalGradient(
            listOf(GlassBody.copy(alpha = 0.3f), GlassBody.copy(alpha = 0.15f)),
            startY = glassTop, endY = glassBot
        )
    )

    // Acqua (~70%)
    val waterTop = glassTop + (glassBot - glassTop) * 0.30f
    val waterTopHalf = botHalf + (topHalf - botHalf) * (1f - 0.30f)
    val waterPath = Path().apply {
        moveTo(cx - waterTopHalf, waterTop)
        lineTo(cx - botHalf, glassBot)
        lineTo(cx + botHalf, glassBot)
        lineTo(cx + waterTopHalf, waterTop)
        close()
    }
    drawPath(
        waterPath,
        brush = Brush.verticalGradient(
            listOf(WaterBlueLight, WaterBlue, WaterBlueDark),
            startY = waterTop, endY = glassBot
        )
    )

    // Riflesso acqua
    drawLine(
        Color.White.copy(alpha = 0.5f),
        Offset(cx - waterTopHalf * 0.6f, waterTop + 3f),
        Offset(cx + waterTopHalf * 0.3f, waterTop + 2f),
        strokeWidth = 1.5f, cap = StrokeCap.Round
    )

    // Contorno
    drawPath(
        glassPath, GlassStroke.copy(alpha = 0.7f),
        style = Stroke(2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // Riflesso vetro
    drawLine(
        Color.White.copy(alpha = 0.4f),
        Offset(cx - topHalf + 2f, glassTop + 3f),
        Offset(cx - botHalf + 1.5f, glassBot - 3f),
        strokeWidth = 1.5f, cap = StrokeCap.Round
    )
}

// 0.5L — Bottiglietta d'acqua (slanciata, stile vetro)
private fun DrawScope.drawWaterBottleSmall() {
    val w = size.width
    val h = size.height
    val cx = w / 2f

    // Tappo piccolo
    val capTop = h * 0.0f
    val capBot = h * 0.08f
    val capHalf = w * 0.09f
    drawRect(BottleCap, Offset(cx - capHalf, capTop), Size(capHalf * 2f, capBot - capTop))

    // Collo
    val neckTop = capBot
    val neckBot = h * 0.26f
    val neckHalf = w * 0.06f
    drawRect(BottleBody.copy(alpha = 0.4f), Offset(cx - neckHalf, neckTop), Size(neckHalf * 2f, neckBot - neckTop))

    // Corpo stretto e allungato
    val bodyTop = neckBot
    val bodyBot = h * 0.97f
    val bodyHalf = w * 0.18f

    val bodyPath = Path().apply {
        moveTo(cx - neckHalf, neckBot)
        cubicTo(cx - bodyHalf, neckBot, cx - bodyHalf, bodyTop + (bodyBot - bodyTop) * 0.06f,
                cx - bodyHalf, bodyTop + (bodyBot - bodyTop) * 0.12f)
        lineTo(cx - bodyHalf, bodyBot)
        lineTo(cx + bodyHalf, bodyBot)
        lineTo(cx + bodyHalf, bodyTop + (bodyBot - bodyTop) * 0.12f)
        cubicTo(cx + bodyHalf, bodyTop + (bodyBot - bodyTop) * 0.06f,
                cx + bodyHalf, neckBot, cx + neckHalf, neckBot)
        close()
    }

    drawPath(bodyPath, brush = Brush.verticalGradient(
        listOf(BottleBody.copy(alpha = 0.3f), BottleBody.copy(alpha = 0.15f)),
        startY = bodyTop, endY = bodyBot
    ))

    // Acqua (~75%)
    val waterTop = bodyTop + (bodyBot - bodyTop) * 0.25f
    drawRect(
        brush = Brush.verticalGradient(listOf(WaterBlueLight, WaterBlue, WaterBlueDark), startY = waterTop, endY = bodyBot),
        topLeft = Offset(cx - bodyHalf, waterTop),
        size = Size(bodyHalf * 2f, bodyBot - waterTop)
    )

    // Riflesso acqua
    drawLine(Color.White.copy(alpha = 0.45f), Offset(cx - bodyHalf * 0.6f, waterTop + 2f), Offset(cx + bodyHalf * 0.3f, waterTop + 1.5f), strokeWidth = 1.2f, cap = StrokeCap.Round)

    // Contorno
    drawPath(bodyPath, GlassStroke.copy(alpha = 0.6f), style = Stroke(1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))

    // Riflesso vetro
    drawLine(Color.White.copy(alpha = 0.35f), Offset(cx - bodyHalf + 2f, bodyTop + 6f), Offset(cx - bodyHalf + 1.5f, bodyBot - 4f), strokeWidth = 1.2f, cap = StrokeCap.Round)

    // Etichetta sottile
    val labelTop = bodyTop + (bodyBot - bodyTop) * 0.45f
    val labelH = (bodyBot - bodyTop) * 0.12f
    drawRect(Color.White.copy(alpha = 0.22f), Offset(cx - bodyHalf + 2f, labelTop), Size(bodyHalf * 2f - 4f, labelH))
}

// 1L — Bottiglia d'acqua grande (slanciata, stile vetro)
private fun DrawScope.drawWaterBottleLarge() {
    val w = size.width
    val h = size.height
    val cx = w / 2f

    // Tappo
    val capTop = h * 0.0f
    val capBot = h * 0.07f
    val capHalf = w * 0.10f
    drawRect(BottleCap, Offset(cx - capHalf, capTop), Size(capHalf * 2f, capBot - capTop))

    // Collo
    val neckTop = capBot
    val neckBot = h * 0.22f
    val neckHalf = w * 0.06f
    drawRect(BottleBody.copy(alpha = 0.4f), Offset(cx - neckHalf, neckTop), Size(neckHalf * 2f, neckBot - neckTop))

    // Corpo stretto e allungato
    val bodyTop = neckBot
    val bodyBot = h * 0.98f
    val bodyHalf = w * 0.22f

    val bodyPath = Path().apply {
        moveTo(cx - neckHalf, neckBot)
        cubicTo(cx - bodyHalf, neckBot, cx - bodyHalf, bodyTop + (bodyBot - bodyTop) * 0.05f,
                cx - bodyHalf, bodyTop + (bodyBot - bodyTop) * 0.10f)
        lineTo(cx - bodyHalf, bodyBot)
        lineTo(cx + bodyHalf, bodyBot)
        lineTo(cx + bodyHalf, bodyTop + (bodyBot - bodyTop) * 0.10f)
        cubicTo(cx + bodyHalf, bodyTop + (bodyBot - bodyTop) * 0.05f,
                cx + bodyHalf, neckBot, cx + neckHalf, neckBot)
        close()
    }

    drawPath(bodyPath, brush = Brush.verticalGradient(
        listOf(BottleBody.copy(alpha = 0.3f), BottleBody.copy(alpha = 0.15f)),
        startY = bodyTop, endY = bodyBot
    ))

    // Acqua (~80%)
    val waterTop = bodyTop + (bodyBot - bodyTop) * 0.20f
    drawRect(
        brush = Brush.verticalGradient(listOf(WaterBlueLight, WaterBlue, WaterBlueDark), startY = waterTop, endY = bodyBot),
        topLeft = Offset(cx - bodyHalf, waterTop),
        size = Size(bodyHalf * 2f, bodyBot - waterTop)
    )

    // Riflesso acqua
    drawLine(Color.White.copy(alpha = 0.45f), Offset(cx - bodyHalf * 0.6f, waterTop + 2f), Offset(cx + bodyHalf * 0.3f, waterTop + 1.5f), strokeWidth = 1.5f, cap = StrokeCap.Round)

    // Contorno
    drawPath(bodyPath, GlassStroke.copy(alpha = 0.6f), style = Stroke(1.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))

    // Riflesso vetro
    drawLine(Color.White.copy(alpha = 0.35f), Offset(cx - bodyHalf + 2f, bodyTop + 8f), Offset(cx - bodyHalf + 1.5f, bodyBot - 4f), strokeWidth = 1.5f, cap = StrokeCap.Round)

    // Etichetta
    val labelTop = bodyTop + (bodyBot - bodyTop) * 0.40f
    val labelH = (bodyBot - bodyTop) * 0.15f
    drawRect(Color.White.copy(alpha = 0.22f), Offset(cx - bodyHalf + 2f, labelTop), Size(bodyHalf * 2f - 4f, labelH))

    // Goccia decorativa sull'etichetta
    val dropCy = labelTop + labelH / 2f
    val dropR = labelH * 0.22f
    drawCircle(WaterBlue.copy(alpha = 0.4f), dropR, Offset(cx, dropCy))
}
