package com.jellydrink.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.jellydrink.app.data.db.entity.DecorationEntity
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// === PALETTE OCEANO PROFONDO ===
private val OceanTop = Color(0xFF030D1A)
private val OceanMid1 = Color(0xFF061830)
private val OceanMid2 = Color(0xFF0A2848)
private val OceanMid3 = Color(0xFF0E3858)
private val OceanMid4 = Color(0xFF124868)
private val OceanMid5 = Color(0xFF185878)
private val OceanBot1 = Color(0xFF1A6888)
private val OceanBot2 = Color(0xFF1E7898)

private val SandHighlight = Color(0xFFE8D8B0)
private val SandMain = Color(0xFFD0B888)
private val SandMid = Color(0xFFC0A870)
private val SandDark = Color(0xFFA89058)
private val SandDeep = Color(0xFF907848)

private val SeaweedBright = Color(0xFF38B860)
private val SeaweedMid = Color(0xFF289848)
private val SeaweedDark = Color(0xFF1A7030)
private val SeaweedOlive = Color(0xFF608838)

private val CoralPink = Color(0xFFE08888)
private val CoralOrange = Color(0xFFD8A060)
private val RockDark = Color(0xFF586068)
private val RockLight = Color(0xFF788088)
private val ShellColor = Color(0xFFE8D0B8)

private val BubbleWhite = Color(0xFFFFFFFF)
private val LightRayColor = Color(0xFFFFFFFF)
private val CausticColor = Color(0xFFB0E8FF)

// Decoration colors
private val FishBlue = Color(0xFF2196F3)
private val FishBlueDark = Color(0xFF1565C0)
private val FishOrange = Color(0xFFFF5722)
private val FishOrangeDark = Color(0xFFE64A19)
private val FishWhite = Color(0xFFFFFFFF)
private val StarfishOrange = Color(0xFFFF9800)
private val StarfishDark = Color(0xFFE65100)
private val TreasureGold = Color(0xFFFFD700)
private val TreasureBrown = Color(0xFF8D6E63)
private val TurtleGreen = Color(0xFF4CAF50)
private val TurtleDark = Color(0xFF2E7D32)
private val SeahorseYellow = Color(0xFFFFEB3B)
private val SeahorseDark = Color(0xFFFBC02D)
private val CrabRed = Color(0xFFF44336)
private val CrabDark = Color(0xFFC62828)

data class Bubble(val x: Float, val baseY: Float, val radius: Float, val speed: Float, val phase: Float)
data class Seaweed(val x: Float, val height: Float, val width: Float, val phase: Float, val colorType: Int)
data class Particle(val x: Float, val y: Float, val size: Float, val speedX: Float, val speedY: Float, val phase: Float)

