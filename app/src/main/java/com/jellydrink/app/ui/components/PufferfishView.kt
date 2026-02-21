package com.jellydrink.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ─── Palette colori ───────────────────────────────────────────────────────────
private val PuffNormal1  = Color(0xFFF5C842)  // giallo ambrato chiaro
private val PuffNormal2  = Color(0xFFD4A017)  // giallo ambrato scuro
private val PuffDrunk1   = Color(0xFFE8841A)  // arancione
private val PuffDrunk2   = Color(0xFFC05A10)  // arancione scuro
private val PuffVDrunk1  = Color(0xFFE85A1A)  // arancione-rosso
private val PuffVDrunk2  = Color(0xFFA02810)  // rosso scuro
private val BeerAmber1   = Color(0xFFF5A623)  // birra chiara
private val BeerAmber2   = Color(0xFFD4780A)  // birra scura
private val BeerFoam     = Color(0xFFFFF8DC)  // schiuma
private val SpineColor   = Color(0xFF8B6914)  // colore spine
private val OutlineColor = Color(0xFF5C4010)

@Composable
fun PufferfishView(
    fillPercentage: Float,
    isDrunk: Boolean,
    isVeryDrunk: Boolean,
    modifier: Modifier = Modifier
) {
    val fill by animateFloatAsState(
        targetValue = fillPercentage.coerceIn(0f, 1f),
        animationSpec = tween(900),
        label = "puff_fill"
    )

    val inf = rememberInfiniteTransition(label = "pf")

    // Drift base (più lento e galleggiante rispetto alla medusa)
    val driftPhase  by inf.smoothPhase(50000, "pf_drift")
    val wavePhase1  by inf.smoothPhase(37000, "pf_w1")
    val wavePhase2  by inf.smoothPhase(61000, "pf_w2")

    // Respiro — più marcato quando ubriaco
    val breatheTarget = if (isVeryDrunk) 1.10f else if (isDrunk) 1.06f else 1.03f
    val breathe by inf.animateFloat(
        initialValue = 0.97f,
        targetValue = breatheTarget,
        animationSpec = infiniteRepeatable(
            tween(if (isDrunk) 1800 else 3000),
            RepeatMode.Reverse
        ),
        label = "pf_br"
    )

    // Wobble ubriachezza — oscillazione irregolare
    val wobble by inf.smoothPhase(if (isVeryDrunk) 1400 else 2200, "pf_wob")

    // Blink
    val blinkCycle by inf.animateFloat(
        initialValue = 0f, targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(6000), RepeatMode.Restart),
        label = "pf_blink"
    )
    val blinking = blinkCycle in 93f..97f

    // Fase onde birra
    val beerWave by inf.smoothPhase(1800, "pf_beerwave")

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        // ── Drift posizione ─────────────────────────────────────────────────
        val driftX = sin(driftPhase) * 0.22f + sin(wavePhase1) * 0.11f
        val driftY = cos(driftPhase) * 0.16f + cos(wavePhase2) * 0.09f

        // Wobble ubriachezza extra
        val drunkWobX = if (isDrunk) sin(wobble) * (if (isVeryDrunk) 0.06f else 0.03f) else 0f
        val drunkWobY = if (isDrunk) cos(wobble * 1.3f) * (if (isVeryDrunk) 0.04f else 0.02f) else 0f

        val cx = w * (0.5f + driftX + drunkWobX)
        val cy = h * (0.48f + driftY + drunkWobY)

        // ── Dimensioni corpo ─────────────────────────────────────────────────
        // Il corpo si gonfia progressivamente con il fill
        val baseR = (w.coerceAtMost(h)) * 0.20f
        val puffFactor = 1f + fill * 0.35f   // da 1x a 1.35x
        val bodyR = baseR * puffFactor * breathe

        // Colore corpo in base allo stato
        val bodyColor1 = when {
            isVeryDrunk -> PuffVDrunk1
            isDrunk     -> PuffDrunk1
            else        -> PuffNormal1
        }
        val bodyColor2 = when {
            isVeryDrunk -> PuffVDrunk2
            isDrunk     -> PuffDrunk2
            else        -> PuffNormal2
        }

        // ── Ombra ───────────────────────────────────────────────────────────
        drawOval(
            color = Color.Black.copy(alpha = 0.10f),
            topLeft = Offset(cx - bodyR * 0.9f, cy + bodyR * 0.85f),
            size = Size(bodyR * 1.8f, bodyR * 0.25f)
        )

        // ── Spine ───────────────────────────────────────────────────────────
        drawSpines(cx, cy, bodyR, fill)

        // ── Corpo principale ─────────────────────────────────────────────────
        val bodyPath = Path().apply {
            addOval(
                androidx.compose.ui.geometry.Rect(
                    center = Offset(cx, cy),
                    radius = bodyR
                )
            )
        }

        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(bodyColor1, bodyColor2, bodyColor2.copy(alpha = 0.8f)),
                center = Offset(cx - bodyR * 0.2f, cy - bodyR * 0.2f),
                radius = bodyR * 1.4f
            ),
            topLeft = Offset(cx - bodyR, cy - bodyR),
            size = Size(bodyR * 2f, bodyR * 2f)
        )

        // ── Riempimento birra ─────────────────────────────────────────────────
        if (fill > 0f) {
            val beerTop = cy + bodyR - (bodyR * 2f) * fill
            clipPath(bodyPath) {
                // Onda birra
                val waveAmp = bodyR * 0.03f
                val left = cx - bodyR * 1.5f
                val right = cx + bodyR * 1.5f
                val totalW = right - left
                val segs = 20
                val wavePath = Path().apply {
                    moveTo(left, beerTop)
                    for (i in 0 until segs) {
                        val segW = totalW / segs
                        val xMid = left + i * segW + segW / 2f
                        val xEnd = left + (i + 1) * segW
                        val yOff = sin(beerWave + i * 0.6f) * waveAmp
                        quadraticTo(xMid, beerTop + yOff, xEnd, beerTop)
                    }
                    lineTo(right, cy + bodyR + 20f)
                    lineTo(left, cy + bodyR + 20f)
                    close()
                }
                drawPath(
                    path = wavePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(BeerAmber1, BeerAmber2, BeerAmber2.copy(alpha = 0.9f)),
                        startY = beerTop,
                        endY = cy + bodyR
                    )
                )
                // Schiuma (solo se c'è abbastanza birra)
                if (fill > 0.08f) {
                    drawFoam(cx, beerTop, bodyR, fill)
                }
                // Bollicine birra
                drawBeerBubbles(cx, beerTop, cy + bodyR, bodyR, beerWave)
            }
        }

        // ── Contorno corpo ───────────────────────────────────────────────────
        drawOval(
            color = OutlineColor.copy(alpha = 0.55f),
            topLeft = Offset(cx - bodyR, cy - bodyR),
            size = Size(bodyR * 2f, bodyR * 2f),
            style = Stroke(2.5f)
        )

        // ── Highlight speculare ──────────────────────────────────────────────
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.50f), Color.Transparent),
                center = Offset(cx - bodyR * 0.25f, cy - bodyR * 0.30f),
                radius = bodyR * 0.45f
            ),
            topLeft = Offset(cx - bodyR * 0.60f, cy - bodyR * 0.58f),
            size = Size(bodyR * 0.80f, bodyR * 0.50f)
        )

        // ── Occhi ───────────────────────────────────────────────────────────
        val eyeY = cy - bodyR * 0.18f
        val eyeGap = bodyR * 0.38f
        val eyeR = bodyR * 0.20f
        if (isVeryDrunk) {
            drawDrunkEye(cx - eyeGap, eyeY, eyeR, mirrored = false)
            drawDrunkEye(cx + eyeGap, eyeY, eyeR, mirrored = true)
        } else if (isDrunk) {
            drawTiltedEye(cx - eyeGap, eyeY, eyeR, tiltDeg = -15f)
            drawTiltedEye(cx + eyeGap, eyeY, eyeR, tiltDeg = 15f)
        } else {
            drawNormalEye(cx - eyeGap, eyeY, eyeR, blinking)
            drawNormalEye(cx + eyeGap, eyeY, eyeR, blinking)
        }

        // ── Bocca ───────────────────────────────────────────────────────────
        drawPuffMouth(cx, cy + bodyR * 0.42f, bodyR, isDrunk)

        // ── Pinna dorsale ────────────────────────────────────────────────────
        drawDorsalFin(cx, cy - bodyR, bodyR)

        // ── Pinna caudale ────────────────────────────────────────────────────
        drawTailFin(cx + bodyR * 0.9f, cy, bodyR, driftPhase)
    }
}

