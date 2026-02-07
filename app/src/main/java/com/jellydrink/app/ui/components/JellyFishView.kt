package com.jellydrink.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

// ═══════════════════════════════════════════════════════════════════
//  PALETTE — Cartoon premium multi-layer, Fishdom / Splash Fish
// ═══════════════════════════════════════════════════════════════════

// Outer glow shell
private val OuterGlow = Color(0xFFFFE0F0)
// Body layers (from highlight to deep shadow)
private val BodyHL1 = Color(0xFFFFF0F6)    // quasi bianco-rosa
private val BodyHL2 = Color(0xFFFFD8EA)    // rosa pallido
private val BodyMain = Color(0xFFFFACD0)   // rosa principale
private val BodyDeep = Color(0xFFEE80B0)   // rosa intenso
private val BodyShadow = Color(0xFFD06090) // ombra rosa scuro
private val BodyDark = Color(0xFFB04878)   // ombra profonda

// Rim light (controluce)
private val RimLight = Color(0xFFFFE8F8)

// Interno organico
private val OrganCenter = Color(0xFFF0C8D8)
private val OrganRing = Color(0xFFE8A0C0)
private val ChannelColor = Color(0x35D080A8)

// Macchie
private val Spot1 = Color(0x40E060A0)
private val Spot2 = Color(0x30F088B8)

// Bordo smerlato
private val ScallopHL = Color(0xFFFFD0E0)
private val ScallopShadow = Color(0xFFCC6898)

// Acqua riempimento
private val WaterSurf = Color(0xFFA8F0FF)
private val WaterMid = Color(0xFF68D8F0)
private val WaterDeep = Color(0xFF28A8D0)
private val WaterFloor = Color(0xFF1890B8)

// Occhi
private val EyeWhite = Color(0xFFFFFFFF)
private val EyeShadowTop = Color(0x28884068)
private val IrisOuter = Color(0xFF2828A0)
private val IrisMid = Color(0xFF4848D0)
private val IrisInner = Color(0xFF6868E8)
private val PupilColor = Color(0xFF080828)
private val EyeShine = Color(0xFFFFFFFF)

// Faccia
private val CheekColor = Color(0xFFFF7898)
private val MouthColor = Color(0xFFA03860)
private val MouthFill = Color(0xFF802848)

// Tentacoli — braccia orali (spesse) e filamenti (sottili)
private val ArmLight = Color(0xFFFFB8D0)
private val ArmMid = Color(0xFFE890B0)
private val ArmDark = Color(0xFFD07098)
private val FilLight = Color(0xB0FFC0D8)
private val FilDark = Color(0x80D080A8)

// Contorno
private val Outline = Color(0xFF904868)

// Effetti
private val GlowGold = Color(0xFFFFD060)
private val BubbleColor = Color(0xCCFFFFFF)

// Piccoli punti bioluminescenti interni
private val BioDot = Color(0xFFFFE8F8)

// ═══════════════════════════════════════════════════════════════════
//  SPECIES COLOR PALETTES
// ═══════════════════════════════════════════════════════════════════
data class JellyfishPalette(
    val bodyHL1: Color,
    val bodyHL2: Color,
    val bodyMain: Color,
    val bodyDeep: Color,
    val bodyShadow: Color,
    val bodyDark: Color,
    val outerGlow: Color,
    val glowGold: Color
)

private val PaletteRosa = JellyfishPalette(
    bodyHL1 = Color(0xFFFFF0F6),
    bodyHL2 = Color(0xFFFFD8EA),
    bodyMain = Color(0xFFFFACD0),
    bodyDeep = Color(0xFFEE80B0),
    bodyShadow = Color(0xFFD06090),
    bodyDark = Color(0xFFB04878),
    outerGlow = Color(0xFFFFE0F0),
    glowGold = Color(0xFFFFD060)
)

private val PaletteLunar = JellyfishPalette(
    bodyHL1 = Color(0xFFF0F8FF),
    bodyHL2 = Color(0xFFD8E8FF),
    bodyMain = Color(0xFFACCCFF),
    bodyDeep = Color(0xFF80A0EE),
    bodyShadow = Color(0xFF6080D0),
    bodyDark = Color(0xFF4860B0),
    outerGlow = Color(0xFFE0F0FF),
    glowGold = Color(0xFFC0D8FF)
)

private val PaletteAbyssal = JellyfishPalette(
    bodyHL1 = Color(0xFFE8E0F8),
    bodyHL2 = Color(0xFFC8B0E8),
    bodyMain = Color(0xFF9868D8),
    bodyDeep = Color(0xFF7040B8),
    bodyShadow = Color(0xFF502898),
    bodyDark = Color(0xFF301878),
    outerGlow = Color(0xFFD0C0F0),
    glowGold = Color(0xFF9060FF)
)

private val PaletteAurora = JellyfishPalette(
    bodyHL1 = Color(0xFFF0FFF8),
    bodyHL2 = Color(0xFFD8FFE8),
    bodyMain = Color(0xFF80FFD0),
    bodyDeep = Color(0xFF40E8A8),
    bodyShadow = Color(0xFF20C888),
    bodyDark = Color(0xFF10A868),
    outerGlow = Color(0xFFE0FFF0),
    glowGold = Color(0xFF60FFD0)
)