@Composable
fun AquariumBackground(
    modifier: Modifier = Modifier,
    placedDecorations: List<DecorationEntity> = emptyList()
) {
    val inf = rememberInfiniteTransition(label = "aquarium")

    // Ultra-smooth continuous animations - multiple independent phases
    // All use 0 to 2*PI cycles which are naturally continuous with sin/cos

    // Main animation phases - different speeds create organic complexity
    val phase1 by inf.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart),
        label = "phase1"
    )

    val phase2 by inf.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing), RepeatMode.Restart),
        label = "phase2"
    )

    val phase3 by inf.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart),
        label = "phase3"
    )

    val phase4 by inf.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(25000, easing = LinearEasing), RepeatMode.Restart),
        label = "phase4"
    )

    val phaseFast by inf.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "phaseFast"
    )

    val phaseSlow by inf.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(30000, easing = LinearEasing), RepeatMode.Restart),
        label = "phaseSlow"
    )

    // FASI ULTRA-LENTE PER PESCI (60-120 secondi per ciclo completo)
    val fishPhase1 by inf.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(67000, easing = LinearEasing), RepeatMode.Restart),
        label = "fishPhase1"
    )

    val fishPhase2 by inf.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(83000, easing = LinearEasing), RepeatMode.Restart),
        label = "fishPhase2"
    )

    val fishPhase3 by inf.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(97000, easing = LinearEasing), RepeatMode.Restart),
        label = "fishPhase3"
    )

    val fishPhase4 by inf.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(113000, easing = LinearEasing), RepeatMode.Restart),
        label = "fishPhase4"
    )

    val fishPhase5 by inf.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(127000, easing = LinearEasing), RepeatMode.Restart),
        label = "fishPhase5"
    )

    // Derived smooth values using pure trigonometry (no multipliers on phase!)
    val time = phase1
    val slowTime = phaseSlow
    val bubbleTime = (sin(phase2) * 0.5f + 0.5f) // 0 to 1, smooth
    val lightSway = sin(phase3) * 0.04f // -0.04 to 0.04, smooth oscillation
    val causticPhase = phaseFast

    // Bolle variabili — da micro a medie
    val bubbles = remember {
        List(22) { i ->
            Bubble(
                x = 0.04f + (i * 0.043f + i * i * 0.003f) % 0.92f,
                baseY = 0.2f + (i * 0.14f) % 0.7f,
                radius = when {
                    i % 5 == 0 -> 6f + (i % 3) * 2f     // medie
                    i % 3 == 0 -> 3.5f + (i % 4) * 1f    // piccole
                    else -> 1.5f + (i % 3) * 0.8f          // micro
                },
                speed = 0.35f + (i % 5) * 0.15f,
                phase = (i * 0.45f) % (2f * PI.toFloat())
            )
        }
    }

    // Alghe con variazioni di colore
    val seaweeds = remember {
        listOf(
            Seaweed(0.06f, 0.22f, 0.035f, 0f, 0),
            Seaweed(0.12f, 0.17f, 0.028f, 1.1f, 1),
            Seaweed(0.19f, 0.13f, 0.022f, 0.5f, 2),
            Seaweed(0.26f, 0.09f, 0.018f, 1.8f, 0),
            Seaweed(0.48f, 0.08f, 0.02f, 1.3f, 2),
            Seaweed(0.55f, 0.11f, 0.025f, 0.3f, 1),
            Seaweed(0.72f, 0.20f, 0.038f, 1.6f, 0),
            Seaweed(0.78f, 0.15f, 0.03f, 2.1f, 1),
            Seaweed(0.85f, 0.18f, 0.032f, 0.9f, 2),
            Seaweed(0.92f, 0.24f, 0.04f, 0.4f, 0),
        )
    }

    // Particelle fluttuanti (plankton/detriti)
    val particles = remember {
        List(25) { i ->
            Particle(
                x = (i * 0.041f + 0.02f) % 1f,
                y = (i * 0.037f + 0.05f) % 0.85f,
                size = 1f + (i % 4) * 0.5f,
                speedX = 0.002f * if (i % 2 == 0) 1f else -1f,
                speedY = -0.001f - (i % 3) * 0.0005f,
                phase = i * 0.8f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // === GRADIENTE OCEANO PROFONDO (7+ color stops) ===
        drawRect(
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0.00f to OceanTop,
                    0.08f to OceanMid1,
                    0.20f to OceanMid2,
                    0.35f to OceanMid3,
                    0.50f to OceanMid4,
                    0.65f to OceanMid5,
                    0.80f to OceanBot1,
                    1.00f to OceanBot2,
                )
            )
        )

        // === NEBBIA SOTTOMARINA — overlay orizzontali a diverse profondità ===
        val fogLayers = listOf(0.25f, 0.45f, 0.65f, 0.78f)
        for (fogY in fogLayers) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0x06809898),
                        Color.Transparent
                    ),
                    startY = h * (fogY - 0.06f),
                    endY = h * (fogY + 0.06f)
                )
            )
        }

        // === RAGGI DI LUCE — god rays larghi e sfumati ===
        for (i in 0..5) {
            val baseX = w * (0.08f + i * 0.17f + lightSway * (i + 1) * 1.5f)
            val topWidth = w * (0.03f + (i % 3) * 0.015f)
            val botWidth = w * (0.10f + (i % 3) * 0.04f)
            val rayLength = h * (0.60f + (i % 2) * 0.15f)
            val alpha = 0.020f + (i % 2) * 0.012f + sin(slowTime + i * 1.2f) * 0.005f

            val rayPath = Path().apply {
                moveTo(baseX - topWidth, 0f)
                lineTo(baseX + topWidth, 0f)
                lineTo(baseX + botWidth + sin(slowTime * 0.5f + i) * 12f, rayLength)
                lineTo(baseX - botWidth + sin(slowTime * 0.5f + i) * 12f, rayLength)
                close()
            }
            drawPath(rayPath, LightRayColor.copy(alpha = alpha))

            // Bordo sfumato più morbido (ray glow)
            val glowPath = Path().apply {
                moveTo(baseX - topWidth * 2f, 0f)
                lineTo(baseX + topWidth * 2f, 0f)
                lineTo(baseX + botWidth * 1.5f + sin(slowTime * 0.5f + i) * 12f, rayLength * 0.8f)
                lineTo(baseX - botWidth * 1.5f + sin(slowTime * 0.5f + i) * 12f, rayLength * 0.8f)
                close()
            }
            drawPath(glowPath, LightRayColor.copy(alpha = alpha * 0.3f))
        }

        // === SABBIA / FONDALE MULTI-LIVELLO ===
        val sandTop = h * 0.87f
        val sandPath = Path().apply {
            moveTo(0f, sandTop)
            val dunes = 10
            val duneW = w / dunes
            for (i in 0 until dunes) {
                val x1 = i * duneW + duneW * 0.5f
                val x2 = (i + 1) * duneW
                val yVar = sin(slowTime * 0.15f + i * 0.6f) * 3f
                quadraticTo(x1, sandTop - 6f - (i % 3) * 3f + yVar, x2, sandTop + (i % 2) * 2f)
            }
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        // Strato sabbia profonda
        drawPath(
            sandPath,
            brush = Brush.verticalGradient(
                colors = listOf(SandHighlight, SandMain, SandMid, SandDark, SandDeep),
                startY = sandTop - 8f,
                endY = h
            )
        )

        // Granelli sulla sabbia
        for (i in 0..40) {
            val gx = (i * 29f + 17f) % w
            val gy = sandTop + 6f + (i * 13f) % (h - sandTop - 8f)
            drawCircle(
                color = SandHighlight.copy(alpha = 0.15f + (i % 4) * 0.06f),
                radius = 1f + (i % 3) * 0.7f,
                center = Offset(gx, gy)
            )
        }

        // === CONCHIGLIE ===
        drawShell(w * 0.18f, sandTop + 8f, 7f, ShellColor)
        drawShell(w * 0.55f, sandTop + 12f, 5f, ShellColor.copy(alpha = 0.8f))
        drawShell(w * 0.82f, sandTop + 6f, 6f, Color(0xFFD8C0A8))

        // === ROCCE / CORALLI ===
        drawRock(w * 0.35f, sandTop - 2f, 18f, 14f, RockDark, RockLight)
        drawRock(w * 0.62f, sandTop - 4f, 24f, 18f, RockDark, RockLight)
        drawRock(w * 0.10f, sandTop - 1f, 14f, 10f, Color(0xFF505860), Color(0xFF687078))

        // Corallo piccolo
        drawCoral(w * 0.40f, sandTop - 8f, 12f, CoralPink, time)
        drawCoral(w * 0.70f, sandTop - 6f, 10f, CoralOrange, time + 1f)

        // === PIANTE MARINE CORTE SUL FONDALE - SMOOTH ===
        for (i in 0..6) {
            val px = w * (0.05f + i * 0.14f)
            val pH = h * (0.03f + (i % 3) * 0.012f)
            // No multipliers on time - perfectly smooth!
            val sway = sin(time + i * 0.9f) * 4f
            drawLine(
                color = SeaweedOlive.copy(alpha = 0.5f),
                start = Offset(px, sandTop + 2f),
                end = Offset(px + sway, sandTop - pH),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = SeaweedBright.copy(alpha = 0.3f),
                start = Offset(px + 3f, sandTop + 2f),
                end = Offset(px + 3f + sway * 0.7f, sandTop - pH * 0.7f),
                strokeWidth = 1.8f,
                cap = StrokeCap.Round
            )
        }

        // === ALGHE ALTE E REALISTICHE ===
        seaweeds.forEach { sw ->
            val color = when (sw.colorType) {
                0 -> SeaweedBright
                1 -> SeaweedDark
                else -> SeaweedOlive
            }
            val colorAlt = when (sw.colorType) {
                0 -> SeaweedMid
                1 -> SeaweedMid
                else -> SeaweedDark
            }
            drawRealisticSeaweed(
                w * sw.x, sandTop, w * sw.width, h * sw.height,
                time, sw.phase, color, colorAlt
            )
        }

        // === CAUSTICS — pattern luce sul fondo ===
        for (i in 0..9) {
            val cx = w * (0.05f + i * 0.1f) + sin(causticPhase * 0.7f + i * 0.9f) * 18f
            val cy = sandTop + 3f
            val cAlpha = 0.04f + sin(causticPhase + i * 0.6f).coerceIn(0f, 1f) * 0.04f
            val cWidth = w * 0.06f + sin(causticPhase * 0.5f + i) * w * 0.015f
            drawOval(
                color = CausticColor.copy(alpha = cAlpha),
                topLeft = Offset(cx, cy),
                size = Size(cWidth, 5f)
            )
            // Secondo livello caustic sovrapposto
            drawOval(
                color = Color.White.copy(alpha = cAlpha * 0.5f),
                topLeft = Offset(cx + 8f, cy + 2f),
                size = Size(cWidth * 0.6f, 3f)
            )
        }

        // === PARTICELLE FLUTTUANTI (plankton/detriti) ===
        particles.forEach { p ->
            val px = ((p.x + sin(slowTime * 0.4f + p.phase) * 0.02f + slowTime * p.speedX) % 1f + 1f) % 1f
            val py = ((p.y + cos(slowTime * 0.3f + p.phase) * 0.015f + slowTime * p.speedY) % 0.85f + 0.85f) % 0.85f + 0.03f
            val pAlpha = 0.15f + sin(time + p.phase) * 0.08f
            drawCircle(
                color = Color.White.copy(alpha = pAlpha),
                radius = p.size,
                center = Offset(w * px, h * py)
            )
        }

        // === DECORAZIONI ACQUISTATE E PIAZZATE ===
        placedDecorations.forEachIndexed { index, deco ->
            // Base positions - distributed across aquarium
            val baseX = 0.15f + (index * 0.18f) % 0.65f
            val baseY = 0.45f + (index % 3) * 0.12f

            // SMOOTH SWIMMING MOTION (like jellyfish) - NO multipliers on time!
            val phaseOffset = index * 3.7f

            when (deco.id) {
                // FISH - Movimento ultra-fluido con 5 fasi lente (67-127 secondi)
                "fish_blue", "fish_orange", "turtle" -> {
                    // MOVIMENTO SU TUTTO LO SCHERMO - Cicli lunghissimi
                    // Usa le 5 fishPhases che vanno da 0 a 2π in 67-127 secondi

                    // ORIZZONTALE - combina tutte e 5 le fasi per massima complessità
                    val driftX1 = sin(fishPhase1 + phaseOffset) * 0.30f
                    val driftX2 = sin(fishPhase2 + phaseOffset * 1.618f) * 0.18f
                    val driftX3 = cos(fishPhase3 + phaseOffset * 2.414f) * 0.12f
                    val driftX4 = sin(fishPhase4 + phaseOffset * 1.732f) * 0.08f
                    val driftX5 = cos(fishPhase5 + phaseOffset * 3.141f) * 0.05f

                    // VERTICALE - fasi sfalsate per movimento indipendente
                    val driftY1 = cos(fishPhase2 + phaseOffset * 1.414f) * 0.20f
                    val driftY2 = cos(fishPhase3 + phaseOffset * 2.236f) * 0.12f
                    val driftY3 = sin(fishPhase1 + phaseOffset * 1.902f) * 0.10f
                    val driftY4 = cos(fishPhase4 + phaseOffset * 2.645f) * 0.06f
                    val driftY5 = sin(fishPhase5 + phaseOffset * 3.873f) * 0.04f

                    // Centro unico per ogni pesce (NO modulo!)
                    val fishCenterX = 0.20f + sin(phaseOffset) * 0.25f
                    val fishCenterY = 0.45f + cos(phaseOffset * 1.3f) * 0.15f

                    // POSIZIONE FINALE - somma TUTTO
                    val totalDriftX = driftX1 + driftX2 + driftX3 + driftX4 + driftX5
                    val totalDriftY = driftY1 + driftY2 + driftY3 + driftY4 + driftY5

                    // Movimento libero in TUTTO lo schermo (margini minimi)
                    val fishX = (w * (fishCenterX + totalDriftX)).coerceIn(w * 0.05f, w * 0.95f)
                    val fishY = (h * (fishCenterY + totalDriftY)).coerceIn(h * 0.25f, h * 0.75f)

                    // Direzione basata su velocità reale
                    val velocityX = cos(fishPhase1 + phaseOffset)
                    val direction = if (velocityX > 0) 1f else -1f

                    when (deco.id) {
                        "fish_blue" -> drawRealisticBlueFish(fishX, fishY, 40f, time, index, direction)
                        "fish_orange" -> drawRealisticClownfish(fishX, fishY, 40f, time, index, direction)
                        "turtle" -> drawRealisticTurtle(fishX, fishY, 45f, time, index, direction)
                    }
                }

                // SEAHORSE - Gentle swaying motion
                "seahorse" -> {
                    val swayX = sin(time + phaseOffset) * 0.08f
                    val swayY = cos(time * 0.6f + phaseOffset) * 0.1f
                    val seahorseX = w * (baseX + swayX)
                    val seahorseY = h * (baseY + swayY)
                    drawRealisticSeahorse(seahorseX, seahorseY, 42f, time, index)
                }

                // BOTTOM DECORATIONS - Static or minimal movement
                "starfish" -> {
                    val starX = w * baseX
                    val starY = sandTop + 8f + sin(time * 0.3f + index) * 2f
                    drawStarfish(starX, starY, 18f, StarfishOrange, StarfishDark)
                }
                "coral_pink" -> {
                    drawCoral(w * baseX, sandTop - 10f, 18f, CoralPink, time)
                }
                "treasure" -> {
                    drawTreasureChest(w * baseX, sandTop - 8f, 30f, time)
                }
                "crab" -> {
                    val crabX = w * baseX + sin(time * 0.5f + index) * 15f
                    drawRealisticCrab(crabX, sandTop + 5f, 22f, time)
                }
            }
        }

        // === BOLLE — variabili con distorsione luce ===
        bubbles.forEach { b ->
            val rawY = b.baseY - bubbleTime * b.speed
            val y = ((rawY % 0.9f) + 0.9f) % 0.9f + 0.05f
            val bx = w * b.x + sin(time + b.phase) * 5f
            val by = h * y
            val alpha = (0.18f - (1f - y) * 0.12f).coerceIn(0.04f, 0.25f)

            // Corpo bolla semi-trasparente
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        BubbleWhite.copy(alpha = alpha * 0.3f),
                        BubbleWhite.copy(alpha = alpha * 0.6f),
                        BubbleWhite.copy(alpha = alpha * 0.2f)
                    ),
                    center = Offset(bx, by),
                    radius = b.radius
                ),
                radius = b.radius,
                center = Offset(bx, by)
            )

            // Contorno sottile
            drawCircle(
                BubbleWhite.copy(alpha = alpha * 0.5f),
                b.radius,
                Offset(bx, by),
                style = Stroke(0.8f)
            )

            // Riflesso interno (luce distorta)
            if (b.radius > 2.5f) {
                drawCircle(
                    BubbleWhite.copy(alpha = alpha * 1.5f),
                    b.radius * 0.25f,
                    Offset(bx - b.radius * 0.3f, by - b.radius * 0.3f)
                )
                // Secondo riflesso piccolo
                drawCircle(
                    BubbleWhite.copy(alpha = alpha * 0.7f),
                    b.radius * 0.12f,
                    Offset(bx + b.radius * 0.2f, by + b.radius * 0.15f)
                )
            }
        }
    }
}

// --- Alga realistica con variazione colore - ULTRA SMOOTH ---
private fun DrawScope.drawRealisticSeaweed(
    baseX: Float, baseY: Float, width: Float, height: Float,
    time: Float, phaseOffset: Float, mainColor: Color, altColor: Color
) {
    val segments = 10
    val segH = height / segments

    // Stelo principale - NO multipliers on time for perfect continuity!
    val path = Path()
    path.moveTo(baseX, baseY)
    for (s in 1..segments) {
        val t = s.toFloat() / segments
        // Use time directly + offsets, no multipliers = perfectly smooth
        val sway = sin(time + phaseOffset + t * PI.toFloat()) * width * 2.5f * t
        val x = baseX + sway
        val y = baseY - s * segH
        path.quadraticTo(
            baseX + sway * 0.5f, baseY - (s - 0.5f) * segH,
            x, y
        )
    }
    // Ombra
    drawPath(path, altColor.copy(alpha = 0.5f), style = Stroke(width * 1.3f + 4f, cap = StrokeCap.Round))
    // Stelo
    drawPath(path, mainColor.copy(alpha = 0.7f), style = Stroke(width * 1.3f + 1f, cap = StrokeCap.Round))

    // Foglie con variazione
    for (s in listOf(2, 4, 6, 8)) {
        if (s > segments) break
        val t = s.toFloat() / segments
        val sway = sin(time + phaseOffset + t * PI.toFloat()) * width * 2.5f * t
        val lx = baseX + sway
        val ly = baseY - s * segH
        val leafDir = if (s % 4 < 2) 1f else -1f
        val leafLen = width * (2.5f + (s % 3) * 0.8f)
        val leafPath = Path().apply {
            moveTo(lx, ly)
            quadraticTo(
                lx + leafDir * leafLen * 1.2f,
                ly - segH * 0.3f,
                lx + leafDir * leafLen * 0.6f,
                ly + segH * 0.25f
            )
        }
        val leafColor = if (s % 3 == 0) altColor else mainColor
        drawPath(leafPath, leafColor.copy(alpha = 0.55f), style = Stroke(2.2f, cap = StrokeCap.Round))
    }
}

