package com.jellydrink.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
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
import com.jellydrink.app.ui.components.PufferfishView
import com.jellydrink.app.viewmodel.BeerUiState
import com.jellydrink.app.viewmodel.BeerViewModel
import kotlinx.coroutines.delay

// Valori in cl per ogni tipo di bevanda
private const val CL_PINTA = 56
private const val CL_33 = 33
private const val CL_66 = 66

// Colori tema birra
private val BeerGold   = Color(0xFFF5A623)
private val BeerDark   = Color(0xFF8B5E0A)
private val BeerFoam   = Color(0xFFFFF8DC)
private val LockedGray = Color(0xFF90A4AE)

@Composable
fun BeerScreen(
    viewModel: BeerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unlockResult by viewModel.unlockResult.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    var beerMenuExpanded by remember { mutableStateOf(false) }

    // Auto-dismiss unlock result
    LaunchedEffect(unlockResult) {
        if (unlockResult != null) {
            delay(2000)
            viewModel.dismissUnlockResult()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Stesso sfondo acquario con stesse decorazioni
        AquariumBackground(placedDecorations = uiState.placedDecorations)

        if (!uiState.isPufferfishUnlocked) {
            // ── Schermata di unlock ──────────────────────────────────────────
            LockedPufferfishScreen(uiState = uiState, onUnlock = { viewModel.unlockPufferfish() })
        } else {
            // ── Schermata principale birra ───────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: totale giornaliero
                BeerHeader(uiState)

                // Pesce palla — occupa tutto lo spazio centrale
                PufferfishView(
                    fillPercentage = uiState.fillPercentage,
                    isDrunk = uiState.isDrunk,
                    isVeryDrunk = uiState.isVeryDrunk,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                // Messaggio ironico quando ubriaco
                if (uiState.isDrunk) {
                    DrunkWarning(isVeryDrunk = uiState.isVeryDrunk)
                }
            }

            // FAB registra birra
            FloatingActionButton(
                onClick = { beerMenuExpanded = !beerMenuExpanded },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 130.dp),
                containerColor = BeerGold
            ) {
                Text("🍺", fontSize = 28.sp)
            }

            // Menu selezione bicchiere
            AnimatedVisibility(
                visible = beerMenuExpanded,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 80.dp, bottom = 115.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.70f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    BeerFABButton(
                        labelTop = "Pinta",
                        labelBot = "${CL_PINTA}cl",
                        drawIcon = { drawPintaGlass() },
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.addBeer(CL_PINTA)
                            beerMenuExpanded = false
                        }
                    )
                    BeerFABButton(
                        labelTop = "Birra",
                        labelBot = "${CL_33}cl",
                        drawIcon = { drawBeerCan() },
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.addBeer(CL_33)
                            beerMenuExpanded = false
                        }
                    )
                    BeerFABButton(
                        labelTop = "Birra",
                        labelBot = "${CL_66}cl",
                        drawIcon = { drawBeerBottle() },
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.addBeer(CL_66)
                            beerMenuExpanded = false
                        }
                    )
                }
            }

            // Feedback unlock
            AnimatedVisibility(
                visible = unlockResult == BeerViewModel.UnlockResult.Success,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Card(
                    modifier = Modifier.padding(32.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(
                        text = "🐡 Pesce Palla sbloccato!",
                        modifier = Modifier.padding(24.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BeerHeader(uiState: BeerUiState) {
    val statusText = when {
        uiState.isVeryDrunk -> "🌀 Decisamente troppo..."
        uiState.isDrunk     -> "😵 Forse basta così"
        uiState.todayTotalCl == 0 -> "Nessuna birra oggi"
        else                -> "Oggi: ${uiState.todayTotalCl}cl"
    }
    val statusColor = when {
        uiState.isVeryDrunk -> Color(0xFFE85A1A)
        uiState.isDrunk     -> Color(0xFFE8841A)
        else                -> BeerGold
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = statusText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                textAlign = TextAlign.Center
            )
            if (uiState.todayTotalCl > 0 && !uiState.isVeryDrunk && !uiState.isDrunk) {
                Text(
                    text = "Birra tracciata oggi",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun DrunkWarning(isVeryDrunk: Boolean) {
    val message = if (isVeryDrunk)
        "Il pesce palla non approva."
    else
        "Il pesce palla ti guarda storto."

    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFFE8841A).copy(alpha = 0.85f),
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 8.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun LockedPufferfishScreen(
    uiState: BeerUiState,
    onUnlock: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Preview pesce palla (sbiadito)
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(100.dp)),
            contentAlignment = Alignment.Center
        ) {
            PufferfishView(
                fillPercentage = 0.4f,
                isDrunk = false,
                isVeryDrunk = false,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .then(Modifier) // alpha resa via wrapper
            )
            // Overlay lock
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(100.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🔒", fontSize = 52.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Pesce Palla",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Traccia il consumo di birra con il tuo pesce palla.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.75f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Requisiti
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.40f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RequirementRow(
                    met = uiState.meetsLevelRequirement,
                    text = "Livello ${BeerUiState.REQUIRED_LEVEL} (sei al ${uiState.level})"
                )
                Spacer(modifier = Modifier.height(8.dp))
                RequirementRow(
                    met = uiState.spendableXp >= BeerUiState.PUFFERFISH_COST,
                    text = "${BeerUiState.PUFFERFISH_COST} XP (hai ${uiState.spendableXp})"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onUnlock,
            enabled = uiState.canUnlock,
            colors = ButtonDefaults.buttonColors(
                containerColor = BeerGold,
                contentColor = Color.Black,
                disabledContainerColor = LockedGray.copy(alpha = 0.5f),
                disabledContentColor = Color.White.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (uiState.canUnlock) "Sblocca — ${BeerUiState.PUFFERFISH_COST} XP"
                       else "Requisiti non soddisfatti",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun RequirementRow(met: Boolean, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(if (met) "✅" else "❌", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (met) Color(0xFF81C784) else Color.White.copy(alpha = 0.65f)
        )
    }
}

@Composable
private fun BeerFABButton(
    labelTop: String,
    labelBot: String,
    drawIcon: DrawScope.() -> Unit,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.size(68.dp),
        containerColor = Color(0xFFE8A020)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Canvas(modifier = Modifier.size(32.dp)) { drawIcon() }
            Text(labelTop, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text(labelBot, fontSize = 9.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

// ═══ ICONE BIRRA (Canvas) ═════════════════════════════════════════════════════

private val BeerBodyColor  = Color(0xFFF5C842)
private val BeerBodyDark   = Color(0xFFC89010)
private val BeerGoldStroke = Color(0xFF8B5E0A)
private val FoamWhite      = Color(0xFFFFFAF0)
private val FoamStroke     = Color(0xFFE8E0C0)

// Pinta (bicchiere alto con schiuma)
private fun DrawScope.drawPintaGlass() {
    val w = size.width; val h = size.height; val cx = w / 2f
    val topW = w * 0.38f; val botW = w * 0.26f
    val glassTop = h * 0.08f; val glassBot = h * 0.95f
    // Corpo
    val bodyPath = Path().apply {
        moveTo(cx - topW, glassTop)
        lineTo(cx + topW, glassTop)
        lineTo(cx + botW, glassBot)
        lineTo(cx - botW, glassBot)
        close()
    }
    drawPath(bodyPath, brush = Brush.verticalGradient(listOf(BeerBodyColor, BeerBodyDark), startY = glassTop, endY = glassBot))
    // Birra (senza schiuma)
    val beerTop = glassTop + (glassBot - glassTop) * 0.22f
    val beerTopW = botW + (topW - botW) * (1f - 0.22f)
    val beerPath = Path().apply {
        moveTo(cx - beerTopW, beerTop)
        lineTo(cx + beerTopW, beerTop)
        lineTo(cx + botW, glassBot)
        lineTo(cx - botW, glassBot)
        close()
    }
    drawPath(beerPath, brush = Brush.verticalGradient(listOf(BeerBodyColor, BeerBodyDark), startY = beerTop, endY = glassBot))
    // Schiuma
    drawOval(FoamWhite, topLeft = Offset(cx - topW, glassTop - h * 0.06f), size = Size(topW * 2f, h * 0.15f))
    drawOval(FoamStroke.copy(alpha = 0.5f), topLeft = Offset(cx - topW, glassTop - h * 0.06f), size = Size(topW * 2f, h * 0.15f), style = Stroke(1f))
    // Contorno
    drawPath(bodyPath, BeerGoldStroke.copy(alpha = 0.6f), style = Stroke(2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    // Riflesso
    drawLine(Color.White.copy(alpha = 0.35f), Offset(cx - topW + 3f, glassTop + 4f), Offset(cx - botW + 2f, glassBot - 4f), 1.5f, cap = StrokeCap.Round)
}

// Lattina 33cl
private fun DrawScope.drawBeerCan() {
    val w = size.width; val h = size.height; val cx = w / 2f
    val hw = w * 0.22f; val top = h * 0.08f; val bot = h * 0.92f
    val bodyH = bot - top
    // Corpo cilindrico
    drawRect(brush = Brush.horizontalGradient(listOf(BeerBodyDark, BeerBodyColor, BeerBodyColor, BeerBodyDark)), topLeft = Offset(cx - hw, top), size = Size(hw * 2f, bodyH))
    // Tappo superiore
    drawOval(BeerGoldStroke, topLeft = Offset(cx - hw, top - h * 0.04f), size = Size(hw * 2f, h * 0.09f))
    drawOval(Color(0xFFCCCCCC), topLeft = Offset(cx - hw + 2f, top - h * 0.025f), size = Size(hw * 2f - 4f, h * 0.06f))
    // Fondo
    drawOval(BeerBodyDark, topLeft = Offset(cx - hw, bot - h * 0.04f), size = Size(hw * 2f, h * 0.09f))
    // Strisce decorative
    drawLine(Color.White.copy(alpha = 0.20f), Offset(cx - hw + 3f, top + bodyH * 0.25f), Offset(cx - hw + 3f, top + bodyH * 0.75f), 1.5f)
    drawLine(Color.White.copy(alpha = 0.15f), Offset(cx + hw - 3f, top + bodyH * 0.25f), Offset(cx + hw - 3f, top + bodyH * 0.75f), 1f)
    // Contorno
    drawRect(BeerGoldStroke.copy(alpha = 0.5f), topLeft = Offset(cx - hw, top), size = Size(hw * 2f, bodyH), style = Stroke(1.5f))
}

// Bottiglia 66cl
private fun DrawScope.drawBeerBottle() {
    val w = size.width; val h = size.height; val cx = w / 2f
    // Tappo
    val capHalf = w * 0.10f; val capTop = h * 0.0f; val capBot = h * 0.07f
    drawRect(BeerGoldStroke, Offset(cx - capHalf, capTop), Size(capHalf * 2f, capBot - capTop))
    // Collo
    val neckHalf = w * 0.07f; val neckTop = capBot; val neckBot = h * 0.28f
    drawRect(BeerBodyDark.copy(alpha = 0.6f), Offset(cx - neckHalf, neckTop), Size(neckHalf * 2f, neckBot - neckTop))
    // Spalle
    val bodyHalf = w * 0.24f; val shoulderBot = h * 0.38f
    val shoulderPath = Path().apply {
        moveTo(cx - neckHalf, neckBot)
        cubicTo(cx - bodyHalf, neckBot, cx - bodyHalf, shoulderBot, cx - bodyHalf, shoulderBot)
        lineTo(cx + bodyHalf, shoulderBot)
        cubicTo(cx + bodyHalf, shoulderBot, cx + bodyHalf, neckBot, cx + neckHalf, neckBot)
        close()
    }
    drawPath(shoulderPath, BeerBodyColor.copy(alpha = 0.5f))
    // Corpo
    val bodyTop = shoulderBot; val bodyBot = h * 0.95f
    drawRect(brush = Brush.verticalGradient(listOf(BeerBodyColor, BeerBodyDark), startY = bodyTop, endY = bodyBot), topLeft = Offset(cx - bodyHalf, bodyTop), size = Size(bodyHalf * 2f, bodyBot - bodyTop))
    // Etichetta
    val lblTop = bodyTop + (bodyBot - bodyTop) * 0.30f; val lblH = (bodyBot - bodyTop) * 0.30f
    drawRoundRect(FoamWhite.copy(alpha = 0.55f), Offset(cx - bodyHalf + 3f, lblTop), Size(bodyHalf * 2f - 6f, lblH), CornerRadius(3f))
    drawRoundRect(BeerGoldStroke.copy(alpha = 0.35f), Offset(cx - bodyHalf + 3f, lblTop), Size(bodyHalf * 2f - 6f, lblH), CornerRadius(3f), style = Stroke(1f))
    // Contorno
    drawRect(BeerGoldStroke.copy(alpha = 0.45f), Offset(cx - bodyHalf, bodyTop), Size(bodyHalf * 2f, bodyBot - bodyTop), style = Stroke(1.5f))
    // Riflesso
    drawLine(Color.White.copy(alpha = 0.25f), Offset(cx - bodyHalf + 3f, bodyTop + 4f), Offset(cx - bodyHalf + 3f, bodyBot - 4f), 1.5f, cap = StrokeCap.Round)
}
