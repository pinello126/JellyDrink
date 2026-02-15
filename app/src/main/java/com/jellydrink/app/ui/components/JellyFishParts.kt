package com.jellydrink.app.ui.components
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin// ═══════════════════════════════════════════════════════════════════
//  PATH CAMPANA con bordo smerlato organico
// ═══════════════════════════════════════════════════════════════════
internal fun buildBellPath(
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
internal fun buildScallopHighlight(cx: Float, bot: Float, hw: Float, scallopPhase: Float): Path {
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
internal fun DrawScope.drawRichEye(cx: Float, cy: Float, r: Float, blink: Boolean) {
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
internal fun DrawScope.drawRichMouth(cx: Float, my: Float, bw: Float, fillPct: Float, complete: Boolean) {
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
internal fun DrawScope.drawOralArms(
    cx: Float, bot: Float, bw: Float, bh: Float,
    phaseA: Float, phaseB: Float, phaseC: Float, count: Int = 5
) {
    val spread = bw * 1.4f
    val colors = listOf(ArmLight, ArmMid, ArmDark, ArmMid, ArmLight)

    for (i in 0 until count) {
        val t = i.toFloat() / (count - 1)
        val startX = cx - spread / 2f + spread * t
        val len = bh * 1.2f + (i % 3) * bh * 0.15f
        val thick = 6f - abs(i - 2) * 0.8f  // più spessi al centro

        // Ombra
        val sh = buildArmPath(startX + 2f, bot + 2f, phaseA, phaseB, phaseC, i, len)
        drawPath(sh, Color.Black.copy(alpha = 0.04f), style = Stroke(thick + 3f, cap = StrokeCap.Round))

        // Braccio principale
        val path = buildArmPath(startX, bot, phaseA, phaseB, phaseC, i, len)
        drawPath(path, colors[i].copy(alpha = 0.80f), style = Stroke(thick, cap = StrokeCap.Round))

        // Highlight laterale
        val hl = buildArmPath(startX - 0.6f, bot - 0.6f, phaseA, phaseB, phaseC, i, len)
        drawPath(hl, Color.White.copy(alpha = 0.18f), style = Stroke(thick * 0.25f, cap = StrokeCap.Round))

        // Outline leggero
        drawPath(path, Outline.copy(alpha = 0.08f), style = Stroke(thick + 1f, cap = StrokeCap.Round))
    }
}

internal fun buildArmPath(
    sx: Float, sy: Float,
    phaseA: Float, phaseB: Float, phaseC: Float,
    idx: Int, len: Float
): Path {
    val path = Path()
    path.moveTo(sx, sy)
    val segs = 16
    val segL = len / segs
    var px = sx; var py = sy
    for (s in 1..segs) {
        val st = s.toFloat() / segs
        val amp = (5f + 8f * st * st)
        // Combine 3 independent phases with NO multipliers — each loops 0→2π seamlessly
        val wave = sin(phaseA + idx * 0.8f + st * 3.5f) * 0.5f +
                   sin(phaseB + idx * 1.2f + st * 2.8f) * 0.3f +
                   sin(phaseC + idx * 0.5f + st * 4.2f) * 0.2f
        val nx = sx + wave * amp
        val ny = sy + s * segL
        path.quadraticTo((px + nx) / 2f, (py + ny) / 2f, nx, ny)
        px = nx; py = ny
    }
    return path
}

// ═══════════════════════════════════════════════════════════════════
//  FILAMENTI SOTTILI — trailing tentacles lunghi e leggeri
// ═══════════════════════════════════════════════════════════════════
internal fun DrawScope.drawTrailingFilaments(
    cx: Float, bot: Float, bw: Float, bh: Float,
    phaseA: Float, phaseB: Float, phaseC: Float, count: Int = 8
) {
    val spread = bw * 2f

    for (i in 0 until count) {
        val t = i.toFloat() / (count - 1)
        val startX = cx - spread / 2f + spread * t
        val len = bh * 2.0f + (i % 4) * bh * 0.20f
        val thick = 1.5f + (i % 2) * 0.4f
        val alpha = 0.25f + (i % 3) * 0.08f
        val color = if (i % 2 == 0) FilLight else FilDark

        val path = buildFilamentPath(startX, bot + bh * 0.08f, phaseA, phaseB, phaseC, i, len)
        drawPath(path, color.copy(alpha = alpha), style = Stroke(thick, cap = StrokeCap.Round))
    }
}

internal fun buildFilamentPath(
    sx: Float, sy: Float,
    phaseA: Float, phaseB: Float, phaseC: Float,
    idx: Int, len: Float
): Path {
    val path = Path()
    path.moveTo(sx, sy)
    val segs = 30
    val segL = len / segs
    var px = sx; var py = sy
    for (s in 1..segs) {
        val st = s.toFloat() / segs
        val amp = (2f + 14f * st * st)
        // Combine 3 independent phases — NO multipliers, perfectly seamless loops
        val wave = sin(phaseA + idx * 0.55f + st * 5f) * 0.45f +
                   sin(phaseB + idx * 0.9f + st * 3.7f) * 0.35f +
                   sin(phaseC + idx * 1.3f + st * 6.1f) * 0.20f
        val nx = sx + wave * amp
        val ny = sy + s * segL
        path.quadraticTo((px + nx) / 2f, (py + ny) / 2f, nx, ny)
        px = nx; py = ny
    }
    return path
}