// --- Conchiglia ---
private fun DrawScope.drawShell(cx: Float, cy: Float, r: Float, color: Color) {
    // Forma ovale con linee di dettaglio
    drawOval(
        color = color.copy(alpha = 0.6f),
        topLeft = Offset(cx - r, cy - r * 0.6f),
        size = Size(r * 2f, r * 1.2f)
    )
    drawOval(
        color = Color.White.copy(alpha = 0.15f),
        topLeft = Offset(cx - r * 0.6f, cy - r * 0.4f),
        size = Size(r * 0.8f, r * 0.5f)
    )
    // Righe della conchiglia
    for (i in 0..2) {
        val lineY = cy - r * 0.3f + i * r * 0.3f
        drawLine(
            color = color.copy(alpha = 0.3f),
            start = Offset(cx - r * 0.7f + i * 2f, lineY),
            end = Offset(cx + r * 0.7f - i * 2f, lineY),
            strokeWidth = 0.8f
        )
    }
    // Contorno
    drawOval(
        color = color.copy(alpha = 0.35f),
        topLeft = Offset(cx - r, cy - r * 0.6f),
        size = Size(r * 2f, r * 1.2f),
        style = Stroke(0.8f)
    )
}

// --- Roccia con ombra ---
private fun DrawScope.drawRock(cx: Float, cy: Float, rw: Float, rh: Float, dark: Color, light: Color) {
    // Ombra
    drawOval(
        color = Color.Black.copy(alpha = 0.12f),
        topLeft = Offset(cx - rw * 0.5f + 2f, cy - rh * 0.3f + 2f),
        size = Size(rw, rh * 0.7f)
    )
    // Corpo roccia
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(light, dark),
            center = Offset(cx - rw * 0.1f, cy - rh * 0.15f),
            radius = rw * 0.7f
        ),
        topLeft = Offset(cx - rw * 0.5f, cy - rh * 0.35f),
        size = Size(rw, rh * 0.7f)
    )
    // Riflesso
    drawOval(
        color = Color.White.copy(alpha = 0.08f),
        topLeft = Offset(cx - rw * 0.2f, cy - rh * 0.3f),
        size = Size(rw * 0.3f, rh * 0.2f)
    )
}

// --- Corallo decorativo - SMOOTH ---
private fun DrawScope.drawCoral(baseX: Float, baseY: Float, height: Float, color: Color, time: Float) {
    val branches = 5
    for (i in 0 until branches) {
        val angle = -PI.toFloat() / 2f + (i - branches / 2) * 0.35f
        // No multipliers on time - perfectly smooth!
        val sway = sin(time + i * 0.8f) * 2f
        val endX = baseX + cos(angle) * height + sway
        val endY = baseY + sin(angle) * height

        drawLine(
            color = color.copy(alpha = 0.6f),
            start = Offset(baseX, baseY),
            end = Offset(endX, endY),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        // Punta arrotondata
        drawCircle(
            color = color.copy(alpha = 0.5f),
            radius = 2.5f,
            center = Offset(endX, endY)
        )

        // Sub-rami
        if (i % 2 == 0) {
            val subAngle = angle + 0.4f * if (i < branches / 2) -1f else 1f
            val subEndX = endX + cos(subAngle) * height * 0.4f
            val subEndY = endY + sin(subAngle) * height * 0.4f
            drawLine(
                color = color.copy(alpha = 0.4f),
                start = Offset(endX, endY),
                end = Offset(subEndX, subEndY),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = color.copy(alpha = 0.35f),
                radius = 1.8f,
                center = Offset(subEndX, subEndY)
            )
        }
    }
}

// --- Fish (realistic swimming animation like Fishdom) ---
private fun DrawScope.drawFish(baseX: Float, baseY: Float, mainColor: Color, darkColor: Color, size: Float, time: Float, index: Int) {
    val w = this.size.width
    val h = this.size.height

    // SAME SMOOTH SYSTEM AS JELLYFISH - no multipliers on time!
    // Pure sine/cosine waves for perfectly continuous motion

    val phaseOffset = index * 3.7f

    // Horizontal drift - combine multiple sine waves (NO multipliers!)
    val driftX1 = sin(time + phaseOffset) * 0.30f
    val driftX2 = sin(time + phaseOffset * 1.4f) * 0.15f
    val driftX3 = cos(time + phaseOffset * 0.8f) * 0.08f
    val driftX4 = cos(time + phaseOffset * 2.1f) * 0.05f

    // Vertical drift - combine multiple cosine waves (NO multipliers!)
    val driftY1 = cos(time + phaseOffset * 1.7f) * 0.25f
    val driftY2 = cos(time + phaseOffset * 0.6f) * 0.12f
    val driftY3 = sin(time + phaseOffset * 1.9f) * 0.06f
    val driftY4 = sin(time + phaseOffset * 2.5f) * 0.04f

    // Combine all components - creates organic, perfectly smooth paths
    val totalXOffset = driftX1 + driftX2 + driftX3 + driftX4
    val totalYOffset = driftY1 + driftY2 + driftY3 + driftY4

    // Final position - ultra-smooth like jellyfish!
    val cx = w * (0.5f + totalXOffset)
    val cy = h * (0.5f + totalYOffset)

    // Direction based on horizontal velocity (derivative of sin is cos)
    val velocityX = cos(time + phaseOffset) * 0.30f
    val facingRight = velocityX > 0
    val direction = if (facingRight) 1f else -1f

    // Swimming speed for fin animation (based on total velocity magnitude)
    val speedMagnitude = abs(velocityX) + abs(
        -sin(time + phaseOffset * 1.7f) * 0.25f
    )

    // Tail wagging - NO multipliers, pure sine waves!
    val tailWag = sin(time + phaseOffset) * (0.25f + speedMagnitude * 2f)

    // Body wave/undulation - gentle and smooth, NO multipliers!
    val bodyWave = sin(time + phaseOffset * 0.7f) * 0.06f

    // Pectoral fin flapping - more pronounced and visible, NO multipliers!
    val finFlap = sin(time + phaseOffset * 1.5f) * (0.3f + speedMagnitude * 1.5f)

    // Shadow below fish
    drawOval(
        color = Color.Black.copy(alpha = 0.08f),
        topLeft = Offset(cx - size * 1.2f, cy + size * 0.6f),
        size = Size(size * 2.4f, size * 0.4f)
    )

    // Body with slight curve/bend
    val bodyPath = Path().apply {
        val bodyBend = bodyWave * size * 0.3f
        // Create curved body shape
        moveTo(cx + direction * size * 0.9f, cy - size * 0.35f)
        cubicTo(
            cx + direction * size * 0.3f, cy - size * 0.5f + bodyBend,
            cx - direction * size * 0.3f, cy - size * 0.45f,
            cx - direction * size * 0.9f, cy - size * 0.3f
        )
        cubicTo(
            cx - direction * size, cy,
            cx - direction * size, cy,
            cx - direction * size * 0.9f, cy + size * 0.3f
        )
        cubicTo(
            cx - direction * size * 0.3f, cy + size * 0.45f,
            cx + direction * size * 0.3f, cy + size * 0.5f + bodyBend,
            cx + direction * size * 0.9f, cy + size * 0.35f
        )
        close()
    }

    // Body gradient with depth
    drawPath(
        bodyPath,
        brush = Brush.radialGradient(
            colors = listOf(
                mainColor.copy(alpha = 0.95f),
                mainColor,
                darkColor.copy(alpha = 0.9f)
            ),
            center = Offset(cx + direction * size * 0.2f, cy - size * 0.15f),
            radius = size * 1.5f
        )
    )

    // Highlight on body (light reflection)
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.3f),
                Color.White.copy(alpha = 0.1f),
                Color.Transparent
            ),
            center = Offset(cx + direction * size * 0.3f, cy - size * 0.2f),
            radius = size * 0.5f
        ),
        topLeft = Offset(cx + direction * size * 0.1f, cy - size * 0.35f),
        size = Size(size * 0.6f, size * 0.4f)
    )

    // Tail fin - animated with swimming motion
    val tailPath = Path().apply {
        val tailBase = cx - direction * size * 0.85f
        val tailSpread = size * 0.65f
        moveTo(tailBase, cy)
        cubicTo(
            tailBase - direction * size * 0.3f, cy - tailSpread + tailWag * size * 0.8f,
            tailBase - direction * size * 0.5f, cy - tailSpread * 0.8f + tailWag * size,
            tailBase - direction * size * 0.6f, cy - tailSpread * 0.6f + tailWag * size * 0.9f
        )
        lineTo(tailBase - direction * size * 0.5f, cy + tailWag * size * 0.3f)
        cubicTo(
            tailBase - direction * size * 0.5f, cy + tailSpread * 0.8f + tailWag * size,
            tailBase - direction * size * 0.3f, cy + tailSpread + tailWag * size * 0.8f,
            tailBase, cy
        )
        close()
    }

    drawPath(
        tailPath,
        brush = Brush.radialGradient(
            colors = listOf(mainColor, darkColor.copy(alpha = 0.85f)),
            center = Offset(cx - direction * size * 0.9f, cy),
            radius = size * 0.8f
        )
    )

    // Tail fin outline
    drawPath(tailPath, darkColor.copy(alpha = 0.4f), style = Stroke(1.2f))

    // Dorsal fin (top fin) - gently waving with water current, NO multipliers!
    val dorsalWave = sin(time + phaseOffset * 0.9f) * 0.12f
    val dorsalPath = Path().apply {
        moveTo(cx + direction * size * 0.1f, cy - size * 0.45f)
        quadraticTo(
            cx + direction * size * 0.0f, cy - size * 0.88f + dorsalWave * size,
            cx - direction * size * 0.2f, cy - size * 0.5f
        )
        lineTo(cx + direction * size * 0.1f, cy - size * 0.45f)
        close()
    }
    drawPath(dorsalPath, mainColor.copy(alpha = 0.75f))
    drawPath(dorsalPath, darkColor.copy(alpha = 0.3f), style = Stroke(1f))

    // Pectoral fins (side fins) - actively flapping, synchronized with speed
    // These fins are CRUCIAL for swimming - make them very visible and animated!
    val pectoralPath1 = Path().apply {
        moveTo(cx + direction * size * 0.25f, cy + size * 0.1f)
        cubicTo(
            cx + direction * size * 0.15f + finFlap * size * 1.2f, cy + size * 0.35f,
            cx + direction * size * 0.20f + finFlap * size * 0.8f, cy + size * 0.55f,
            cx + direction * size * 0.35f, cy + size * 0.3f
        )
        close()
    }
    drawPath(pectoralPath1, mainColor.copy(alpha = 0.7f))
    drawPath(pectoralPath1, darkColor.copy(alpha = 0.2f), style = Stroke(1f))

    // Second pectoral fin (slightly behind, creates depth)
    val pectoralPath2 = Path().apply {
        moveTo(cx + direction * size * 0.2f, cy - size * 0.05f)
        cubicTo(
            cx + direction * size * 0.1f - finFlap * size * 0.9f, cy + size * 0.2f,
            cx + direction * size * 0.15f - finFlap * size * 0.6f, cy + size * 0.4f,
            cx + direction * size * 0.3f, cy + size * 0.15f
        )
        close()
    }
    drawPath(pectoralPath2, mainColor.copy(alpha = 0.5f))

    // Eye - positioned based on direction
    val eyeX = cx + direction * size * 0.6f
    val eyeY = cy - size * 0.12f

    // Eye white
    drawCircle(
        Color.White.copy(alpha = 0.95f),
        size * 0.22f,
        Offset(eyeX, eyeY)
    )

    // Iris (blue for blue fish)
    drawCircle(
        Color(0xFF1565C0).copy(alpha = 0.8f),
        size * 0.14f,
        Offset(eyeX + direction * size * 0.02f, eyeY)
    )

    // Pupil
    drawCircle(
        Color.Black,
        size * 0.08f,
        Offset(eyeX + direction * size * 0.04f, eyeY)
    )

    // Eye shine
    drawCircle(
        Color.White.copy(alpha = 0.9f),
        size * 0.05f,
        Offset(eyeX + direction * size * 0.05f, eyeY - size * 0.04f)
    )

    // Mouth - small smile
    val mouthPath = Path().apply {
        moveTo(cx + direction * size * 0.75f, cy + size * 0.08f)
        quadraticTo(
            cx + direction * size * 0.85f, cy + size * 0.15f,
            cx + direction * size * 0.90f, cy + size * 0.08f
        )
    }
    drawPath(mouthPath, darkColor.copy(alpha = 0.6f), style = Stroke(1.5f, cap = StrokeCap.Round))

    // Body outline for definition
    drawPath(bodyPath, darkColor.copy(alpha = 0.25f), style = Stroke(1.5f))

    // Scales pattern (subtle)
    for (i in 0..4) {
        val scaleX = cx + direction * size * (0.5f - i * 0.15f)
        val scaleY = cy - size * 0.15f + (i % 2) * size * 0.15f
        drawCircle(
            darkColor.copy(alpha = 0.08f),
            size * 0.12f,
            Offset(scaleX, scaleY),
            style = Stroke(0.8f)
        )
    }
}