// ── Spine (aculei) ─────────────────────────────────────────────────────────────
private fun DrawScope.drawSpines(cx: Float, cy: Float, bodyR: Float, fill: Float) {
    val spineCount = 16
    val baseLen = bodyR * 0.15f
    val extraLen = bodyR * 0.30f
    val spineLen = baseLen + fill * extraLen
    val spineW = 3.5f + fill * 2.5f

    for (i in 0 until spineCount) {
        val angle = (i.toFloat() / spineCount) * 2f * PI.toFloat()
        val startX = cx + cos(angle) * bodyR * 0.88f
        val startY = cy + sin(angle) * bodyR * 0.88f
        val endX = cx + cos(angle) * (bodyR + spineLen)
        val endY = cy + sin(angle) * (bodyR + spineLen)

        drawLine(
            color = SpineColor.copy(alpha = 0.75f + fill * 0.2f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = spineW,
            cap = StrokeCap.Round
        )
        // Punta più scura
        drawLine(
            color = OutlineColor.copy(alpha = 0.5f),
            start = Offset(endX - cos(angle) * spineLen * 0.25f, endY - sin(angle) * spineLen * 0.25f),
            end = Offset(endX, endY),
            strokeWidth = spineW * 0.5f,
            cap = StrokeCap.Round
        )
    }
}

// ── Schiuma birra ─────────────────────────────────────────────────────────────
private fun DrawScope.drawFoam(cx: Float, beerTop: Float, bodyR: Float, fill: Float) {
    val foamR = bodyR * 0.07f * fill.coerceAtMost(0.5f) / 0.5f
    val count = 8
    for (i in 0 until count) {
        val t = i.toFloat() / count
        val fx = cx - bodyR * 0.6f + t * bodyR * 1.2f
        val fy = beerTop - foamR * 0.5f
        drawCircle(BeerFoam.copy(alpha = 0.85f), foamR, Offset(fx, fy))
    }
}

// ── Bollicine birra interne ───────────────────────────────────────────────────
private fun DrawScope.drawBeerBubbles(cx: Float, beerTop: Float, beerBot: Float, bodyR: Float, phase: Float) {
    val bubbleData = listOf(
        Triple(-0.35f, 0.3f, 2.5f),
        Triple(0.10f,  0.6f, 2.0f),
        Triple(0.40f,  0.2f, 3.0f),
        Triple(-0.15f, 0.8f, 1.8f),
        Triple(0.25f,  0.5f, 2.2f)
    )
    val range = beerBot - beerTop
    if (range <= 0f) return
    bubbleData.forEach { (offX, baseYF, r) ->
        val bPhase = (phase / (2f * PI.toFloat()) + baseYF) % 1f
        val bx = cx + offX * bodyR
        val by = beerBot - bPhase * range
        if (by >= beerTop && by <= beerBot) {
            val alpha = (1f - bPhase * 0.7f).coerceIn(0f, 1f) * 0.5f
            drawCircle(BeerFoam.copy(alpha = alpha), r, Offset(bx, by), style = Stroke(0.8f))
            drawCircle(Color.White.copy(alpha = alpha * 0.6f), r * 0.25f, Offset(bx - r * 0.3f, by - r * 0.3f))
        }
    }
}

// ── Occhio normale ────────────────────────────────────────────────────────────
private fun DrawScope.drawNormalEye(cx: Float, cy: Float, r: Float, blinking: Boolean) {
    val eyeH = if (blinking) r * 0.1f else r
    drawOval(
        color = Color.White,
        topLeft = Offset(cx - r, cy - eyeH),
        size = Size(r * 2f, eyeH * 2f)
    )
    if (!blinking) {
        drawCircle(Color(0xFF1A1A1A), r * 0.55f, Offset(cx + r * 0.12f, cy + r * 0.08f))
        drawCircle(Color.White.copy(alpha = 0.8f), r * 0.18f, Offset(cx - r * 0.10f, cy - r * 0.20f))
    }
    drawOval(
        color = OutlineColor.copy(alpha = 0.4f),
        topLeft = Offset(cx - r, cy - eyeH),
        size = Size(r * 2f, eyeH * 2f),
        style = Stroke(1.2f)
    )
}

// ── Occhio inclinato (moderatamente ubriaco) ──────────────────────────────────
private fun DrawScope.drawTiltedEye(cx: Float, cy: Float, r: Float, tiltDeg: Float) {
    rotate(tiltDeg, pivot = Offset(cx, cy)) {
        drawOval(color = Color.White, topLeft = Offset(cx - r, cy - r * 0.75f), size = Size(r * 2f, r * 1.5f))
        // Pupilla spostata lateralmente (effetto "strano")
        drawCircle(Color(0xFF1A1A1A), r * 0.45f, Offset(cx + r * 0.25f, cy + r * 0.15f))
        drawCircle(Color.White.copy(alpha = 0.7f), r * 0.15f, Offset(cx + r * 0.10f, cy))
    }
}

// ── Occhio spirale (molto ubriaco) ────────────────────────────────────────────
private fun DrawScope.drawDrunkEye(cx: Float, cy: Float, r: Float, mirrored: Boolean) {
    drawCircle(Color.White, r, Offset(cx, cy))
    // Spirale disegnata come anelli concentrici sfasati
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.rgb(26, 26, 26)
        strokeWidth = 1.8f
        style = android.graphics.Paint.Style.STROKE
        isAntiAlias = true
    }
    drawContext.canvas.nativeCanvas.apply {
        var spiralR = r * 0.85f
        while (spiralR > r * 0.08f) {
            drawCircle(cx, cy, spiralR, paint)
            spiralR *= 0.62f
        }
    }
    drawCircle(Color(0xFF1A1A1A), r * 0.18f, Offset(cx, cy))
    // X rossa sopra
    val xPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.rgb(200, 30, 30)
        strokeWidth = 2f
        style = android.graphics.Paint.Style.STROKE
        strokeCap = android.graphics.Paint.Cap.ROUND
        isAntiAlias = true
    }
    val d = r * 0.35f
    drawContext.canvas.nativeCanvas.drawLine(cx - d, cy - r * 1.3f - d, cx + d, cy - r * 1.3f + d, xPaint)
    drawContext.canvas.nativeCanvas.drawLine(cx + d, cy - r * 1.3f - d, cx - d, cy - r * 1.3f + d, xPaint)
}

