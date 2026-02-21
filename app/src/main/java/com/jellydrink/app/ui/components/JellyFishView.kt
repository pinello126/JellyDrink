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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun JellyFishView(
    fillPercentage: Float,
    modifier: Modifier = Modifier
) {
    val palette = PaletteRosa
    val fill by animateFloatAsState(
        targetValue = fillPercentage.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 900),
        label = "fill"
    )

    // === DRAG STATE ===
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    // Posizione dove è stata rilasciata la medusa
    var releasePosition by remember { mutableStateOf<Offset?>(null) }
    // Drift al momento del rilascio (per compensare e evitare scatti)
    var releaseDrift by remember { mutableStateOf(Offset.Zero) }

    // Store jellyfish position for hit testing
    var jellyfishCenter by remember { mutableStateOf(Offset.Zero) }
    var jellyfishSize by remember { mutableStateOf(Size.Zero) }

    val inf = rememberInfiniteTransition(label = "jf")

    // Smooth phase animations (0→2π, LinearEasing, Restart)
    val driftPhase by inf.smoothPhase(40000, "phase")
    val wavePhase1 by inf.smoothPhase(30000, "w1")
    val wavePhase2 by inf.smoothPhase(50000, "w2")
    val wavePhase3 by inf.smoothPhase(35000, "w3")

    // Tentacle phases (prime periods for organic movement)
    val tentPhaseA by inf.smoothPhase(7919, "tentA")
    val tentPhaseB by inf.smoothPhase(11003, "tentB")
    val tentPhaseC by inf.smoothPhase(13999, "tentC")

    // Breathing — ~3.5s
    val breathe by inf.animateFloat(
        initialValue = 0.97f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(3500), RepeatMode.Reverse), label = "br"
    )
    val scallopPhase by inf.smoothPhase(4500, "sc")

    // Blink
    val blinkCycle by inf.animateFloat(
        initialValue = 0f, targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "blink"
    )
    val blinking = blinkCycle in 92f..97f

    val wavePhase by inf.smoothPhase(2200, "wave")

    // Bollicine fase
    val bubblePhase by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "bub"
    )

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
                            // Reset drift compensation per il prossimo rilascio
                            releaseDrift = Offset.Zero
                        }
                    },
                    onDrag = { change, _ ->
                        if (isDragging) {
                            change.consume()
                            dragPosition = change.position
                        }
                    },
                    onDragEnd = {
                        // Salva la posizione di rilascio (il drift verrà compensato nel Canvas)
                        releasePosition = dragPosition
                        isDragging = false
                    },
                    onDragCancel = {
                        // Salva la posizione di rilascio (il drift verrà compensato nel Canvas)
                        releasePosition = dragPosition
                        isDragging = false
                    }
                )
            }
    ) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val complete = fill >= 1f

        // Ultra-smooth organic drift using combined sine/cosine waves

        // Horizontal drift
        val driftX1 = sin(driftPhase) * 0.35f
        val driftX2 = sin(wavePhase1) * 0.19f
        val driftX3 = cos(wavePhase2) * 0.12f

        // Vertical drift
        val driftY1 = cos(driftPhase) * 0.27f
        val driftY2 = cos(wavePhase3) * 0.15f
        val driftY3 = sin(wavePhase1) * 0.09f

        // Drift totale attuale
        val currentDriftX = w * (driftX1 + driftX2 + driftX3)
        val currentDriftY = h * (driftY1 + driftY2 + driftY3)

        // COMPENSAZIONE DRIFT: quando rilascio, salva il drift corrente per evitare scatti
        if (!isDragging && releasePosition != null && releaseDrift == Offset.Zero) {
            releaseDrift = Offset(currentDriftX, currentDriftY)
        }

        val ref = min(w, h)
        val bw = ref * 0.22f * breathe * scale
        val bh = bw * 1.18f

        // Position logic con compensazione smooth del drift
        val cx = if (isDragging) {
            dragPosition.x
        } else if (releasePosition != null) {
            releasePosition!!.x - releaseDrift.x + currentDriftX
        } else {
            w * (0.5f + driftX1 + driftX2 + driftX3)
        }

        val baseY = if (isDragging) {
            dragPosition.y
        } else if (releasePosition != null) {
            releasePosition!!.y - releaseDrift.y + currentDriftY
        } else {
            h * (0.5f + driftY1 + driftY2 + driftY3)
        }
        val bodyTop = baseY - bh * 0.5f
        val bodyBot = baseY + bh * 0.5f

        // Update jellyfish position and size for hit testing
        jellyfishCenter = Offset(cx, baseY)
        jellyfishSize = Size(bw * 2f, bh)

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
        drawOralArms(cx, bodyBot, bw, bh, tentPhaseA, tentPhaseB, tentPhaseC, tentacleCount)
        drawTrailingFilaments(cx, bodyBot, bw, bh, tentPhaseA, tentPhaseB, tentPhaseC, filamentCount)

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
            val bx = cx + offX * bw + sin(tentPhaseA + offX * 5f) * bw * 0.04f
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

        // Nessun effetto speciale quando si trascina - solo movimento naturale
    }
}