// --- Clownfish ---
private fun DrawScope.drawClownfish(cx: Float, cy: Float, size: Float, time: Float, index: Int) {
    val tailWag = sin(time * 3f + index * 0.5f) * 0.2f

    // Body (orange)
    drawOval(
        color = FishOrange,
        topLeft = Offset(cx - size, cy - size * 0.5f),
        size = Size(size * 2f, size)
    )

    // White stripes
    drawRect(
        color = FishWhite,
        topLeft = Offset(cx - size * 0.1f, cy - size * 0.5f),
        size = Size(size * 0.2f, size)
    )
    drawRect(
        color = FishWhite,
        topLeft = Offset(cx + size * 0.5f, cy - size * 0.45f),
        size = Size(size * 0.15f, size * 0.9f)
    )

    // Tail
    val tailPath = Path().apply {
        moveTo(cx - size * 0.8f, cy)
        lineTo(cx - size * 1.3f, cy - size * 0.4f + tailWag * size)
        lineTo(cx - size * 1.3f, cy + size * 0.4f + tailWag * size)
        close()
    }
    drawPath(tailPath, FishOrangeDark)

    // Eye
    drawCircle(Color.White, size * 0.18f, Offset(cx + size * 0.55f, cy - size * 0.05f))
    drawCircle(Color.Black, size * 0.09f, Offset(cx + size * 0.58f, cy - size * 0.05f))
}

// --- Starfish ---
private fun DrawScope.drawStarfish(cx: Float, cy: Float, size: Float, mainColor: Color, darkColor: Color) {
    val arms = 5
    for (i in 0 until arms) {
        val angle = (i.toFloat() / arms) * 2f * PI.toFloat() - PI.toFloat() / 2f
        val endX = cx + cos(angle) * size
        val endY = cy + sin(angle) * size

        val armPath = Path().apply {
            moveTo(cx, cy)
            val ctrlAngle1 = angle - 0.3f
            val ctrlAngle2 = angle + 0.3f
            lineTo(cx + cos(ctrlAngle1) * size * 0.4f, cy + sin(ctrlAngle1) * size * 0.4f)
            lineTo(endX, endY)
            lineTo(cx + cos(ctrlAngle2) * size * 0.4f, cy + sin(ctrlAngle2) * size * 0.4f)
            close()
        }
        drawPath(armPath, mainColor)
        drawPath(armPath, darkColor, style = Stroke(1f))
    }

    // Center
    drawCircle(darkColor, size * 0.25f, Offset(cx, cy))
}

// --- Treasure Chest ---
private fun DrawScope.drawTreasureChest(cx: Float, cy: Float, size: Float, time: Float) {
    val openAmount = (sin(time * 0.5f) * 0.5f + 0.5f) * 0.3f

    // Box
    drawRect(
        color = TreasureBrown,
        topLeft = Offset(cx - size * 0.5f, cy - size * 0.3f),
        size = Size(size, size * 0.6f)
    )

    // Lid (slightly open)
    val lidPath = Path().apply {
        moveTo(cx - size * 0.55f, cy - size * 0.3f)
        lineTo(cx - size * 0.55f, cy - size * 0.5f - openAmount * size)
        quadraticTo(cx, cy - size * 0.65f - openAmount * size * 1.5f, cx + size * 0.55f, cy - size * 0.5f - openAmount * size)
        lineTo(cx + size * 0.55f, cy - size * 0.3f)
        close()
    }
    drawPath(lidPath, TreasureBrown.copy(alpha = 0.9f))

    // Gold shine
    if (openAmount > 0.1f) {
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(TreasureGold, TreasureGold.copy(alpha = 0.3f), Color.Transparent),
                center = Offset(cx, cy - size * 0.35f),
                radius = size * 0.4f
            ),
            topLeft = Offset(cx - size * 0.3f, cy - size * 0.5f),
            size = Size(size * 0.6f, size * 0.3f)
        )
    }

    // Lock
    drawCircle(TreasureGold, size * 0.08f, Offset(cx, cy - size * 0.1f))
}

// --- Turtle ---
private fun DrawScope.drawTurtle(cx: Float, cy: Float, size: Float, time: Float, index: Int) {
    val swimPhase = sin(time * 1.5f + index) * 0.15f

    // Shell
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(TurtleGreen, TurtleDark),
            center = Offset(cx, cy),
            radius = size * 0.7f
        ),
        topLeft = Offset(cx - size * 0.5f, cy - size * 0.35f),
        size = Size(size, size * 0.7f)
    )

    // Shell pattern
    drawOval(
        color = TurtleDark.copy(alpha = 0.4f),
        topLeft = Offset(cx - size * 0.25f, cy - size * 0.15f),
        size = Size(size * 0.5f, size * 0.3f),
        style = Stroke(2f)
    )

    // Head
    drawOval(
        color = TurtleGreen.copy(alpha = 0.9f),
        topLeft = Offset(cx + size * 0.35f, cy - size * 0.15f),
        size = Size(size * 0.35f, size * 0.3f)
    )

    // Eye
    drawCircle(Color.Black, size * 0.05f, Offset(cx + size * 0.55f, cy - size * 0.05f))

    // Flippers
    val flipAngle = swimPhase * 0.5f
    // Front flippers
    drawOval(
        color = TurtleGreen.copy(alpha = 0.8f),
        topLeft = Offset(cx + size * 0.1f + flipAngle * size, cy - size * 0.45f),
        size = Size(size * 0.4f, size * 0.2f)
    )
    drawOval(
        color = TurtleGreen.copy(alpha = 0.8f),
        topLeft = Offset(cx + size * 0.1f + flipAngle * size, cy + size * 0.25f),
        size = Size(size * 0.4f, size * 0.2f)
    )
    // Back flippers
    drawOval(
        color = TurtleGreen.copy(alpha = 0.7f),
        topLeft = Offset(cx - size * 0.55f - flipAngle * size * 0.5f, cy - size * 0.2f),
        size = Size(size * 0.25f, size * 0.15f)
    )
    drawOval(
        color = TurtleGreen.copy(alpha = 0.7f),
        topLeft = Offset(cx - size * 0.55f - flipAngle * size * 0.5f, cy + size * 0.1f),
        size = Size(size * 0.25f, size * 0.15f)
    )
}

// --- Seahorse ---
private fun DrawScope.drawSeahorse(cx: Float, cy: Float, size: Float, time: Float, index: Int) {
    val sway = sin(time + index * 0.7f) * 3f

    // Body (curved)
    val bodyPath = Path().apply {
        moveTo(cx, cy - size * 0.4f)
        quadraticTo(cx + size * 0.3f, cy, cx, cy + size * 0.3f)
        quadraticTo(cx - size * 0.2f, cy + size * 0.5f, cx - size * 0.1f + sway * 0.1f, cy + size * 0.7f)
    }
    drawPath(bodyPath, SeahorseYellow, style = Stroke(size * 0.25f, cap = StrokeCap.Round))

    // Head
    drawOval(
        color = SeahorseYellow,
        topLeft = Offset(cx - size * 0.15f, cy - size * 0.55f),
        size = Size(size * 0.35f, size * 0.3f)
    )

    // Snout
    drawLine(
        color = SeahorseDark,
        start = Offset(cx + size * 0.15f, cy - size * 0.45f),
        end = Offset(cx + size * 0.35f, cy - size * 0.42f),
        strokeWidth = size * 0.08f,
        cap = StrokeCap.Round
    )

    // Eye
    drawCircle(Color.Black, size * 0.05f, Offset(cx, cy - size * 0.45f))

    // Fin
    val finPath = Path().apply {
        moveTo(cx - size * 0.1f, cy - size * 0.1f)
        quadraticTo(cx - size * 0.35f + sway * 0.05f, cy, cx - size * 0.1f, cy + size * 0.1f)
    }
    drawPath(finPath, SeahorseYellow.copy(alpha = 0.6f), style = Stroke(2f))

    // Belly ridges
    for (i in 0..3) {
        val ridgeY = cy - size * 0.1f + i * size * 0.12f
        drawLine(
            color = SeahorseDark.copy(alpha = 0.3f),
            start = Offset(cx - size * 0.08f, ridgeY),
            end = Offset(cx + size * 0.08f, ridgeY),
            strokeWidth = 1f
        )
    }
}