private val PaletteCrystal = JellyfishPalette(
    bodyHL1 = Color(0xFFFFFFFF),
    bodyHL2 = Color(0xFFF8F8FF),
    bodyMain = Color(0xFFE8E8F8),
    bodyDeep = Color(0xFFD0D0E8),
    bodyShadow = Color(0xFFB8B8D8),
    bodyDark = Color(0xFFA0A0C8),
    outerGlow = Color(0xFFF8F8FF),
    glowGold = Color(0xFFFFFFFF)
)

private val PaletteGolden = JellyfishPalette(
    bodyHL1 = Color(0xFFFFFFF0),
    bodyHL2 = Color(0xFFFFE8B0),
    bodyMain = Color(0xFFFFD060),
    bodyDeep = Color(0xFFE8A020),
    bodyShadow = Color(0xFFC88010),
    bodyDark = Color(0xFFA86008),
    outerGlow = Color(0xFFFFF0D0),
    glowGold = Color(0xFFFFE040)
)

private fun getPalette(species: String): JellyfishPalette = when (species) {
    "lunar" -> PaletteLunar
    "abyssal" -> PaletteAbyssal
    "aurora" -> PaletteAurora
    "crystal" -> PaletteCrystal
    "golden" -> PaletteGolden
    else -> PaletteRosa
}

// ═══════════════════════════════════════════════════════════════════
//  JELLYFISH CONFIGURATION
// ═══════════════════════════════════════════════════════════════════
private val scale = 0.7f
private val tentacleCount = 5
private val filamentCount = 8
private val showEyes = true
private val showMouth = true
private val glowIntensity = 1.0f
private val bioDotsCount = 14

// Dati pre-generati per punti bioluminescenti
private data class InternalDot(val angleF: Float, val radiusF: Float, val size: Float, val phase: Float)