// ── Bocca ─────────────────────────────────────────────────────────────────────
private fun DrawScope.drawPuffMouth(cx: Float, cy: Float, bodyR: Float, isDrunk: Boolean) {
    val mouthR = bodyR * 0.12f
    val mouthPath = Path()
    if (isDrunk) {
        // Bocca storta a zig-zag
        mouthPath.moveTo(cx - mouthR * 1.5f, cy)
        mouthPath.cubicTo(
            cx - mouthR * 0.5f, cy + mouthR * 0.8f,
            cx + mouthR * 0.5f, cy - mouthR * 0.8f,
            cx + mouthR * 1.5f, cy
        )
    } else {
        // Bocca tonda a O (tipica dei pesci palla)
        mouthPath.addOval(
            androidx.compose.ui.geometry.Rect(
                center = Offset(cx, cy),
                radius = mouthR
            )
        )
        drawPath(mouthPath, Color(0xFF5C3010).copy(alpha = 0.2f))
    }
    drawPath(
        mouthPath,
        color = OutlineColor.copy(alpha = 0.65f),
        style = Stroke(2f, cap = StrokeCap.Round)
    )
}

// ── Pinna dorsale ─────────────────────────────────────────────────────────────
private fun DrawScope.drawDorsalFin(cx: Float, bodyTop: Float, bodyR: Float) {
    val finPath = Path().apply {
        moveTo(cx - bodyR * 0.22f, bodyTop)
        cubicTo(
            cx - bodyR * 0.10f, bodyTop - bodyR * 0.35f,
            cx + bodyR * 0.10f, bodyTop - bodyR * 0.35f,
            cx + bodyR * 0.22f, bodyTop
        )
    }
    drawPath(finPath, PuffNormal2.copy(alpha = 0.65f), style = Stroke(3f, cap = StrokeCap.Round))
}

// ── Pinna caudale ─────────────────────────────────────────────────────────────
private fun DrawScope.drawTailFin(cx: Float, cy: Float, bodyR: Float, phase: Float) {
    val sway = sin(phase * 2f) * bodyR * 0.06f
    val finPath = Path().apply {
        moveTo(cx, cy + sway)
        cubicTo(
            cx + bodyR * 0.30f, cy - bodyR * 0.30f + sway,
            cx + bodyR * 0.55f, cy - bodyR * 0.20f + sway,
            cx + bodyR * 0.45f, cy + sway
        )
        cubicTo(
            cx + bodyR * 0.55f, cy + bodyR * 0.20f + sway,
            cx + bodyR * 0.30f, cy + bodyR * 0.30f + sway,
            cx, cy + sway
        )
        close()
    }
    drawPath(finPath, PuffNormal2.copy(alpha = 0.60f))
    drawPath(finPath, OutlineColor.copy(alpha = 0.30f), style = Stroke(1.5f))
}