// --- Crab ---
private fun DrawScope.drawCrab(cx: Float, cy: Float, size: Float, time: Float) {
    val clawMove = sin(time * 2f) * 0.1f

    // Body
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(CrabRed, CrabDark),
            center = Offset(cx, cy),
            radius = size * 0.6f
        ),
        topLeft = Offset(cx - size * 0.5f, cy - size * 0.25f),
        size = Size(size, size * 0.5f)
    )

    // Eyes on stalks
    for (side in listOf(-1f, 1f)) {
        val eyeX = cx + side * size * 0.25f
        // Stalk
        drawLine(
            color = CrabRed,
            start = Offset(eyeX, cy - size * 0.2f),
            end = Offset(eyeX + side * size * 0.05f, cy - size * 0.4f),
            strokeWidth = size * 0.08f,
            cap = StrokeCap.Round
        )
        // Eye
        drawCircle(Color.White, size * 0.08f, Offset(eyeX + side * size * 0.05f, cy - size * 0.42f))
        drawCircle(Color.Black, size * 0.04f, Offset(eyeX + side * size * 0.07f, cy - size * 0.42f))
    }

    // Claws
    for (side in listOf(-1f, 1f)) {
        val clawX = cx + side * size * 0.6f
        val clawOpenAngle = clawMove * side

        // Arm
        drawLine(
            color = CrabRed,
            start = Offset(cx + side * size * 0.4f, cy),
            end = Offset(clawX, cy - size * 0.1f),
            strokeWidth = size * 0.12f,
            cap = StrokeCap.Round
        )

        // Claw pincer
        drawOval(
            color = CrabDark,
            topLeft = Offset(clawX - size * 0.15f, cy - size * 0.25f + clawOpenAngle * size),
            size = Size(size * 0.3f, size * 0.15f)
        )
        drawOval(
            color = CrabDark,
            topLeft = Offset(clawX - size * 0.15f, cy - size * 0.1f - clawOpenAngle * size),
            size = Size(size * 0.3f, size * 0.15f)
        )
    }

    // Legs
    for (i in 0..2) {
        for (side in listOf(-1f, 1f)) {
            val legX = cx + side * (size * 0.35f + i * size * 0.15f)
            val legEndX = cx + side * (size * 0.55f + i * size * 0.18f)
            drawLine(
                color = CrabRed.copy(alpha = 0.8f),
                start = Offset(legX, cy + size * 0.15f),
                end = Offset(legEndX, cy + size * 0.35f),
                strokeWidth = size * 0.06f,
                cap = StrokeCap.Round
            )
        }
    }
}

// --- Realistic Blue Fish (Cartoon 3D Style like Fishdom) ---
private fun DrawScope.drawRealisticBlueFish(cx: Float, cy: Float, size: Float, time: Float, index: Int, direction: Float = 1f) {
    // Swimming animation - smooth continuous motion
    val phaseOffset = index * 3.7f
    val tailWag = sin(time * 2f + phaseOffset) * 0.3f
    val bodyBounce = sin(time * 1.5f + phaseOffset) * 0.08f
    val finFlap = sin(time * 2.5f + phaseOffset) * 0.4f

    // Colors - Turquoise/Cyan with yellow accents
    val bodyMainColor = Color(0xFF00CED1) // Turquoise
    val bodyLightColor = Color(0xFF7FFFD4) // Aquamarine light
    val bodyDarkColor = Color(0xFF008B8B) // Dark cyan
    val bellyColor = Color(0xFFB0E0E6) // Powder blue
    val accentYellow = Color(0xFFFDD835) // Golden yellow
    val accentOrange = Color(0xFFFFB74D) // Light orange
    val finColor = Color(0xFF40E0D0) // Turquoise
    val finLightColor = Color(0xFFAFEEEE) // Pale turquoise

    // Direction passed as parameter - fish faces the direction it's swimming
    val bodyY = cy + bodyBounce * size

    // === SHADOW ===
    drawOval(
        color = Color.Black.copy(alpha = 0.15f),
        topLeft = Offset(cx - size * 1.4f, bodyY + size * 0.9f),
        size = Size(size * 2.8f, size * 0.6f)
    )

    // === TAIL FIN (Caudal) - Behind body ===
    val tailPath = Path().apply {
        val tailBase = cx - direction * size * 1.0f
        val tailSpread = size * 1.2f
        moveTo(tailBase, bodyY)
        // Top lobe
        cubicTo(
            tailBase - direction * size * 0.4f, bodyY - tailSpread * 0.6f + tailWag * size * 0.8f,
            tailBase - direction * size * 0.8f, bodyY - tailSpread * 0.7f + tailWag * size,
            tailBase - direction * size * 1.1f, bodyY - tailSpread * 0.5f + tailWag * size * 0.9f
        )
        // Middle
        lineTo(tailBase - direction * size * 0.9f, bodyY + tailWag * size * 0.3f)
        // Bottom lobe
        cubicTo(
            tailBase - direction * size * 0.8f, bodyY + tailSpread * 0.7f + tailWag * size,
            tailBase - direction * size * 0.4f, bodyY + tailSpread * 0.6f + tailWag * size * 0.8f,
            tailBase, bodyY
        )
        close()
    }

    // Tail gradient
    drawPath(
        tailPath,
        brush = Brush.radialGradient(
            colors = listOf(
                finColor,
                finLightColor.copy(alpha = 0.9f),
                finLightColor.copy(alpha = 0.7f)
            ),
            center = Offset(cx - direction * size * 1.0f, bodyY),
            radius = size * 1.5f
        )
    )

    // Tail fin rays
    for (i in 0..6) {
        val rayAngle = -0.6f + i * 0.2f
        val rayLen = size * (0.9f + (i % 2) * 0.15f)
        val rayStart = Offset(cx - direction * size * 1.0f, bodyY)
        val rayEnd = Offset(
            rayStart.x - direction * cos(rayAngle) * rayLen,
            rayStart.y + sin(rayAngle) * rayLen + tailWag * size * 0.8f
        )
        drawLine(
            color = bodyDarkColor.copy(alpha = 0.3f),
            start = rayStart,
            end = rayEnd,
            strokeWidth = 1.2f,
            cap = StrokeCap.Round
        )
    }

    // === MAIN BODY (Chubby, round) ===
    val bodyPath = Path().apply {
        // Create a rounded, chubby fish body
        moveTo(cx + direction * size * 1.0f, bodyY) // Front (head)
        // Top curve
        cubicTo(
            cx + direction * size * 0.8f, bodyY - size * 0.9f,
            cx + direction * size * 0.2f, bodyY - size * 1.1f,
            cx - direction * size * 0.6f, bodyY - size * 0.8f
        )
        // Back top to tail
        cubicTo(
            cx - direction * size * 0.9f, bodyY - size * 0.5f,
            cx - direction * size * 1.0f, bodyY - size * 0.2f,
            cx - direction * size * 1.0f, bodyY
        )
        // Back bottom to tail
        cubicTo(
            cx - direction * size * 1.0f, bodyY + size * 0.2f,
            cx - direction * size * 0.9f, bodyY + size * 0.5f,
            cx - direction * size * 0.6f, bodyY + size * 0.8f
        )
        // Bottom curve
        cubicTo(
            cx + direction * size * 0.2f, bodyY + size * 1.1f,
            cx + direction * size * 0.8f, bodyY + size * 0.9f,
            cx + direction * size * 1.0f, bodyY
        )
        close()
    }

    // Body base gradient (3D effect)
    drawPath(
        bodyPath,
        brush = Brush.radialGradient(
            colors = listOf(
                bodyLightColor,
                bodyMainColor,
                bodyDarkColor
            ),
            center = Offset(cx + direction * size * 0.3f, bodyY - size * 0.2f),
            radius = size * 1.5f
        )
    )

    // === BELLY HIGHLIGHT (lighter area) ===
    val bellyPath = Path().apply {
        moveTo(cx + direction * size * 0.8f, bodyY)
        cubicTo(
            cx + direction * size * 0.5f, bodyY + size * 0.7f,
            cx, bodyY + size * 0.85f,
            cx - direction * size * 0.5f, bodyY + size * 0.6f
        )
        cubicTo(
            cx - direction * size * 0.3f, bodyY + size * 0.3f,
            cx + direction * size * 0.3f, bodyY + size * 0.2f,
            cx + direction * size * 0.8f, bodyY
        )
        close()
    }
    drawPath(bellyPath, bellyColor.copy(alpha = 0.6f))

    // === YELLOW/GOLD ACCENT near gills ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                accentYellow.copy(alpha = 0.7f),
                accentOrange.copy(alpha = 0.5f),
                Color.Transparent
            ),
            center = Offset(cx + direction * size * 0.2f, bodyY - size * 0.3f),
            radius = size * 0.5f
        ),
        topLeft = Offset(cx + direction * size * 0.0f, bodyY - size * 0.6f),
        size = Size(size * 0.5f, size * 0.6f)
    )

    // === SCALES PATTERN (detailed) ===
    for (row in 0..4) {
        for (col in 0..6) {
            val scaleX = cx + direction * (size * 0.6f - col * size * 0.25f)
            val scaleY = bodyY - size * 0.6f + row * size * 0.3f
            // Check if scale is within body bounds (rough check)
            if (scaleX > cx - size * 1.0f && scaleX < cx + size * 0.9f) {
                drawCircle(
                    color = bodyDarkColor.copy(alpha = 0.15f),
                    radius = size * 0.12f,
                    center = Offset(scaleX, scaleY),
                    style = Stroke(1.5f)
                )
                // Inner highlight on scale
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = size * 0.07f,
                    center = Offset(scaleX - size * 0.03f, scaleY - size * 0.03f)
                )
            }
        }
    }

    // === TOP GLOSSY HIGHLIGHT (3D shine effect) ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.5f),
                Color.White.copy(alpha = 0.25f),
                Color.Transparent
            ),
            center = Offset(cx + direction * size * 0.3f, bodyY - size * 0.5f),
            radius = size * 0.8f
        ),
        topLeft = Offset(cx + direction * size * 0.1f, bodyY - size * 0.9f),
        size = Size(size * 0.8f, size * 0.6f)
    )

    // === DORSAL FIN (Top) ===
    val dorsalPath = Path().apply {
        moveTo(cx + direction * size * 0.1f, bodyY - size * 0.9f)
        cubicTo(
            cx - direction * size * 0.1f, bodyY - size * 1.6f,
            cx - direction * size * 0.4f, bodyY - size * 1.5f,
            cx - direction * size * 0.5f, bodyY - size * 1.0f
        )
        lineTo(cx - direction * size * 0.3f, bodyY - size * 0.8f)
        close()
    }
    drawPath(
        dorsalPath,
        brush = Brush.linearGradient(
            colors = listOf(finColor, finLightColor),
            start = Offset(cx, bodyY - size * 1.6f),
            end = Offset(cx, bodyY - size * 0.8f)
        )
    )
    // Dorsal fin rays
    for (i in 0..4) {
        val rayT = i / 4f
        val rayStart = Offset(
            cx + direction * size * (0.1f - rayT * 0.6f),
            bodyY - size * (0.9f + rayT * 0.1f)
        )
        val rayEnd = Offset(
            cx - direction * size * (0.1f + rayT * 0.4f),
            bodyY - size * (1.6f - rayT * 0.6f)
        )
        drawLine(
            color = bodyDarkColor.copy(alpha = 0.3f),
            start = rayStart,
            end = rayEnd,
            strokeWidth = 1.2f
        )
    }

    // === PECTORAL FINS (side fins) - Animated ===
    val pectoralPath = Path().apply {
        moveTo(cx + direction * size * 0.3f, bodyY + size * 0.2f)
        cubicTo(
            cx + direction * size * (0.2f + finFlap * 0.5f), bodyY + size * 0.6f,
            cx + direction * size * (0.3f + finFlap * 0.4f), bodyY + size * 0.9f,
            cx + direction * size * 0.5f, bodyY + size * 0.5f
        )
        close()
    }
    drawPath(pectoralPath, finColor.copy(alpha = 0.7f))
    // Pectoral rays
    for (i in 0..3) {
        val rayStart = Offset(cx + direction * size * 0.3f, bodyY + size * 0.2f)
        val rayEnd = Offset(
            cx + direction * size * (0.3f + finFlap * 0.4f + i * 0.05f),
            bodyY + size * (0.4f + i * 0.15f)
        )
        drawLine(
            color = bodyDarkColor.copy(alpha = 0.25f),
            start = rayStart,
            end = rayEnd,
            strokeWidth = 1f
        )
    }

    // Second pectoral (other side, partially visible)
    drawOval(
        color = finColor.copy(alpha = 0.4f),
        topLeft = Offset(cx + direction * size * 0.2f - finFlap * size * 0.3f, bodyY - size * 0.1f),
        size = Size(size * 0.35f, size * 0.6f)
    )

    // === VENTRAL FINS (bottom small fins) ===
    drawOval(
        color = finColor.copy(alpha = 0.6f),
        topLeft = Offset(cx + direction * size * 0.0f, bodyY + size * 0.7f),
        size = Size(size * 0.25f, size * 0.4f)
    )

    // === EYEBROW/CREST (golden arc above eye) ===
    val eyebrowPath = Path().apply {
        moveTo(cx + direction * size * 0.5f, bodyY - size * 0.5f)
        quadraticTo(
            cx + direction * size * 0.7f, bodyY - size * 0.6f,
            cx + direction * size * 0.85f, bodyY - size * 0.45f
        )
    }
    drawPath(
        eyebrowPath,
        brush = Brush.linearGradient(
            colors = listOf(accentYellow, accentOrange),
            start = Offset(cx + direction * size * 0.5f, bodyY - size * 0.5f),
            end = Offset(cx + direction * size * 0.85f, bodyY - size * 0.45f)
        ),
        style = Stroke(size * 0.15f, cap = StrokeCap.Round)
    )

    // === HUGE CARTOON EYES ===
    val eyeX = cx + direction * size * 0.65f
    val eyeY = bodyY - size * 0.2f
    val eyeRadius = size * 0.45f // VERY LARGE

    // Eye white (slightly oval)
    drawOval(
        color = Color.White,
        topLeft = Offset(eyeX - eyeRadius, eyeY - eyeRadius * 1.1f),
        size = Size(eyeRadius * 2f, eyeRadius * 2.2f)
    )

    // Eye outline
    drawOval(
        color = bodyDarkColor.copy(alpha = 0.3f),
        topLeft = Offset(eyeX - eyeRadius, eyeY - eyeRadius * 1.1f),
        size = Size(eyeRadius * 2f, eyeRadius * 2.2f),
        style = Stroke(2f)
    )

    // Iris (purple/magenta)
    val irisRadius = eyeRadius * 0.6f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFD946EF), // Bright magenta
                Color(0xFF9333EA), // Purple
                Color(0xFF6B21A8)  // Dark purple
            ),
            center = Offset(eyeX + direction * size * 0.05f, eyeY),
            radius = irisRadius
        ),
        radius = irisRadius,
        center = Offset(eyeX + direction * size * 0.05f, eyeY)
    )

    // Pupil (large, black)
    val pupilRadius = eyeRadius * 0.35f
    drawCircle(
        Color.Black,
        pupilRadius,
        Offset(eyeX + direction * size * 0.08f, eyeY + size * 0.02f)
    )

    // Eye shine (large white highlight)
    drawCircle(
        Color.White.copy(alpha = 0.95f),
        eyeRadius * 0.18f,
        Offset(eyeX + direction * size * 0.12f, eyeY - size * 0.08f)
    )
    // Secondary shine
    drawCircle(
        Color.White.copy(alpha = 0.6f),
        eyeRadius * 0.1f,
        Offset(eyeX - direction * size * 0.05f, eyeY + size * 0.1f)
    )

    // === SMILING MOUTH (large, open) ===
    val mouthPath = Path().apply {
        val mouthCenterX = cx + direction * size * 0.85f
        val mouthY = bodyY + size * 0.15f
        moveTo(mouthCenterX - size * 0.15f, mouthY)
        quadraticTo(
            mouthCenterX, mouthY + size * 0.2f,
            mouthCenterX + size * 0.15f, mouthY
        )
    }

    // Mouth opening (dark)
    drawPath(
        mouthPath,
        Color(0xFF2C1810),
        style = Stroke(size * 0.12f, cap = StrokeCap.Round)
    )

    // Tongue (pink, inside mouth)
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFF6B9D),
                Color(0xFFE91E63)
            ),
            center = Offset(cx + direction * size * 0.85f, bodyY + size * 0.22f),
            radius = size * 0.15f
        ),
        topLeft = Offset(cx + direction * size * 0.78f, bodyY + size * 0.15f),
        size = Size(size * 0.14f, size * 0.12f)
    )

    // Mouth outline/smile
    drawPath(
        mouthPath,
        bodyDarkColor.copy(alpha = 0.5f),
        style = Stroke(size * 0.08f, cap = StrokeCap.Round)
    )

    // === CHEEK DOTS (cute detail) ===
    drawCircle(
        Color(0xFFFF9ECD).copy(alpha = 0.3f),
        size * 0.12f,
        Offset(cx + direction * size * 0.55f, bodyY + size * 0.25f)
    )

    // === BODY OUTLINE (subtle, for definition) ===
    drawPath(
        bodyPath,
        bodyDarkColor.copy(alpha = 0.2f),
        style = Stroke(2f)
    )
}