@Composable
fun JellyFishView(
    fillPercentage: Float,
    modifier: Modifier = Modifier,
    species: String = "rosa"
) {
    val palette = getPalette(species)
    val fill by animateFloatAsState(
        targetValue = fillPercentage.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 900),
        label = "fill"
    )

    // === DRAG STATE ===
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var shockIntensity by remember { mutableStateOf(0f) }

    // Manual positioning - quando l'utente trascina la medusa
    var isManuallyPositioned by remember { mutableStateOf(false) }
    var manualPosition by remember { mutableStateOf(Offset.Zero) }

    // Store jellyfish position for hit testing
    var jellyfishCenter by remember { mutableStateOf(Offset.Zero) }
    var jellyfishSize by remember { mutableStateOf(Size.Zero) }

    val inf = rememberInfiniteTransition(label = "jf")

    // Ultra-smooth continuous movement using infinite looping animations
    // These create perfectly smooth, organic drift patterns without any jumps

    // Primary drift phase - continuous rotation (never restarts, perfectly smooth)
    val driftPhase by inf.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(40000, easing = LinearEasing), RepeatMode.Restart), label = "phase"
    )

    // Secondary wave phases for complex, natural movement patterns
    val wavePhase1 by inf.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(30000, easing = LinearEasing), RepeatMode.Restart), label = "w1"
    )

    val wavePhase2 by inf.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(50000, easing = LinearEasing), RepeatMode.Restart), label = "w2"
    )

    val wavePhase3 by inf.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(35000, easing = LinearEasing), RepeatMode.Restart), label = "w3"
    )
    // Tentacoli — ~6s
    val tentPhase by inf.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "tent"
    )
    // Breathing — ~3.5s
    val breathe by inf.animateFloat(
        initialValue = 0.97f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(3500), RepeatMode.Reverse), label = "br"
    )
    // Smerlatura — ~4.5s
    val scallopPhase by inf.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(4500, easing = LinearEasing), RepeatMode.Restart),
        label = "sc"
    )
    // Blink
    val blinkCycle by inf.animateFloat(
        initialValue = 0f, targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "blink"
    )
    val blinking = blinkCycle in 92f..97f

    // Onda acqua
    val wavePhase by inf.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Restart),
        label = "wave"
    )
    // Glow 100%
    val glow100 by inf.animateFloat(
        initialValue = 0.06f, targetValue = 0.28f,
        animationSpec = infiniteRepeatable(tween(2500), RepeatMode.Reverse), label = "glow"
    )
    // Pulsazione organo interno
    val organPulse by inf.animateFloat(
        initialValue = 0.92f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(2800), RepeatMode.Reverse), label = "organ"
    )
    // Bollicine fase
    val bubblePhase by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "bub"
    )

    // Punti bioluminescenti pre-generati (stabili tra recompositions)
    val bioDots = remember {
        List(14) { i ->
            InternalDot(
                angleF = (i * 0.45f + 0.2f) % (2f * PI.toFloat()),
                radiusF = 0.25f + (i * 0.17f) % 0.45f,
                size = 1.2f + (i % 4) * 0.6f,
                phase = i * 0.9f
            )
        }
    }

    // Bollicine pre-generate
    val bubbles = remember {
        List(6) { i ->
            Triple(
                -0.3f + i * 0.12f,            // offsetX frazionale
                0.1f + (i * 0.14f) % 0.6f,   // baseY frazionale
                1.5f + (i % 3) * 1.2f          // raggio
            )
        }
    }

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Hit test: only start dragging if touch is on the jellyfish
                        val dx = offset.x - jellyfishCenter.x
                        val dy = offset.y - jellyfishCenter.y
                        val hitRadius = jellyfishSize.width / 2f * 1.2f // Slightly larger hit area

                        // Check if touch is within jellyfish bounds (elliptical hit test)
                        val normalizedDist = (dx * dx) / (hitRadius * hitRadius) +
                                           (dy * dy) / ((jellyfishSize.height / 2f * 1.2f) * (jellyfishSize.height / 2f * 1.2f))

                        if (normalizedDist <= 1f) {
                            isDragging = true
                            dragPosition = offset
                            shockIntensity = 1f
                        }
                    },
                    onDrag = { change, _ ->
                        if (isDragging) {
                            change.consume()
                            dragPosition = change.position
                            shockIntensity = 1f
                        }
                    },
                    onDragEnd = {
                        if (isDragging) {
                            // Salva la posizione finale dove l'utente ha lasciato la medusa
                            manualPosition = dragPosition
                            isManuallyPositioned = true
                        }
                        isDragging = false
                        shockIntensity = 0f
                    },
                    onDragCancel = {
                        if (isDragging) {
                            // Salva la posizione anche se il drag viene cancellato
                            manualPosition = dragPosition
                            isManuallyPositioned = true
                        }
                        isDragging = false
                        shockIntensity = 0f
                    }
                )
            }
    ) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val complete = fill >= 1f

        // Ultra-smooth organic drift using combined sine/cosine waves
        // NO MULTIPLIERS on phases = perfectly continuous, zero jumps!

        // Horizontal drift - combine multiple sine waves for complex motion
        val driftX1 = sin(driftPhase) * 0.38f
        val driftX2 = sin(wavePhase1) * 0.22f        // NO multiplier!
        val driftX3 = cos(wavePhase2) * 0.15f        // NO multiplier!

        // Vertical drift - combine multiple cosine waves for independent Y movement
        val driftY1 = cos(driftPhase) * 0.30f        // NO multiplier!
        val driftY2 = cos(wavePhase3) * 0.18f        // NO multiplier!
        val driftY3 = sin(wavePhase1) * 0.12f        // NO multiplier!

        // Final position logic:
        // 1. Se sto trascinando -> usa dragPosition
        // 2. Se è stata posizionata manualmente -> resta lì (manualPosition)
        // 3. Altrimenti -> usa il movimento automatico (drift)
        val cx = when {
            isDragging -> dragPosition.x
            isManuallyPositioned -> manualPosition.x
            else -> w * (0.5f + driftX1 + driftX2 + driftX3)
        }
        val baseY = when {
            isDragging -> dragPosition.y
            isManuallyPositioned -> manualPosition.y
            else -> h * (0.5f + driftY1 + driftY2 + driftY3)
        }

        val ref = min(w, h)
        val bw = ref * 0.22f * breathe * scale
        val bh = bw * 1.18f
        val bodyTop = baseY - bh * 0.5f
        val bodyBot = baseY + bh * 0.5f

        // Update jellyfish position and size for hit testing
        jellyfishCenter = Offset(cx, baseY)
        jellyfishSize = Size(bw * 2f, bh)

        // ══════════════════════════════════════════
        //  GLOW DORATO 100%
        // ══════════════════════════════════════════
        if (complete) {
            val glowAlpha = glow100 * 0.15f * glowIntensity
            for (r in 4 downTo 1) {
                drawCircle(
                    color = palette.glowGold.copy(alpha = glowAlpha / r),
                    radius = bw * (1.5f + r * 0.55f),
                    center = Offset(cx, baseY)
                )
            }
        }

        // ══════════════════════════════════════════
        //  OMBRA MORBIDA SOTTO (doppio layer)
        // ══════════════════════════════════════════
        drawOval(
            color = Color.Black.copy(alpha = 0.04f),
            topLeft = Offset(cx - bw * 0.9f, bodyBot + bh * 0.05f),
            size = Size(bw * 1.8f, bh * 0.12f)
        )
        drawOval(
            color = Color.Black.copy(alpha = 0.08f),
            topLeft = Offset(cx - bw * 0.6f, bodyBot + bh * 0.03f),
            size = Size(bw * 1.2f, bh * 0.08f)
        )

        // ══════════════════════════════════════════
        //  TENTACOLI — dietro il corpo
        // ══════════════════════════════════════════
        drawOralArms(cx, bodyBot, bw, bh, tentPhase, tentacleCount)
        drawTrailingFilaments(cx, bodyBot, bw, bh, tentPhase, filamentCount)

        // ══════════════════════════════════════════
        //  OUTER GLOW SHELL — alone esterno (palette based)
        // ══════════════════════════════════════════
        val bodyPath = buildBellPath(cx, bodyTop, bodyBot, bw, scallopPhase)
        drawPath(
            path = bodyPath,
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, palette.outerGlow.copy(alpha = 0.12f * glowIntensity), Color.Transparent),
                center = Offset(cx, baseY),
                radius = bw * 1.6f
            )
        )

        // ══════════════════════════════════════════
        //  CORPO LAYER 1 — base profonda (palette based)
        // ══════════════════════════════════════════
        drawPath(
            path = bodyPath,
            brush = Brush.verticalGradient(
                colors = listOf(palette.bodyHL2, palette.bodyMain, palette.bodyDeep, palette.bodyDark),
                startY = bodyTop,
                endY = bodyBot + bh * 0.05f
            )
        )

        // ══════════════════════════════════════════
        //  CORPO LAYER 2 — gradiente radiale per volume
        // ══════════════════════════════════════════
        drawPath(
            path = bodyPath,
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.bodyHL1.copy(alpha = 0.55f),
                    palette.bodyMain.copy(alpha = 0.20f),
                    Color.Transparent
                ),
                center = Offset(cx - bw * 0.15f, bodyTop + bh * 0.3f),
                radius = bw * 1.1f
            )
        )

        // ══════════════════════════════════════════
        //  CANALI RADIALI interni (struttura organica)
        // ══════════════════════════════════════════
        clipPath(bodyPath) {
            val channelCount = 10
            for (i in 0 until channelCount) {
                val angle = (i.toFloat() / channelCount) * PI.toFloat() + PI.toFloat() * 0.05f
                val startR = bw * 0.15f * organPulse
                val endR = bw * 0.85f
                val startX = cx + cos(angle) * startR
                val startYc = baseY - bh * 0.08f + sin(angle) * startR * 0.5f
                val endX = cx + cos(angle) * endR
                val endYc = baseY - bh * 0.08f + sin(angle) * endR * 0.7f
                val ctrlSway = sin(tentPhase * 0.3f + i * 0.6f) * bw * 0.04f

                val ch = Path().apply {
                    moveTo(startX, startYc)
                    quadraticTo(
                        (startX + endX) / 2f + ctrlSway,
                        (startYc + endYc) / 2f,
                        endX, endYc
                    )
                }
                drawPath(
                    ch, ChannelColor,
                    style = Stroke(1.8f + sin(tentPhase * 0.2f + i) * 0.3f, cap = StrokeCap.Round)
                )
            }

            // Organo centrale pulsante (stomaco)
            val organR = bw * 0.18f * organPulse
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(OrganCenter, OrganRing, OrganRing.copy(alpha = 0.15f)),
                    center = Offset(cx, baseY - bh * 0.05f),
                    radius = organR * 1.3f
                ),
                topLeft = Offset(cx - organR, baseY - bh * 0.05f - organR * 0.7f),
                size = Size(organR * 2f, organR * 1.4f)
            )
            // Anello organo
            drawOval(
                color = OrganRing.copy(alpha = 0.35f),
                topLeft = Offset(cx - organR, baseY - bh * 0.05f - organR * 0.7f),
                size = Size(organR * 2f, organR * 1.4f),
                style = Stroke(1.2f)
            )

            // Punti bioluminescenti
            bioDots.take(bioDotsCount).forEach { dot ->
                val dotR = bw * dot.radiusF
                val dx = cx + cos(dot.angleF) * dotR
                val dy = baseY - bh * 0.05f + sin(dot.angleF) * dotR * 0.7f
                val alpha = (0.3f + sin(tentPhase + dot.phase) * 0.15f) * glowIntensity
                drawCircle(BioDot.copy(alpha = alpha), dot.size, Offset(dx, dy))
                drawCircle(BioDot.copy(alpha = alpha * 0.3f), dot.size * 2.5f, Offset(dx, dy))
            }
        }

        // ══════════════════════════════════════════
        //  MACCHIE ORGANICHE
        // ══════════════════════════════════════════
        clipPath(bodyPath) {
            drawCircle(Spot1, bw * 0.10f, Offset(cx + bw * 0.45f, bodyTop + bh * 0.25f))
            drawCircle(Spot2, bw * 0.07f, Offset(cx - bw * 0.50f, bodyTop + bh * 0.38f))
            drawCircle(Spot1, bw * 0.06f, Offset(cx + bw * 0.55f, bodyTop + bh * 0.50f))
            drawCircle(Spot2, bw * 0.08f, Offset(cx - bw * 0.25f, bodyTop + bh * 0.20f))
            drawCircle(Spot1, bw * 0.05f, Offset(cx + bw * 0.20f, bodyTop + bh * 0.60f))
            drawCircle(Spot2, bw * 0.04f, Offset(cx - bw * 0.60f, bodyTop + bh * 0.55f))
        }

        // ══════════════════════════════════════════
        //  RIEMPIMENTO ACQUA — proporzionale esatto
        // ══════════════════════════════════════════
        if (fill > 0f) {
            val waterY = bodyBot - (bodyBot - bodyTop) * fill

            clipPath(bodyPath) {
                val waveAmp = if (fill < 1f) bw * 0.035f else bw * 0.012f
                val left = cx - bw * 1.5f
                val right = cx + bw * 1.5f
                val totalW = right - left
                val segs = 28

                val wavePath = Path().apply {
                    moveTo(left, waterY)
                    for (i in 0 until segs) {
                        val segW = totalW / segs
                        val xMid = left + i * segW + segW / 2f
                        val xEnd = left + (i + 1) * segW
                        val yOff = sin(wavePhase + i * 0.52f) * waveAmp
                        quadraticTo(xMid, waterY + yOff, xEnd, waterY)
                    }
                    lineTo(right, bodyBot + 30f)
                    lineTo(left, bodyBot + 30f)
                    close()
                }

                drawPath(
                    path = wavePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(WaterSurf, WaterMid, WaterDeep, WaterFloor),
                        startY = waterY - bw * 0.03f,
                        endY = bodyBot
                    )
                )

                // Riflesso superficie acqua
                drawLine(
                    color = Color.White.copy(alpha = 0.30f),
                    start = Offset(cx - bw * 0.55f, waterY + waveAmp * 0.3f),
                    end = Offset(cx + bw * 0.35f, waterY - waveAmp * 0.2f),
                    strokeWidth = 1.8f, cap = StrokeCap.Round
                )
                // Secondo riflesso spezzato
                drawLine(
                    color = Color.White.copy(alpha = 0.15f),
                    start = Offset(cx + bw * 0.4f, waterY + waveAmp * 0.5f),
                    end = Offset(cx + bw * 0.65f, waterY),
                    strokeWidth = 1.2f, cap = StrokeCap.Round
                )
            }
        }

        // ══════════════════════════════════════════
        //  HIGHLIGHT SPECULARE principale (grande)
        // ══════════════════════════════════════════
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(cx - bw * 0.18f, bodyTop + bh * 0.13f),
                radius = bw * 0.55f
            ),
            topLeft = Offset(cx - bw * 0.58f, bodyTop + bh * 0.01f),
            size = Size(bw * 0.78f, bh * 0.28f)
        )
        // Highlight secondario
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.28f), Color.Transparent),
                center = Offset(cx + bw * 0.42f, bodyTop + bh * 0.22f),
                radius = bw * 0.18f
            ),
            topLeft = Offset(cx + bw * 0.30f, bodyTop + bh * 0.16f),
            size = Size(bw * 0.25f, bh * 0.14f)
        )

        // ══════════════════════════════════════════
        //  RIM LIGHT — controluce sul bordo destro
        // ══════════════════════════════════════════
        clipPath(bodyPath) {
            drawOval(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, RimLight.copy(alpha = 0.30f)),
                    startX = cx + bw * 0.3f,
                    endX = cx + bw * 1.2f
                ),
                topLeft = Offset(cx + bw * 0.4f, bodyTop + bh * 0.05f),
                size = Size(bw * 0.8f, bh * 0.9f)
            )
        }

        // ══════════════════════════════════════════
        //  ⚡ ELECTRIC SHOCK EFFECT - X-RAY STYLE ⚡
        // ══════════════════════════════════════════
        if (isDragging && shockIntensity > 0f) {
            val shockColor = Color(0xFFFFD700) // Giallo dorato
            val shockYellow = Color(0xFFFFFF00) // Giallo elettrico

            // ESPLOSIONE A STELLA PIÙ PICCOLA E CONTENUTA
            val burstPath = Path().apply {
                val spikes = 12
                val innerRadius = bw * 0.8f
                val outerRadius = bw * 1.3f

                for (i in 0..spikes) {
                    val angle = (i.toFloat() / spikes) * 2f * PI.toFloat()
                    val nextAngle = ((i + 1).toFloat() / spikes) * 2f * PI.toFloat()

                    val outerX = cx + cos(angle) * outerRadius
                    val outerY = baseY + sin(angle) * outerRadius

                    val innerAngle = (angle + nextAngle) / 2f
                    val innerX = cx + cos(innerAngle) * innerRadius
                    val innerY = baseY + sin(innerAngle) * innerRadius

                    if (i == 0) {
                        moveTo(outerX, outerY)
                    } else {
                        lineTo(outerX, outerY)
                    }
                    lineTo(innerX, innerY)
                }
                close()
            }

            // Disegna l'esplosione gialla
            drawPath(burstPath, shockColor)

            // FULMINI PIÙ CORTI (4 direzioni)
            val lightningCount = 4
            for (i in 0 until lightningCount) {
                val angle = (i.toFloat() / lightningCount) * 2f * PI.toFloat()
                val boltLength = bw * 1.5f

                val boltPath = Path().apply {
                    moveTo(cx, baseY)

                    val segments = 3
                    var currentX = cx
                    var currentY = baseY

                    for (s in 1..segments) {
                        val progress = s.toFloat() / segments
                        val targetX = cx + cos(angle) * boltLength * progress
                        val targetY = baseY + sin(angle) * boltLength * progress

                        val zigzagAngle = angle + PI.toFloat() / 2f
                        val zigzagDist = bw * 0.2f * (if (s % 2 == 0) 1f else -1f)

                        val midX = (currentX + targetX) / 2f + cos(zigzagAngle) * zigzagDist
                        val midY = (currentY + targetY) / 2f + sin(zigzagAngle) * zigzagDist

                        lineTo(midX, midY)
                        lineTo(targetX, targetY)

                        currentX = targetX
                        currentY = targetY
                    }
                }

                drawPath(
                    boltPath,
                    shockYellow,
                    style = Stroke(6f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
                )
            }

            // ★ EFFETTO RADIOGRAFIA/X-RAY ★
            // Medusa diventa una silhouette scura (come scheletro nella foto)

            // Sfondo giallo brillante
            drawPath(
                path = bodyPath,
                color = shockYellow.copy(alpha = 0.9f)
            )

            // Silhouette scura della struttura interna (X-RAY)
            clipPath(bodyPath) {
                // Organo centrale più scuro (stomaco)
                val organR = bw * 0.18f
                drawOval(
                    color = Color(0xFF1A1A1A).copy(alpha = 0.8f),
                    topLeft = Offset(cx - organR, baseY - bh * 0.05f - organR * 0.7f),
                    size = Size(organR * 2f, organR * 1.4f)
                )

                // Canali radiali scuri (struttura interna)
                val channelCount = 8
                for (i in 0 until channelCount) {
                    val angle = (i.toFloat() / channelCount) * PI.toFloat()
                    val startR = bw * 0.15f
                    val endR = bw * 0.75f
                    val startX = cx + cos(angle) * startR
                    val startYc = baseY - bh * 0.05f + sin(angle) * startR * 0.5f
                    val endX = cx + cos(angle) * endR
                    val endYc = baseY - bh * 0.05f + sin(angle) * endR * 0.7f

                    val ch = Path().apply {
                        moveTo(startX, startYc)
                        lineTo(endX, endYc)
                    }
                    drawPath(
                        ch,
                        Color(0xFF2A2A2A).copy(alpha = 0.7f),
                        style = Stroke(2f, cap = StrokeCap.Round)
                    )
                }

                // Tentacoli come linee scure
                for (i in 0 until tentacleCount) {
                    val t = i.toFloat() / (tentacleCount - 1)
                    val spread = bw * 1.4f
                    val startX = cx - spread / 2f + spread * t

                    drawLine(
                        color = Color(0xFF1A1A1A).copy(alpha = 0.6f),
                        start = Offset(startX, bodyBot),
                        end = Offset(startX, bodyBot + bh * 0.8f),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Contorno scuro forte per contrasto X-ray
            drawPath(
                bodyPath,
                Color(0xFF000000).copy(alpha = 0.5f),
                style = Stroke(3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // ══════════════════════════════════════════
        //  CONTORNO — doppio layer per profondità
        // ══════════════════════════════════════════
        drawPath(bodyPath, Outline.copy(alpha = 0.15f),
            style = Stroke(4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(bodyPath, Outline.copy(alpha = 0.45f),
            style = Stroke(2.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Highlight bordo smerlato inferiore
        val scallopHL = buildScallopHighlight(cx, bodyBot, bw, scallopPhase)
        drawPath(scallopHL, ScallopHL.copy(alpha = 0.35f),
            style = Stroke(1.5f, cap = StrokeCap.Round))

        // ══════════════════════════════════════════
        //  GUANCE — blush sfumato
        // ══════════════════════════════════════════
        val cheekY = bodyTop + bh * 0.60f
        val cheekR = bw * 0.14f
        for (side in listOf(-1f, 1f)) {
            val cheekX = cx + side * bw * 0.52f
            drawOval(
                brush = Brush.radialGradient(
                    listOf(CheekColor.copy(alpha = 0.50f), CheekColor.copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(cheekX, cheekY)
                ),
                topLeft = Offset(cheekX - cheekR * 1.1f, cheekY - cheekR * 0.75f),
                size = Size(cheekR * 2.2f, cheekR * 1.5f)
            )
        }

        // ══════════════════════════════════════════
        //  OCCHI — multi-layer ricchi
        // ══════════════════════════════════════════
        val eyeY = bodyTop + bh * 0.42f
        val eyeGap = bw * 0.30f
        val eyeR = bw * 0.17f
        drawRichEye(cx - eyeGap, eyeY, eyeR, blinking)
        drawRichEye(cx + eyeGap, eyeY, eyeR, blinking)

        // ══════════════════════════════════════════
        //  BOCCA — espressiva
        // ══════════════════════════════════════════
        drawRichMouth(cx, bodyTop + bh * 0.70f, bw, fill, complete)

        // ══════════════════════════════════════════
        //  BOLLICINE che escono dalla medusa
        // ══════════════════════════════════════════
        bubbles.forEach { (offX, baseYF, radius) ->
            val bPhase = (bubblePhase + baseYF) % 1f
            val bx = cx + offX * bw + sin(tentPhase + offX * 5f) * bw * 0.04f
            val by = bodyTop - bPhase * bh * 1.8f
            val alpha = (1f - bPhase).coerceIn(0f, 1f) * 0.40f

            if (alpha > 0.02f) {
                drawCircle(BubbleColor.copy(alpha = alpha * 0.25f), radius * 2f, Offset(bx, by))
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color.Transparent, BubbleColor.copy(alpha = alpha * 0.5f), BubbleColor.copy(alpha = alpha * 0.15f)),
                        center = Offset(bx, by), radius = radius
                    ),
                    radius = radius, center = Offset(bx, by)
                )
                drawCircle(BubbleColor.copy(alpha = alpha * 0.6f), radius, Offset(bx, by), style = Stroke(0.7f))
                // Riflesso bolla
                drawCircle(BubbleColor.copy(alpha = alpha * 0.9f), radius * 0.25f,
                    Offset(bx - radius * 0.3f, by - radius * 0.3f))
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  PATH CAMPANA con bordo smerlato organico
// ═══════════════════════════════════════════════════════════════════
private fun buildBellPath(
    cx: Float, top: Float, bot: Float, hw: Float, scallopPhase: Float
): Path {
    val h = bot - top
    return Path().apply {
        val scallops = 12
        val sw = hw * 2f / scallops
        moveTo(cx - hw, bot)
        for (i in 0 until scallops) {
            val x1 = cx - hw + i * sw
            val x2 = x1 + sw
            val amp = sw * 0.30f + sin(scallopPhase + i * 0.75f) * sw * 0.07f
            cubicTo(x1 + sw * 0.25f, bot + amp, x2 - sw * 0.25f, bot + amp, x2, bot)
        }
        // Lato destro — pieno e curvo
        cubicTo(cx + hw * 1.16f, bot - h * 0.04f, cx + hw * 1.10f, top + h * 0.20f, cx + hw * 0.28f, top)
        // Cima
        cubicTo(cx + hw * 0.07f, top - h * 0.06f, cx - hw * 0.07f, top - h * 0.06f, cx - hw * 0.28f, top)
        // Lato sinistro
        cubicTo(cx - hw * 1.10f, top + h * 0.20f, cx - hw * 1.16f, bot - h * 0.04f, cx - hw, bot)
        close()
    }
}

// Path highlight solo per il bordo smerlato
private fun buildScallopHighlight(cx: Float, bot: Float, hw: Float, scallopPhase: Float): Path {
    val scallops = 12
    val sw = hw * 2f / scallops
    return Path().apply {
        moveTo(cx - hw, bot)
        for (i in 0 until scallops) {
            val x1 = cx - hw + i * sw
            val x2 = x1 + sw
            val amp = sw * 0.20f + sin(scallopPhase + i * 0.75f) * sw * 0.05f
            cubicTo(x1 + sw * 0.25f, bot + amp, x2 - sw * 0.25f, bot + amp, x2, bot)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  OCCHIO RICCO — ombra palpebrale, iride multi-gradiente, riflessi
// ═══════════════════════════════════════════════════════════════════
private fun DrawScope.drawRichEye(cx: Float, cy: Float, r: Float, blink: Boolean) {
    val eyeH = if (blink) r * 0.08f else r * 1.25f

    // Ombra palpebrale sopra
    if (!blink) {
        drawOval(
            brush = Brush.verticalGradient(
                listOf(EyeShadowTop, Color.Transparent),
                startY = cy - eyeH - r * 0.1f,
                endY = cy - eyeH * 0.3f
            ),
            topLeft = Offset(cx - r * 1.1f, cy - eyeH - r * 0.1f),
            size = Size(r * 2.2f, eyeH * 0.8f)
        )
    }

    // Ombra dietro occhio
    drawOval(Color.Black.copy(alpha = 0.06f),
        topLeft = Offset(cx - r + 1.5f, cy - eyeH + 2f),
        size = Size(r * 2f, eyeH * 2f))

    // Sclera bianca
    drawOval(EyeWhite,
        topLeft = Offset(cx - r, cy - eyeH),
        size = Size(r * 2f, eyeH * 2f))

    if (!blink) {
        val irisCy = cy + r * 0.05f
        val irisR = r * 0.62f

        // Iride — 3 layer di gradiente per profondità
        drawCircle(
            brush = Brush.radialGradient(
                listOf(IrisInner, IrisMid, IrisOuter, Color(0xFF181870)),
                center = Offset(cx, irisCy), radius = irisR
            ),
            radius = irisR, center = Offset(cx, irisCy)
        )
        // Anello esterno iride
        drawCircle(IrisOuter.copy(alpha = 0.4f), irisR, Offset(cx, irisCy), style = Stroke(1.2f))

        // Pupilla con leggero gradiente
        drawCircle(
            brush = Brush.radialGradient(
                listOf(PupilColor, Color(0xFF181848)),
                center = Offset(cx, irisCy), radius = irisR * 0.5f
            ),
            radius = irisR * 0.48f, center = Offset(cx, irisCy)
        )

        // Riflesso grande
        drawCircle(EyeShine.copy(alpha = 0.93f), r * 0.23f,
            Offset(cx + r * 0.16f, cy - r * 0.24f))
        // Riflesso medio
        drawCircle(EyeShine.copy(alpha = 0.50f), r * 0.11f,
            Offset(cx - r * 0.22f, cy + r * 0.14f))
        // Micro riflesso
        drawCircle(EyeShine.copy(alpha = 0.35f), r * 0.06f,
            Offset(cx + r * 0.30f, cy + r * 0.04f))
    }

    // Contorno
    drawOval(Outline.copy(alpha = 0.40f),
        topLeft = Offset(cx - r, cy - eyeH),
        size = Size(r * 2f, eyeH * 2f),
        style = Stroke(1.8f))
}

// ═══════════════════════════════════════════════════════════════════
//  BOCCA ESPRESSIVA
// ═══════════════════════════════════════════════════════════════════
private fun DrawScope.drawRichMouth(cx: Float, my: Float, bw: Float, fillPct: Float, complete: Boolean) {
    val mw = bw * 0.22f
    when {
        complete -> {
            // Sorriso grande D con lingua e ombra
            val p = Path().apply {
                moveTo(cx - mw, my)
                cubicTo(cx - mw * 0.3f, my + mw * 1.1f, cx + mw * 0.3f, my + mw * 1.1f, cx + mw, my)
                cubicTo(cx + mw * 0.3f, my + mw * 0.3f, cx - mw * 0.3f, my + mw * 0.3f, cx - mw, my)
                close()
            }
            drawPath(p, MouthFill)
            // Lingua
            drawOval(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFFF7898), Color(0xFFE05878)),
                    center = Offset(cx, my + mw * 0.5f)
                ),
                topLeft = Offset(cx - mw * 0.3f, my + mw * 0.30f),
                size = Size(mw * 0.6f, mw * 0.45f)
            )
            drawPath(p, Outline.copy(alpha = 0.5f), style = Stroke(2.2f, cap = StrokeCap.Round))
        }
        fillPct >= 0.5f -> {
            val curve = 0.5f + (fillPct - 0.5f)  // 0.5 → 1.0
            val p = Path().apply {
                moveTo(cx - mw * 0.85f, my)
                cubicTo(cx - mw * 0.3f, my + mw * curve * 0.8f,
                    cx + mw * 0.3f, my + mw * curve * 0.8f, cx + mw * 0.85f, my)
            }
            drawPath(p, MouthColor, style = Stroke(2.5f, cap = StrokeCap.Round))
        }
        fillPct >= 0.2f -> {
            val curve = fillPct - 0.2f  // 0 → 0.3
            val p = Path().apply {
                moveTo(cx - mw * 0.55f, my)
                cubicTo(cx - mw * 0.15f, my + mw * (0.15f + curve),
                    cx + mw * 0.15f, my + mw * (0.15f + curve), cx + mw * 0.55f, my)
            }
            drawPath(p, MouthColor, style = Stroke(2.2f, cap = StrokeCap.Round))
        }
        fillPct > 0.05f -> {
            drawLine(MouthColor, Offset(cx - mw * 0.35f, my), Offset(cx + mw * 0.35f, my),
                strokeWidth = 2.2f, cap = StrokeCap.Round)
        }
        else -> {
            val p = Path().apply {
                moveTo(cx - mw * 0.45f, my + mw * 0.15f)
                cubicTo(cx - mw * 0.12f, my - mw * 0.18f,
                    cx + mw * 0.12f, my - mw * 0.18f, cx + mw * 0.45f, my + mw * 0.15f)
            }
            drawPath(p, MouthColor, style = Stroke(2.2f, cap = StrokeCap.Round))
        }
    }
}

// ═══════════════════════════════════════════════════════════════════
//  BRACCIA ORALI — tentacoli spessi, organici, con volume
// ═══════════════════════════════════════════════════════════════════
private fun DrawScope.drawOralArms(
    cx: Float, bot: Float, bw: Float, bh: Float, phase: Float, count: Int = 5
) {
    val spread = bw * 1.4f
    val colors = listOf(ArmLight, ArmMid, ArmDark, ArmMid, ArmLight)

    for (i in 0 until count) {
        val t = i.toFloat() / (count - 1)
        val startX = cx - spread / 2f + spread * t
        val len = bh * 1.2f + (i % 3) * bh * 0.15f
        val thick = 6f - abs(i - 2) * 0.8f  // più spessi al centro

        // Ombra
        val sh = buildArmPath(startX + 2f, bot + 2f, phase, i, len)
        drawPath(sh, Color.Black.copy(alpha = 0.04f), style = Stroke(thick + 3f, cap = StrokeCap.Round))

        // Braccio principale
        val path = buildArmPath(startX, bot, phase, i, len)
        drawPath(path, colors[i].copy(alpha = 0.80f), style = Stroke(thick, cap = StrokeCap.Round))

        // Highlight laterale
        val hl = buildArmPath(startX - 0.6f, bot - 0.6f, phase, i, len)
        drawPath(hl, Color.White.copy(alpha = 0.18f), style = Stroke(thick * 0.25f, cap = StrokeCap.Round))

        // Outline leggero
        drawPath(path, Outline.copy(alpha = 0.08f), style = Stroke(thick + 1f, cap = StrokeCap.Round))
    }
}

private fun buildArmPath(sx: Float, sy: Float, phase: Float, idx: Int, len: Float): Path {
    val path = Path()
    path.moveTo(sx, sy)
    val segs = 16
    val segL = len / segs
    var px = sx; var py = sy
    for (s in 1..segs) {
        val st = s.toFloat() / segs
        val amp = (5f + 8f * st * st)
        val nx = sx + sin(phase * 0.9f + idx * 0.8f + st * 3.5f) * amp
        val ny = sy + s * segL
        path.quadraticTo((px + nx) / 2f, (py + ny) / 2f, nx, ny)
        px = nx; py = ny
    }
    return path
}

// ═══════════════════════════════════════════════════════════════════
//  FILAMENTI SOTTILI — trailing tentacles lunghi e leggeri
// ═══════════════════════════════════════════════════════════════════
private fun DrawScope.drawTrailingFilaments(
    cx: Float, bot: Float, bw: Float, bh: Float, phase: Float, count: Int = 8
) {
    val spread = bw * 2f

    for (i in 0 until count) {
        val t = i.toFloat() / (count - 1)
        val startX = cx - spread / 2f + spread * t
        val len = bh * 2.0f + (i % 4) * bh * 0.20f
        val thick = 1.5f + (i % 2) * 0.4f
        val alpha = 0.25f + (i % 3) * 0.08f
        val color = if (i % 2 == 0) FilLight else FilDark

        val path = buildFilamentPath(startX, bot + bh * 0.08f, phase, i, len)
        drawPath(path, color.copy(alpha = alpha), style = Stroke(thick, cap = StrokeCap.Round))
    }
}

private fun buildFilamentPath(sx: Float, sy: Float, phase: Float, idx: Int, len: Float): Path {
    val path = Path()
    path.moveTo(sx, sy)
    val segs = 30
    val segL = len / segs
    var px = sx; var py = sy
    for (s in 1..segs) {
        val st = s.toFloat() / segs
        val amp = (2f + 14f * st * st)
        val nx = sx + sin(phase * 0.7f + idx * 0.55f + st * 5f) * amp
        val ny = sy + s * segL
        path.quadraticTo((px + nx) / 2f, (py + ny) / 2f, nx, ny)
        px = nx; py = ny
    }
    return path
}
