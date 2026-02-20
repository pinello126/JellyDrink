package com.jellydrink.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jellydrink.app.data.repository.WaterRepository
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Colori medaglia
private val GoldLight = Color(0xFFFFE070)
private val GoldMain = Color(0xFFFFD040)
private val GoldDark = Color(0xFFC8A020)
private val GoldDeep = Color(0xFFA08018)
private val RibbonRed = Color(0xFFD03030)
private val RibbonDark = Color(0xFF981818)
private val StarColor = Color(0xFFFFFFFF)

// Palette azzurra per le medaglie di Giorni Attivi, Livelli, Sfide e Record
private val AzzurroHi   = Color(0xFFBBDEFB)
private val AzzurroMid  = Color(0xFF64B5F6)
private val AzzurroDark = Color(0xFF1565C0)
private val AzzurroDeep = Color(0xFF0D47A1)

// Composable riutilizzabile che disegna solo la medaglia (usato anche nel popup HomeScreen)
@Composable
internal fun BadgeMedalCanvas(
    category: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawMedal(category, locked = false)
    }
}

@Composable
fun BadgeCard(
    badge: WaterRepository.BadgeWithStatus,
    modifier: Modifier = Modifier
) {
    val alpha = if (badge.isEarned) 1f else 0.4f

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isEarned) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sempre medaglia: dorata se sbloccata, grigia se locked
            Canvas(modifier = Modifier.size(52.dp)) {
                drawMedal(badge.category, locked = !badge.isEarned)
            }

            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = badge.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (badge.isEarned) FontWeight.Bold else FontWeight.Normal,
                    color = if (badge.isEarned) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    }
                )
                Text(
                    text = badge.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (badge.isEarned) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    }
                )
                if (badge.isEarned && badge.dateEarned != null) {
                    Text(
                        text = "Sbloccato: ${badge.dateEarned}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                } else if (!badge.isEarned) {
                    Text(
                        text = "🔒 Da sbloccare",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawMedal(category: String, locked: Boolean = false) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f
    val medalR = w * 0.36f

    // Categorie che usano la medaglia azzurra
    val isBlue = category in listOf("Giorni Attivi", "Livelli", "Sfide e Record")

    // Palette: azzurra o dorata (il locked viene gestito via saveLayerAlpha)
    val ribbonMain  = RibbonRed
    val ribbonEdge  = RibbonDark
    val medalHi     = if (isBlue) AzzurroHi   else GoldLight
    val medalMid    = if (isBlue) AzzurroMid  else GoldMain
    val medalDark   = if (isBlue) AzzurroDark else GoldDark
    val medalDeep   = if (isBlue) AzzurroDeep else GoldDeep

    // Per le medaglie locked: sbiadisce l'intera medaglia con alpha ~36%
    val nativeCanvas = drawContext.canvas.nativeCanvas
    if (locked) nativeCanvas.saveLayerAlpha(null, 92)

    // Nastro (ribbon) dietro la medaglia
    val ribbonW = medalR * 0.45f
    val ribbonTop = cy - medalR * 0.3f
    val ribbonBot = h * 0.95f

    // Nastro sinistro
    val leftRibbon = Path().apply {
        moveTo(cx - ribbonW * 0.3f, ribbonTop)
        lineTo(cx - ribbonW * 1.6f, ribbonBot)
        lineTo(cx - ribbonW * 0.8f, ribbonBot - medalR * 0.2f)
        lineTo(cx - ribbonW * 0.1f, ribbonBot)
        lineTo(cx + ribbonW * 0.2f, ribbonTop)
        close()
    }
    drawPath(leftRibbon, ribbonMain)
    drawPath(leftRibbon, ribbonEdge.copy(alpha = 0.3f), style = Stroke(1f))

    // Nastro destro
    val rightRibbon = Path().apply {
        moveTo(cx - ribbonW * 0.2f, ribbonTop)
        lineTo(cx + ribbonW * 0.1f, ribbonBot)
        lineTo(cx + ribbonW * 0.8f, ribbonBot - medalR * 0.2f)
        lineTo(cx + ribbonW * 1.6f, ribbonBot)
        lineTo(cx + ribbonW * 0.3f, ribbonTop)
        close()
    }
    drawPath(rightRibbon, ribbonMain.copy(alpha = 0.85f))
    drawPath(rightRibbon, ribbonEdge.copy(alpha = 0.3f), style = Stroke(1f))

    // Ombra medaglia
    drawCircle(
        color = Color.Black.copy(alpha = 0.12f),
        radius = medalR * 1.05f,
        center = Offset(cx + 1.5f, cy + 1.5f)
    )

    // Corpo medaglia
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(medalHi, medalMid, medalDark, medalDeep),
            center = Offset(cx - medalR * 0.2f, cy - medalR * 0.2f),
            radius = medalR * 1.5f
        ),
        radius = medalR,
        center = Offset(cx, cy)
    )

    // Bordo esterno
    drawCircle(
        color = medalDeep,
        radius = medalR,
        center = Offset(cx, cy),
        style = Stroke(2.5f)
    )

    // Anello interno
    drawCircle(
        color = medalDeep.copy(alpha = 0.5f),
        radius = medalR * 0.82f,
        center = Offset(cx, cy),
        style = Stroke(1.2f)
    )

    // Highlight speculare (meno intenso se locked)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.45f), Color.Transparent),
            center = Offset(cx - medalR * 0.25f, cy - medalR * 0.25f),
            radius = medalR * 0.6f
        ),
        radius = medalR * 0.7f,
        center = Offset(cx - medalR * 0.15f, cy - medalR * 0.2f)
    )

    when (category) {
        "Primi Passi"    -> drawWaterDrop(cx, cy, medalR * 0.42f)
        "Streak"         -> drawEmojiIcon("\uD83D\uDD25", cx, cy, medalR * 0.85f) // 🔥
        "Litri Totali"   -> drawEmojiIcon("\uD83C\uDF0A", cx, cy, medalR * 0.85f) // 🌊
        "Giorni Attivi"  -> drawEmojiIcon("\u2600\uFE0F", cx, cy, medalR * 0.85f) // ☀️
        "Livelli"        -> drawEmojiIcon("\u2B50",        cx, cy, medalR * 0.85f) // ⭐
        "Sfide e Record" -> drawEmojiIcon("\u26A1",        cx, cy, medalR * 0.85f) // ⚡
        else             -> drawStar(cx, cy, medalR * 0.42f, 5)
    }

    if (locked) nativeCanvas.restore()
}