// --- Realistic Clownfish (Orange with white stripes) ---
private fun DrawScope.drawRealisticClownfish(cx: Float, cy: Float, size: Float, time: Float, index: Int, direction: Float = 1f) {
    // Swimming animation
    val phaseOffset = index * 4.2f
    val tailWag = sin(time * 2f + phaseOffset) * 0.35f
    val bodyBounce = sin(time * 1.5f + phaseOffset) * 0.08f
    val finFlap = sin(time * 2.5f + phaseOffset) * 0.4f

    // Colors - Orange/Red with white stripes
    val bodyOrange = Color(0xFFFF6B35) // Bright orange
    val bodyOrangeDark = Color(0xFFD84315) // Dark orange/red
    val bodyOrangeLight = Color(0xFFFFB74D) // Light orange
    val whiteStripe = Color(0xFFFFFFFF) // White
    val blackOutline = Color(0xFF1A1A1A) // Black for outlines

    // Direction passed as parameter - fish faces the direction it's swimming
    val bodyY = cy + bodyBounce * size

    // === SHADOW ===
    drawOval(
        color = Color.Black.copy(alpha = 0.15f),
        topLeft = Offset(cx - size * 1.3f, bodyY + size * 0.9f),
        size = Size(size * 2.6f, size * 0.6f)
    )

    // === TAIL FIN ===
    val tailPath = Path().apply {
        val tailBase = cx - direction * size * 0.9f
        val tailSpread = size * 1.0f
        moveTo(tailBase, bodyY)
        cubicTo(
            tailBase - direction * size * 0.3f, bodyY - tailSpread * 0.5f + tailWag * size * 0.7f,
            tailBase - direction * size * 0.6f, bodyY - tailSpread * 0.6f + tailWag * size * 0.8f,
            tailBase - direction * size * 0.8f, bodyY - tailSpread * 0.4f + tailWag * size
        )
        lineTo(tailBase - direction * size * 0.7f, bodyY + tailWag * size * 0.2f)
        cubicTo(
            tailBase - direction * size * 0.6f, bodyY + tailSpread * 0.6f + tailWag * size * 0.8f,
            tailBase - direction * size * 0.3f, bodyY + tailSpread * 0.5f + tailWag * size * 0.7f,
            tailBase, bodyY
        )
        close()
    }

    // Tail - Orange with gradient
    drawPath(
        tailPath,
        brush = Brush.radialGradient(
            colors = listOf(bodyOrange, bodyOrangeDark),
            center = Offset(cx - direction * size * 0.9f, bodyY),
            radius = size * 1.2f
        )
    )

    // === MAIN BODY ===
    val bodyPath = Path().apply {
        moveTo(cx + direction * size * 0.95f, bodyY)
        cubicTo(
            cx + direction * size * 0.75f, bodyY - size * 0.85f,
            cx + direction * size * 0.15f, bodyY - size * 1.0f,
            cx - direction * size * 0.5f, bodyY - size * 0.75f
        )
        cubicTo(
            cx - direction * size * 0.85f, bodyY - size * 0.45f,
            cx - direction * size * 0.9f, bodyY - size * 0.15f,
            cx - direction * size * 0.9f, bodyY
        )
        cubicTo(
            cx - direction * size * 0.9f, bodyY + size * 0.15f,
            cx - direction * size * 0.85f, bodyY + size * 0.45f,
            cx - direction * size * 0.5f, bodyY + size * 0.75f
        )
        cubicTo(
            cx + direction * size * 0.15f, bodyY + size * 1.0f,
            cx + direction * size * 0.75f, bodyY + size * 0.85f,
            cx + direction * size * 0.95f, bodyY
        )
        close()
    }

    // Body gradient
    drawPath(
        bodyPath,
        brush = Brush.radialGradient(
            colors = listOf(
                bodyOrangeLight,
                bodyOrange,
                bodyOrangeDark
            ),
            center = Offset(cx + direction * size * 0.2f, bodyY - size * 0.2f),
            radius = size * 1.3f
        )
    )

    // === WHITE STRIPES (characteristic of clownfish) ===
    // Stripe 1 - Near head
    val stripe1Path = Path().apply {
        val stripeX = cx + direction * size * 0.45f
        val stripeWidth = size * 0.35f
        moveTo(stripeX - stripeWidth * 0.5f, bodyY - size * 0.75f)
        cubicTo(
            stripeX, bodyY - size * 0.85f,
            stripeX, bodyY - size * 0.85f,
            stripeX + stripeWidth * 0.5f, bodyY - size * 0.75f
        )
        lineTo(stripeX + stripeWidth * 0.45f, bodyY + size * 0.75f)
        cubicTo(
            stripeX, bodyY + size * 0.85f,
            stripeX, bodyY + size * 0.85f,
            stripeX - stripeWidth * 0.45f, bodyY + size * 0.75f
        )
        close()
    }
    drawPath(stripe1Path, whiteStripe)
    drawPath(stripe1Path, blackOutline.copy(alpha = 0.3f), style = Stroke(2f))

    // Stripe 2 - Middle
    val stripe2Path = Path().apply {
        val stripeX = cx + direction * size * 0.05f
        val stripeWidth = size * 0.3f
        moveTo(stripeX - stripeWidth * 0.5f, bodyY - size * 0.88f)
        cubicTo(
            stripeX, bodyY - size * 0.95f,
            stripeX, bodyY - size * 0.95f,
            stripeX + stripeWidth * 0.5f, bodyY - size * 0.88f
        )
        lineTo(stripeX + stripeWidth * 0.45f, bodyY + size * 0.88f)
        cubicTo(
            stripeX, bodyY + size * 0.95f,
            stripeX, bodyY + size * 0.95f,
            stripeX - stripeWidth * 0.45f, bodyY + size * 0.88f
        )
        close()
    }
    drawPath(stripe2Path, whiteStripe)
    drawPath(stripe2Path, blackOutline.copy(alpha = 0.3f), style = Stroke(2f))

    // Stripe 3 - Near tail
    val stripe3Path = Path().apply {
        val stripeX = cx - direction * size * 0.45f
        val stripeWidth = size * 0.25f
        moveTo(stripeX - stripeWidth * 0.5f, bodyY - size * 0.65f)
        lineTo(stripeX + stripeWidth * 0.5f, bodyY - size * 0.65f)
        lineTo(stripeX + stripeWidth * 0.45f, bodyY + size * 0.65f)
        lineTo(stripeX - stripeWidth * 0.45f, bodyY + size * 0.65f)
        close()
    }
    drawPath(stripe3Path, whiteStripe)
    drawPath(stripe3Path, blackOutline.copy(alpha = 0.3f), style = Stroke(2f))

    // === GLOSSY HIGHLIGHT ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.5f),
                Color.White.copy(alpha = 0.2f),
                Color.Transparent
            ),
            center = Offset(cx + direction * size * 0.25f, bodyY - size * 0.45f),
            radius = size * 0.7f
        ),
        topLeft = Offset(cx + direction * size * 0.05f, bodyY - size * 0.8f),
        size = Size(size * 0.7f, size * 0.5f)
    )

    // === DORSAL FIN ===
    val dorsalPath = Path().apply {
        moveTo(cx + direction * size * 0.15f, bodyY - size * 0.85f)
        cubicTo(
            cx - direction * size * 0.05f, bodyY - size * 1.5f,
            cx - direction * size * 0.35f, bodyY - size * 1.4f,
            cx - direction * size * 0.45f, bodyY - size * 0.95f
        )
        lineTo(cx - direction * size * 0.25f, bodyY - size * 0.75f)
        close()
    }
    drawPath(
        dorsalPath,
        brush = Brush.linearGradient(
            colors = listOf(bodyOrange, bodyOrangeDark),
            start = Offset(cx, bodyY - size * 1.5f),
            end = Offset(cx, bodyY - size * 0.75f)
        )
    )

    // === PECTORAL FINS ===
    val pectoralPath = Path().apply {
        moveTo(cx + direction * size * 0.35f, bodyY + size * 0.15f)
        cubicTo(
            cx + direction * size * (0.25f + finFlap * 0.5f), bodyY + size * 0.55f,
            cx + direction * size * (0.35f + finFlap * 0.4f), bodyY + size * 0.85f,
            cx + direction * size * 0.55f, bodyY + size * 0.45f
        )
        close()
    }
    drawPath(pectoralPath, bodyOrange.copy(alpha = 0.7f))

    // === HUGE CARTOON EYES ===
    val eyeX = cx + direction * size * 0.7f
    val eyeY = bodyY - size * 0.15f
    val eyeRadius = size * 0.38f

    // Eye white
    drawOval(
        color = Color.White,
        topLeft = Offset(eyeX - eyeRadius, eyeY - eyeRadius * 1.05f),
        size = Size(eyeRadius * 2f, eyeRadius * 2.1f)
    )

    // Eye outline
    drawOval(
        color = blackOutline.copy(alpha = 0.3f),
        topLeft = Offset(eyeX - eyeRadius, eyeY - eyeRadius * 1.05f),
        size = Size(eyeRadius * 2f, eyeRadius * 2.1f),
        style = Stroke(2f)
    )

    // Iris (bright blue)
    val irisRadius = eyeRadius * 0.55f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF3B82F6), // Bright blue
                Color(0xFF2563EB), // Blue
                Color(0xFF1E40AF)  // Dark blue
            ),
            center = Offset(eyeX + direction * size * 0.05f, eyeY),
            radius = irisRadius
        ),
        radius = irisRadius,
        center = Offset(eyeX + direction * size * 0.05f, eyeY)
    )

    // Pupil
    val pupilRadius = eyeRadius * 0.32f
    drawCircle(
        Color.Black,
        pupilRadius,
        Offset(eyeX + direction * size * 0.08f, eyeY + size * 0.02f)
    )

    // Eye shines
    drawCircle(
        Color.White.copy(alpha = 0.95f),
        eyeRadius * 0.16f,
        Offset(eyeX + direction * size * 0.12f, eyeY - size * 0.07f)
    )
    drawCircle(
        Color.White.copy(alpha = 0.6f),
        eyeRadius * 0.09f,
        Offset(eyeX - direction * size * 0.04f, eyeY + size * 0.09f)
    )

    // === SMILING MOUTH ===
    val mouthPath = Path().apply {
        val mouthCenterX = cx + direction * size * 0.88f
        val mouthY = bodyY + size * 0.12f
        moveTo(mouthCenterX - size * 0.12f, mouthY)
        quadraticTo(
            mouthCenterX, mouthY + size * 0.15f,
            mouthCenterX + size * 0.12f, mouthY
        )
    }

    drawPath(
        mouthPath,
        Color(0xFF2C1810),
        style = Stroke(size * 0.1f, cap = StrokeCap.Round)
    )

    drawPath(
        mouthPath,
        bodyOrangeDark.copy(alpha = 0.6f),
        style = Stroke(size * 0.06f, cap = StrokeCap.Round)
    )

    // === BODY OUTLINE ===
    drawPath(
        bodyPath,
        blackOutline.copy(alpha = 0.2f),
        style = Stroke(2f)
    )
}

