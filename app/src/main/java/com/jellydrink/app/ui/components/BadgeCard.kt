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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

// Colori locked
private val LockedGray = Color(0xFF808080)
private val LockedLight = Color(0xFFB0B0B0)
private val LockedDark = Color(0xFF606060)

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
            // Medaglia o emoji
            if (badge.isEarned) {
                Canvas(modifier = Modifier.size(52.dp)) {
                    drawMedal(badge.type)
                }
            } else {
                // Badge locked - mostra emoji in grigio
                Text(
                    text = badge.icon,
                    fontSize = 32.sp,
                    modifier = Modifier.size(52.dp),
                    color = Color.Gray.copy(alpha = 0.3f)
                )
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

private fun DrawScope.drawMedal(type: String) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f
    val medalR = w * 0.36f

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
    drawPath(leftRibbon, RibbonRed)
    drawPath(leftRibbon, RibbonDark.copy(alpha = 0.3f), style = Stroke(1f))

    // Nastro destro
    val rightRibbon = Path().apply {
        moveTo(cx - ribbonW * 0.2f, ribbonTop)
        lineTo(cx + ribbonW * 0.1f, ribbonBot)
        lineTo(cx + ribbonW * 0.8f, ribbonBot - medalR * 0.2f)
        lineTo(cx + ribbonW * 1.6f, ribbonBot)
        lineTo(cx + ribbonW * 0.3f, ribbonTop)
        close()
    }
    drawPath(rightRibbon, RibbonRed.copy(alpha = 0.85f))
    drawPath(rightRibbon, RibbonDark.copy(alpha = 0.3f), style = Stroke(1f))

    // Ombra medaglia
    drawCircle(
        color = Color.Black.copy(alpha = 0.12f),
        radius = medalR * 1.05f,
        center = Offset(cx + 1.5f, cy + 1.5f)
    )

    // Corpo medaglia — gradiente dorato
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(GoldLight, GoldMain, GoldDark, GoldDeep),
            center = Offset(cx - medalR * 0.2f, cy - medalR * 0.2f),
            radius = medalR * 1.5f
        ),
        radius = medalR,
        center = Offset(cx, cy)
    )

    // Bordo esterno medaglia
    drawCircle(
        color = GoldDeep,
        radius = medalR,
        center = Offset(cx, cy),
        style = Stroke(2.5f)
    )

    // Anello interno
    drawCircle(
        color = GoldDeep.copy(alpha = 0.5f),
        radius = medalR * 0.82f,
        center = Offset(cx, cy),
        style = Stroke(1.2f)
    )

    // Highlight speculare
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.45f), Color.Transparent),
            center = Offset(cx - medalR * 0.25f, cy - medalR * 0.25f),
            radius = medalR * 0.6f
        ),
        radius = medalR * 0.7f,
        center = Offset(cx - medalR * 0.15f, cy - medalR * 0.2f)
    )

    // Icona centrale in base al tipo
    when (type) {
        "first_sip" -> drawWaterDrop(cx, cy, medalR * 0.4f)
        "daily_goal" -> drawStar(cx, cy, medalR * 0.45f, 5)
        "streak_3" -> drawFlame(cx, cy, medalR * 0.4f)
        "streak_7" -> drawFlame(cx, cy, medalR * 0.45f)
        "streak_30" -> drawDoubleStar(cx, cy, medalR * 0.4f)
        else -> drawStar(cx, cy, medalR * 0.4f, 5)
    }
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

// Fiamma
private fun DrawScope.drawFlame(cx: Float, cy: Float, r: Float) {
    val flame = Path().apply {
        moveTo(cx, cy - r * 1.1f)
        cubicTo(cx + r * 0.6f, cy - r * 0.4f, cx + r * 0.7f, cy + r * 0.2f, cx + r * 0.3f, cy + r * 0.7f)
        cubicTo(cx + r * 0.15f, cy + r * 0.9f, cx - r * 0.15f, cy + r * 0.9f, cx - r * 0.3f, cy + r * 0.7f)
        cubicTo(cx - r * 0.7f, cy + r * 0.2f, cx - r * 0.6f, cy - r * 0.4f, cx, cy - r * 1.1f)
        close()
    }
    drawPath(flame, Color(0xFFFF6830))
    // Nucleo interno
    val inner = Path().apply {
        moveTo(cx, cy - r * 0.3f)
        cubicTo(cx + r * 0.25f, cy, cx + r * 0.2f, cy + r * 0.4f, cx, cy + r * 0.55f)
        cubicTo(cx - r * 0.2f, cy + r * 0.4f, cx - r * 0.25f, cy, cx, cy - r * 0.3f)
        close()
    }
    drawPath(inner, Color(0xFFFFD040))
    drawPath(flame, GoldDeep.copy(alpha = 0.3f), style = Stroke(0.8f))
}

// Doppia stella
private fun DrawScope.drawDoubleStar(cx: Float, cy: Float, r: Float) {
    drawStar(cx, cy, r * 1.05f, 6)
    drawStar(cx, cy, r * 0.55f, 6)
}