// Goccia d'acqua
private fun DrawScope.drawWaterDrop(cx: Float, cy: Float, r: Float) {
    val drop = Path().apply {
        moveTo(cx, cy - r * 1.1f)
        cubicTo(cx + r * 0.1f, cy - r * 0.5f, cx + r * 0.8f, cy, cx + r * 0.5f, cy + r * 0.5f)
        cubicTo(cx + r * 0.3f, cy + r * 0.9f, cx - r * 0.3f, cy + r * 0.9f, cx - r * 0.5f, cy + r * 0.5f)
        cubicTo(cx - r * 0.8f, cy, cx - r * 0.1f, cy - r * 0.5f, cx, cy - r * 1.1f)
        close()
    }
    drawPath(drop, Color(0xFF3888D0))
    drawPath(drop, GoldDeep.copy(alpha = 0.4f), style = Stroke(1f))
    // Riflesso
    drawCircle(Color.White.copy(alpha = 0.5f), r * 0.12f, Offset(cx - r * 0.15f, cy - r * 0.1f))
}

// Stella a N punte
private fun DrawScope.drawStar(cx: Float, cy: Float, r: Float, points: Int) {
    val path = Path()
    val innerR = r * 0.42f
    for (i in 0 until points * 2) {
        val angle = (i * PI.toFloat() / points) - PI.toFloat() / 2f
        val radius = if (i % 2 == 0) r else innerR
        val x = cx + cos(angle) * radius
        val y = cy + sin(angle) * radius
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, StarColor.copy(alpha = 0.9f))
    drawPath(path, GoldDeep.copy(alpha = 0.35f), style = Stroke(0.8f))
}

// Emoji centrata dentro la medaglia via nativeCanvas
private fun DrawScope.drawEmojiIcon(emoji: String, cx: Float, cy: Float, textSize: Float) {
    val paint = android.graphics.Paint().apply {
        this.textSize = textSize
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
    }
    // Offset verticale: drawText disegna dalla baseline, non dal centro
    val textBounds = android.graphics.Rect()
    paint.getTextBounds(emoji, 0, emoji.length, textBounds)
    val verticalOffset = textBounds.height() / 2f - textBounds.bottom
    drawContext.canvas.nativeCanvas.drawText(emoji, cx, cy + verticalOffset, paint)
}