// --- Realistic Turtle (Cartoon 3D) ---
private fun DrawScope.drawRealisticTurtle(cx: Float, cy: Float, size: Float, time: Float, index: Int, direction: Float = 1f) {
    val phaseOffset = index * 3.9f
    val flipperFlap = sin(time * 1.8f + phaseOffset) * 0.35f
    val bodyBounce = sin(time * 1.2f + phaseOffset) * 0.06f

    val shellGreen = Color(0xFF2E7D32) // Dark green
    val shellLight = Color(0xFF66BB6A) // Light green
    val skinGreen = Color(0xFF81C784) // Skin green
    val shellPattern = Color(0xFF1B5E20) // Very dark green
    val eyeColor = Color(0xFF1A1A1A)

    val bodyY = cy + bodyBounce * size
    // Direction passed as parameter - turtle faces the direction it's swimming

    // Shadow
    drawOval(
        color = Color.Black.copy(alpha = 0.15f),
        topLeft = Offset(cx - size * 0.8f, bodyY + size * 0.6f),
        size = Size(size * 1.6f, size * 0.5f)
    )

    // === BACK FLIPPERS (behind shell) ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(skinGreen, shellGreen),
            center = Offset(cx - direction * size * 0.4f, bodyY - size * 0.1f),
            radius = size * 0.3f
        ),
        topLeft = Offset(cx - direction * size * 0.65f - flipperFlap * size * 0.4f, bodyY - size * 0.25f),
        size = Size(size * 0.4f, size * 0.25f)
    )
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(skinGreen, shellGreen),
            center = Offset(cx - direction * size * 0.4f, bodyY + size * 0.15f),
            radius = size * 0.3f
        ),
        topLeft = Offset(cx - direction * size * 0.65f - flipperFlap * size * 0.4f, bodyY + size * 0.05f),
        size = Size(size * 0.4f, size * 0.25f)
    )

    // === SHELL (main body) ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(shellLight, shellGreen, shellGreen.copy(alpha = 0.9f)),
            center = Offset(cx, bodyY - size * 0.1f),
            radius = size * 0.7f
        ),
        topLeft = Offset(cx - size * 0.65f, bodyY - size * 0.5f),
        size = Size(size * 1.3f, size)
    )

    // Shell pattern (hexagons)
    for (row in 0..1) {
        for (col in 0..2) {
            val patternX = cx - size * 0.35f + col * size * 0.35f
            val patternY = bodyY - size * 0.25f + row * size * 0.3f
            drawCircle(
                color = shellPattern.copy(alpha = 0.4f),
                radius = size * 0.15f,
                center = Offset(patternX, patternY),
                style = Stroke(2.5f)
            )
        }
    }

    // Shell highlight
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.4f),
                Color.Transparent
            ),
            center = Offset(cx - size * 0.2f, bodyY - size * 0.3f),
            radius = size * 0.4f
        ),
        topLeft = Offset(cx - size * 0.4f, bodyY - size * 0.45f),
        size = Size(size * 0.6f, size * 0.4f)
    )

    // === HEAD ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(skinGreen, shellGreen.copy(alpha = 0.8f)),
            center = Offset(cx + direction * size * 0.65f, bodyY - size * 0.05f),
            radius = size * 0.35f
        ),
        topLeft = Offset(cx + direction * size * 0.4f, bodyY - size * 0.25f),
        size = Size(size * 0.5f, size * 0.4f)
    )

    // Eyes
    val eyeX = cx + direction * size * 0.72f
    val eyeY = bodyY - size * 0.1f
    drawCircle(
        Color.White,
        size * 0.12f,
        Offset(eyeX, eyeY)
    )
    drawCircle(
        eyeColor,
        size * 0.07f,
        Offset(eyeX + direction * size * 0.02f, eyeY)
    )
    drawCircle(
        Color.White.copy(alpha = 0.8f),
        size * 0.03f,
        Offset(eyeX + direction * size * 0.04f, eyeY - size * 0.02f)
    )

    // Smile
    val smilePath = Path().apply {
        moveTo(cx + direction * size * 0.75f, bodyY + size * 0.05f)
        quadraticTo(
            cx + direction * size * 0.82f, bodyY + size * 0.12f,
            cx + direction * size * 0.88f, bodyY + size * 0.06f
        )
    }
    drawPath(smilePath, eyeColor.copy(alpha = 0.5f), style = Stroke(2f, cap = StrokeCap.Round))

    // === FRONT FLIPPERS (in front of shell) ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(skinGreen, shellGreen),
            center = Offset(cx + direction * size * 0.2f, bodyY - size * 0.35f),
            radius = size * 0.3f
        ),
        topLeft = Offset(cx + direction * size * 0.0f + flipperFlap * size * 0.5f, bodyY - size * 0.6f),
        size = Size(size * 0.5f, size * 0.3f)
    )
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(skinGreen, shellGreen),
            center = Offset(cx + direction * size * 0.2f, bodyY + size * 0.35f),
            radius = size * 0.3f
        ),
        topLeft = Offset(cx + direction * size * 0.0f + flipperFlap * size * 0.5f, bodyY + size * 0.35f),
        size = Size(size * 0.5f, size * 0.3f)
    )
}

