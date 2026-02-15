package com.jellydrink.app.ui.components

import androidx.compose.animation.core.rememberInfiniteTransition
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
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AquariumBackground(
    modifier: Modifier = Modifier,
    placedDecorations: List<DecorationEntity> = emptyList()
) {
    val inf = rememberInfiniteTransition(label = "aquarium")

    // All phase animations use smoothPhase helper (0→2π, LinearEasing, Restart)
    val phase1 by inf.smoothPhase(10000, "phase1")
    val phase2 by inf.smoothPhase(15000, "phase2")
    val phase3 by inf.smoothPhase(20000, "phase3")
    val phase4 by inf.smoothPhase(25000, "phase4")
    val phaseFast by inf.smoothPhase(6000, "phaseFast")
    val phaseSlow by inf.smoothPhase(30000, "phaseSlow")

    // Fish swim phases (ultra-slow: 60-120s cycles)
    val fishPhase1 by inf.smoothPhase(67000, "fishPhase1")
    val fishPhase2 by inf.smoothPhase(83000, "fishPhase2")
    val fishPhase3 by inf.smoothPhase(97000, "fishPhase3")
    val fishPhase4 by inf.smoothPhase(113000, "fishPhase4")
    val fishPhase5 by inf.smoothPhase(127000, "fishPhase5")

    // Seahorse phases (prime periods for organic drift)
    val seahorsePhaseA by inf.smoothPhase(37013, "shA")
    val seahorsePhaseB by inf.smoothPhase(50021, "shB")
    val seahorsePhaseC by inf.smoothPhase(67003, "shC")

    // Crab phase (~20s cycle)
    val crabPhase by inf.smoothPhase(20011, "crabP")

    // Fish swim animation phase (tail, fins)
    val swimPhase by inf.smoothPhase(3001, "swim")

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
            val alpha = 0.020f + (i % 2) * 0.012f + sin(phase4 + i * 1.2f) * 0.005f

            // Usa phase4 (25s) direttamente — nessun moltiplicatore, zero scatti
            val raySway = sin(phase4 + i * 0.9f) * 12f
            val rayPath = Path().apply {
                moveTo(baseX - topWidth, 0f)
                lineTo(baseX + topWidth, 0f)
                lineTo(baseX + botWidth + raySway, rayLength)
                lineTo(baseX - botWidth + raySway, rayLength)
                close()
            }
            drawPath(rayPath, LightRayColor.copy(alpha = alpha))

            // Bordo sfumato più morbido (ray glow)
            val glowPath = Path().apply {
                moveTo(baseX - topWidth * 2f, 0f)
                lineTo(baseX + topWidth * 2f, 0f)
                lineTo(baseX + botWidth * 1.5f + raySway, rayLength * 0.8f)
                lineTo(baseX - botWidth * 1.5f + raySway, rayLength * 0.8f)
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
                val yVar = sin(phase4 + i * 0.6f) * 3f
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
            val px = ((p.x + sin(phase3 + p.phase) * 0.02f + slowTime * p.speedX) % 1f + 1f) % 1f
            val py = ((p.y + cos(phase4 + p.phase) * 0.015f + slowTime * p.speedY) % 0.85f + 0.85f) % 0.85f + 0.03f
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
                "fish_blue", "fish_orange" -> {
                    // Pesci — nuoto prevalentemente orizzontale, realistico

                    // ORIZZONTALE — ampiezza grande, il pesce attraversa lo schermo
                    val driftX1 = sin(fishPhase1 + phaseOffset) * 0.32f
                    val driftX2 = sin(fishPhase2 + phaseOffset * 1.618f) * 0.20f
                    val driftX3 = cos(fishPhase3 + phaseOffset * 2.414f) * 0.12f
                    val driftX4 = sin(fishPhase4 + phaseOffset * 1.732f) * 0.08f

                    // VERTICALE — ampiezza ridotta, i pesci non salgono/scendono molto
                    val driftY1 = cos(fishPhase2 + phaseOffset * 1.414f) * 0.08f
                    val driftY2 = cos(fishPhase3 + phaseOffset * 2.236f) * 0.05f
                    val driftY3 = sin(fishPhase4 + phaseOffset * 1.902f) * 0.03f

                    val fishCenterX = 0.20f + sin(phaseOffset) * 0.25f
                    val fishCenterY = 0.45f + cos(phaseOffset * 1.3f) * 0.12f

                    val totalDriftX = driftX1 + driftX2 + driftX3 + driftX4
                    val totalDriftY = driftY1 + driftY2 + driftY3

                    val fishX = w * (fishCenterX + totalDriftX)
                    val fishY = (h * (fishCenterY + totalDriftY)).coerceIn(h * 0.20f, h * 0.80f)

                    // Direzione basata su velocità reale
                    val velocityX = cos(fishPhase1 + phaseOffset)
                    val direction = if (velocityX > 0) 1f else -1f

                    when (deco.id) {
                        "fish_blue" -> drawRealisticBlueFish(fishX, fishY, 40f, swimPhase, index, direction)
                        "fish_orange" -> drawRealisticClownfish(fishX, fishY, 40f, swimPhase, index, direction)
                    }
                }

                // TURTLE — nuoto lento, copre tutto l'acquario
                "turtle" -> {
                    // Drift orizzontale ampio — la tartaruga attraversa tutto lo schermo
                    val tDriftX1 = sin(fishPhase1 + phaseOffset) * 0.35f
                    val tDriftX2 = sin(fishPhase3 + phaseOffset * 1.618f) * 0.18f

                    // Drift verticale piu' ampio dei pesci — la tartaruga sale e scende
                    val tDriftY1 = cos(fishPhase2 + phaseOffset * 1.414f) * 0.14f
                    val tDriftY2 = sin(fishPhase4 + phaseOffset * 2.236f) * 0.08f

                    // Centro a meta' schermo cosi' copre tutta l'area
                    val tCenterX = 0.50f
                    val tCenterY = 0.48f

                    val turtleX = w * (tCenterX + tDriftX1 + tDriftX2)
                    val turtleY = (h * (tCenterY + tDriftY1 + tDriftY2)).coerceIn(h * 0.15f, h * 0.80f)

                    val velocityX = cos(fishPhase1 + phaseOffset)
                    val direction = if (velocityX > 0) 1f else -1f

                    // Usa fishPhase2 per animazione zampe — ciclo lento e fluido (~83s wrapping)
                    drawRealisticTurtle(turtleX, turtleY, 45f, fishPhase2, index, direction)
                }

                // SEAHORSE — drift verticale gentile, simile alla medusa
                "seahorse" -> {
                    // Movimento orizzontale — copre tutto lo schermo come i pesci
                    val shDriftX1 = sin(seahorsePhaseA + phaseOffset) * 0.22f
                    val shDriftX2 = sin(seahorsePhaseB + phaseOffset * 1.618f) * 0.14f
                    val shDriftX3 = cos(seahorsePhaseC + phaseOffset * 2.414f) * 0.08f

                    // Movimento verticale pronunciato (galleggia nella realtà)
                    val shDriftY1 = cos(seahorsePhaseA + phaseOffset * 1.414f) * 0.16f
                    val shDriftY2 = cos(seahorsePhaseB + phaseOffset * 2.236f) * 0.10f
                    val shDriftY3 = sin(seahorsePhaseC + phaseOffset * 1.732f) * 0.06f

                    val shCenterX = 0.45f + sin(phaseOffset) * 0.10f
                    val shCenterY = 0.45f + cos(phaseOffset * 1.3f) * 0.10f

                    val seahorseX = w * (shCenterX + shDriftX1 + shDriftX2 + shDriftX3)
                    val seahorseY = h * (shCenterY + shDriftY1 + shDriftY2 + shDriftY3)

                    drawRealisticSeahorse(
                        seahorseX, seahorseY, 42f,
                        seahorsePhaseA, seahorsePhaseB, index
                    )
                }

                // BOTTOM DECORATIONS - Static or minimal movement
                "starfish" -> {
                    val starX = w * baseX
                    val starY = sandTop + 8f + sin(phase4 + index) * 2f
                    drawStarfish(starX, starY, 18f, StarfishOrange, StarfishDark)
                }
                "coral_pink" -> {
                    // 3 coralli sovrapposti in basso a destra, immersi nella sabbia
                    val sandDepth = sandTop + 130f
                    drawCoral(w * 0.82f, sandDepth, 95f)
                    drawCoral(w * 0.85f, sandDepth, 81f)
                    drawCoral(w * 0.88f, sandDepth, 88f)
                }
                "treasure" -> {
                    drawTreasureChest(w * baseX, sandTop - 8f, 30f, phase2)
                }
                "crab" -> {
                    // Staircase: due onde atan a frequenze diverse creano
                    // tanti piccoli spostamenti con pause in mezzo
                    val k = 3f  // k basso = camminata, non scatto
                    val atanK = atan(k)

                    // Step 1 (~20s): piccoli movimenti frequenti
                    val step1 = atan(k * sin(crabPhase + phaseOffset)) / atanK
                    // Step 2 (~67s): drift lento complessivo
                    val step2 = atan(k * sin(fishPhase1 + phaseOffset * 1.414f)) / atanK

                    // Tratti brevi (0.12) + drift lento (0.18)
                    val crabX = w * (0.5f + step1 * 0.12f + step2 * 0.18f)

                    // WalkSpeed: alto quando step1 è in transizione, basso in pausa
                    val walkSpeed = ((1f - step1 * step1) * 1.5f).coerceIn(0f, 1f)

                    drawRealisticCrab(crabX, sandTop + 5f, 40f, time, walkSpeed)
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
internal fun DrawScope.drawRealisticSeaweed(
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
internal fun DrawScope.drawShell(cx: Float, cy: Float, r: Float, color: Color) {
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
internal fun DrawScope.drawRock(cx: Float, cy: Float, rw: Float, rh: Float, dark: Color, light: Color) {
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

// --- Corallo 🪸 — rosso sgargiante, macchie bianche, rami dalla sabbia ---
internal fun DrawScope.drawCoral(baseX: Float, baseY: Float, height: Float) {
    val h = height
    // Palette rosso corallo sgargiante
    val coralRed = Color(0xFFE83030)
    val coralDark = Color(0xFFB01818)
    val coralBright = Color(0xFFFF5040)
    val coralHot = Color(0xFFFF3838)

    // Accumula posizioni dei rami per le macchie bianche
    val spotPositions = mutableListOf<Offset>()

    // Disegna un singolo ramo: tubo con punta arrotondata
    fun branch(x1: Float, y1: Float, x2: Float, y2: Float, w: Float) {
        val dx = x2 - x1
        val dy = y2 - y1
        val len = kotlin.math.sqrt(dx * dx + dy * dy)
        if (len < 0.5f) return
        val nx = -dy / len * w * 0.5f
        val ny = dx / len * w * 0.5f

        val tubePath = Path().apply {
            moveTo(x1 + nx, y1 + ny)
            lineTo(x2 + nx * 0.7f, y2 + ny * 0.7f)
            quadraticTo(x2, y2 - w * 0.5f, x2 - nx * 0.7f, y2 - ny * 0.7f)
            lineTo(x1 - nx, y1 - ny)
            close()
        }
        drawPath(tubePath, brush = Brush.verticalGradient(
            colors = listOf(coralDark, coralRed, coralBright),
            startY = y1, endY = y2
        ))
        // Highlight laterale (luce)
        drawLine(
            color = Color.White.copy(alpha = 0.22f),
            start = Offset(x1 + nx * 0.5f, y1 + ny * 0.5f),
            end = Offset(x2 + nx * 0.35f, y2 + ny * 0.35f),
            strokeWidth = w * 0.18f,
            cap = StrokeCap.Round
        )
        // Punta tondeggiante
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(coralHot, coralRed),
                center = Offset(x2, y2),
                radius = w * 0.7f
            ),
            radius = w * 0.5f,
            center = Offset(x2, y2)
        )
        // Riflesso sulla punta
        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            radius = w * 0.18f,
            center = Offset(x2 - w * 0.1f, y2 - w * 0.15f)
        )

        // Salva posizioni per macchie bianche
        val midX = (x1 + x2) * 0.5f
        val midY = (y1 + y2) * 0.5f
        spotPositions.add(Offset(midX, midY))
        spotPositions.add(Offset(x2, y2))
    }

    // Ramo che si biforca a Y, ricorsivo
    fun yBranch(x: Float, y: Float, angle: Float, len: Float, w: Float, depth: Int) {
        val tipX = x + cos(angle) * len
        val tipY = y + sin(angle) * len
        branch(x, y, tipX, tipY, w)

        if (depth > 0) {
            val spread = 0.4f + depth * 0.05f
            val nextLen = len * 0.62f
            val nextW = w * 0.72f
            yBranch(tipX, tipY, angle - spread, nextLen, nextW, depth - 1)
            yBranch(tipX, tipY, angle + spread, nextLen, nextW, depth - 1)
        }
    }

    val up = -PI.toFloat() / 2f

    // Ramo centrale (piu' alto)
    yBranch(baseX, baseY, up, h * 0.42f, h * 0.10f, 2)
    // Ramo sinistro
    yBranch(baseX - h * 0.08f, baseY, up - 0.35f, h * 0.34f, h * 0.08f, 2)
    // Ramo destro
    yBranch(baseX + h * 0.08f, baseY, up + 0.35f, h * 0.34f, h * 0.08f, 2)
    // Rametto laterale sinistro
    yBranch(baseX - h * 0.13f, baseY, up - 0.7f, h * 0.22f, h * 0.06f, 1)
    // Rametto laterale destro
    yBranch(baseX + h * 0.13f, baseY, up + 0.7f, h * 0.22f, h * 0.06f, 1)

    // === MACCHIE BIANCHE sparse (come polipi / texture corallo) ===
    // Usa un pattern deterministico basato sulla posizione del corallo
    val seed = (baseX * 7f + baseY * 13f).toInt()
    spotPositions.forEachIndexed { i, pos ->
        // Alterna: una macchia si' una no, pattern vario
        if ((i + seed) % 3 == 0) {
            val r = h * 0.018f + (((i * 17 + seed) % 7) * h * 0.004f)
            val ox = ((i * 31 + seed) % 11 - 5) * h * 0.006f
            val oy = ((i * 23 + seed) % 9 - 4) * h * 0.005f
            drawCircle(
                color = Color.White.copy(alpha = 0.55f),
                radius = r,
                center = Offset(pos.x + ox, pos.y + oy)
            )
        }
    }
}