// --- Realistic Seahorse (Cartoon 3D) ---
private fun DrawScope.drawRealisticSeahorse(cx: Float, cy: Float, size: Float, time: Float, index: Int) {
    val phaseOffset = index * 4.1f
    val sway = sin(time + phaseOffset) * size * 0.15f
    val tailCurl = sin(time * 0.8f + phaseOffset) * 0.3f

    val bodyYellow = Color(0xFFFDD835) // Golden yellow
    val bodyOrange = Color(0xFFFFB74D) // Light orange
    val bodyDark = Color(0xFFF57C00) // Dark orange

    // Shadow
    drawOval(
        color = Color.Black.copy(alpha = 0.12f),
        topLeft = Offset(cx - size * 0.35f, cy + size * 0.9f),
        size = Size(size * 0.7f, size * 0.4f)
    )

    // === CURVED BODY/TAIL ===
    val bodyPath = Path().apply {
        moveTo(cx, cy - size * 0.5f) // Head
        // Body curves down and back
        cubicTo(
            cx + size * 0.35f, cy - size * 0.2f,
            cx + size * 0.4f + sway * 0.3f, cy + size * 0.2f,
            cx + size * 0.25f + sway * 0.5f, cy + size * 0.5f
        )
        // Tail curls
        cubicTo(
            cx + size * 0.1f + sway * 0.7f, cy + size * 0.75f,
            cx - size * 0.15f + sway, cy + size * 0.85f + tailCurl * size * 0.2f,
            cx - size * 0.25f + sway * 0.8f, cy + size * 0.75f + tailCurl * size * 0.3f
        )
    }

    drawPath(
        bodyPath,
        brush = Brush.linearGradient(
            colors = listOf(bodyYellow, bodyOrange, bodyDark),
            start = Offset(cx, cy - size * 0.5f),
            end = Offset(cx - size * 0.25f, cy + size * 0.75f)
        ),
        style = Stroke(size * 0.28f, cap = StrokeCap.Round)
    )

    // === HEAD (rounded) ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(bodyYellow, bodyOrange),
            center = Offset(cx, cy - size * 0.5f),
            radius = size * 0.35f
        ),
        topLeft = Offset(cx - size * 0.25f, cy - size * 0.75f),
        size = Size(size * 0.5f, size * 0.45f)
    )

    // === CROWN/SPIKES on head ===
    for (i in 0..3) {
        val spikeAngle = -PI.toFloat() * 0.6f + i * 0.15f
        val spikeLen = size * (0.2f + (i % 2) * 0.08f)
        val spikeStart = Offset(cx - size * 0.05f, cy - size * 0.72f)
        val spikeEnd = Offset(
            spikeStart.x + cos(spikeAngle) * spikeLen,
            spikeStart.y + sin(spikeAngle) * spikeLen
        )
        drawLine(
            color = bodyDark,
            start = spikeStart,
            end = spikeEnd,
            strokeWidth = size * 0.08f,
            cap = StrokeCap.Round
        )
    }

    // === SNOUT ===
    drawLine(
        color = bodyDark.copy(alpha = 0.9f),
        start = Offset(cx + size * 0.2f, cy - size * 0.58f),
        end = Offset(cx + size * 0.48f, cy - size * 0.54f),
        strokeWidth = size * 0.12f,
        cap = StrokeCap.Round
    )

    // === BIG CARTOON EYE ===
    val eyeX = cx + size * 0.05f
    val eyeY = cy - size * 0.58f
    val eyeRadius = size * 0.18f

    drawCircle(Color.White, eyeRadius, Offset(eyeX, eyeY))
    drawCircle(
        Color.Black.copy(alpha = 0.3f),
        eyeRadius,
        Offset(eyeX, eyeY),
        style = Stroke(1.5f)
    )

    // Iris
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF1976D2), Color(0xFF0D47A1)),
            center = Offset(eyeX + size * 0.02f, eyeY),
            radius = eyeRadius * 0.6f
        ),
        radius = eyeRadius * 0.6f,
        center = Offset(eyeX + size * 0.02f, eyeY)
    )

    // Pupil
    drawCircle(Color.Black, eyeRadius * 0.35f, Offset(eyeX + size * 0.03f, eyeY))

    // Shine
    drawCircle(
        Color.White.copy(alpha = 0.9f),
        eyeRadius * 0.15f,
        Offset(eyeX + size * 0.05f, eyeY - size * 0.04f)
    )

    // === DORSAL FIN (wavy) ===
    val finPath = Path().apply {
        moveTo(cx + size * 0.05f, cy - size * 0.25f)
        quadraticTo(
            cx + size * 0.25f + sway * 0.4f,
            cy,
            cx + size * 0.08f,
            cy + size * 0.25f
        )
    }
    drawPath(
        finPath,
        bodyYellow.copy(alpha = 0.6f),
        style = Stroke(size * 0.1f, cap = StrokeCap.Round)
    )

    // === BELLY RIDGES ===
    for (i in 0..4) {
        val ridgeY = cy - size * 0.3f + i * size * 0.18f
        val ridgeX = cx + size * 0.18f + sway * 0.2f
        drawLine(
            color = bodyDark.copy(alpha = 0.3f),
            start = Offset(ridgeX - size * 0.08f, ridgeY),
            end = Offset(ridgeX + size * 0.08f, ridgeY),
            strokeWidth = 1.8f
        )
    }
}

// --- Realistic Crab (Cartoon 3D) ---
private fun DrawScope.drawRealisticCrab(cx: Float, cy: Float, size: Float, time: Float) {
    val clawMove = sin(time * 2f) * 0.15f
    val eyeWave = sin(time * 1.5f) * 0.08f

    val crabRed = Color(0xFFE53935) // Bright red
    val crabDark = Color(0xFFC62828) // Dark red
    val crabLight = Color(0xFFFF6F60) // Light red/coral

    // Shadow
    drawOval(
        color = Color.Black.copy(alpha = 0.18f),
        topLeft = Offset(cx - size * 0.7f, cy + size * 0.15f),
        size = Size(size * 1.4f, size * 0.4f)
    )

    // === BODY (oval shell) ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(crabLight, crabRed, crabDark),
            center = Offset(cx, cy - size * 0.15f),
            radius = size * 0.65f
        ),
        topLeft = Offset(cx - size * 0.6f, cy - size * 0.35f),
        size = Size(size * 1.2f, size * 0.7f)
    )

    // Body spots/pattern
    for (i in 0..3) {
        val spotX = cx - size * 0.3f + (i % 2) * size * 0.3f
        val spotY = cy - size * 0.25f + (i / 2) * size * 0.2f
        drawCircle(
            crabDark.copy(alpha = 0.3f),
            size * 0.08f,
            Offset(spotX, spotY)
        )
    }

    // === EYES ON STALKS ===
    for (side in listOf(-1f, 1f)) {
        val eyeStalkX = cx + side * size * 0.3f
        // Stalk
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(crabRed, crabLight),
                start = Offset(eyeStalkX, cy - size * 0.3f),
                end = Offset(eyeStalkX + side * size * 0.08f, cy - size * 0.6f + eyeWave * size)
            ),
            start = Offset(eyeStalkX, cy - size * 0.3f),
            end = Offset(eyeStalkX + side * size * 0.08f, cy - size * 0.6f + eyeWave * size),
            strokeWidth = size * 0.12f,
            cap = StrokeCap.Round
        )
        // Eye ball
        drawCircle(
            Color.White,
            size * 0.14f,
            Offset(eyeStalkX + side * size * 0.08f, cy - size * 0.62f + eyeWave * size)
        )
        // Pupil
        drawCircle(
            Color.Black,
            size * 0.08f,
            Offset(eyeStalkX + side * size * 0.11f, cy - size * 0.62f + eyeWave * size)
        )
        // Shine
        drawCircle(
            Color.White.copy(alpha = 0.8f),
            size * 0.04f,
            Offset(eyeStalkX + side * size * 0.12f, cy - size * 0.65f + eyeWave * size)
        )
    }

    // === CLAWS (pincers) ===
    for (side in listOf(-1f, 1f)) {
        val clawX = cx + side * size * 0.7f
        val clawOpenAngle = clawMove * side

        // Arm
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(crabRed, crabDark),
                start = Offset(cx + side * size * 0.5f, cy - size * 0.05f),
                end = Offset(clawX, cy - size * 0.15f)
            ),
            start = Offset(cx + side * size * 0.5f, cy - size * 0.05f),
            end = Offset(clawX, cy - size * 0.15f),
            strokeWidth = size * 0.18f,
            cap = StrokeCap.Round
        )

        // Top pincer
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(crabLight, crabDark),
                center = Offset(clawX, cy - size * 0.3f),
                radius = size * 0.25f
            ),
            topLeft = Offset(clawX - size * 0.2f, cy - size * 0.4f + clawOpenAngle * size),
            size = Size(size * 0.4f, size * 0.22f)
        )

        // Bottom pincer
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(crabLight, crabDark),
                center = Offset(clawX, cy),
                radius = size * 0.25f
            ),
            topLeft = Offset(clawX - size * 0.2f, cy - size * 0.08f - clawOpenAngle * size),
            size = Size(size * 0.4f, size * 0.22f)
        )
    }

    // === LEGS (walking legs) ===
    for (i in 0..2) {
        for (side in listOf(-1f, 1f)) {
            val legBaseX = cx + side * (size * 0.4f + i * size * 0.12f)
            val legEndX = cx + side * (size * 0.65f + i * size * 0.15f)
            val legY = cy + size * 0.08f

            drawLine(
                color = crabRed.copy(alpha = 0.85f),
                start = Offset(legBaseX, legY),
                end = Offset(legEndX, legY + size * 0.25f),
                strokeWidth = size * 0.08f,
                cap = StrokeCap.Round
            )
        }
    }
}
