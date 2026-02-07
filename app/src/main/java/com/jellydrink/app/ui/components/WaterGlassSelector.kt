package com.jellydrink.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val RetroBlue = Color(0xFF2196F3)
private val RetroBlueDark = Color(0xFF1565C0)
private val RetroOutline = Color(0xFF0D47A1)

@Composable
fun WaterGlassSelector(
    glasses: List<Int>,
    onGlassSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        items(glasses) { amount ->
            GlassButton(
                amountMl = amount,
                onClick = { onGlassSelected(amount) }
            )
        }
    }
}

@Composable
private fun GlassButton(
    amountMl: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = when {
        amountMl % 1000 == 0 -> "${amountMl / 1000}L"
        amountMl % 100 == 0 -> "%.1fL".format(amountMl / 1000f)
        else -> "%.2fL".format(amountMl / 1000f)
    }

    ElevatedButton(
        onClick = onClick,
        modifier = modifier.size(width = 90.dp, height = 110.dp),
        contentPadding = PaddingValues(8.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Canvas(modifier = Modifier.size(44.dp)) {
                when {
                    amountMl >= 1000 -> drawRetroBottle(this)
                    amountMl >= 500 -> drawRetroSmallBottle(this)
                    else -> drawRetroGlass(this)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

private fun drawRetroGlass(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        val strokeW = 3f
        val outline = RetroOutline

        // Bicchiere trapezoidale stile retro
        val glassPath = Path().apply {
            moveTo(w * 0.2f, h * 0.15f)
            lineTo(w * 0.8f, h * 0.15f)
            lineTo(w * 0.72f, h * 0.85f)
            lineTo(w * 0.28f, h * 0.85f)
            close()
        }

        // Riempimento
        drawPath(path = glassPath, color = RetroBlue.copy(alpha = 0.3f))

        // Acqua dentro
        val waterPath = Path().apply {
            moveTo(w * 0.26f, h * 0.4f)
            lineTo(w * 0.74f, h * 0.4f)
            lineTo(w * 0.72f, h * 0.85f)
            lineTo(w * 0.28f, h * 0.85f)
            close()
        }
        drawPath(path = waterPath, color = RetroBlue.copy(alpha = 0.6f))

        // Contorno spesso retro
        drawPath(
            path = glassPath,
            color = outline,
            style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Riflesso
        drawLine(
            color = Color.White.copy(alpha = 0.5f),
            start = Offset(w * 0.3f, h * 0.2f),
            end = Offset(w * 0.32f, h * 0.6f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )

        // Linee orizzontali retro (decorazione)
        drawLine(
            color = outline.copy(alpha = 0.3f),
            start = Offset(w * 0.25f, h * 0.5f),
            end = Offset(w * 0.75f, h * 0.5f),
            strokeWidth = 1.5f
        )
    }
}

private fun drawRetroSmallBottle(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        val strokeW = 3f
        val outline = RetroOutline

        // Collo bottiglia
        val neckPath = Path().apply {
            moveTo(w * 0.38f, h * 0.08f)
            lineTo(w * 0.62f, h * 0.08f)
            lineTo(w * 0.62f, h * 0.25f)
            lineTo(w * 0.38f, h * 0.25f)
            close()
        }
        drawPath(path = neckPath, color = RetroBlue.copy(alpha = 0.3f))
        drawPath(path = neckPath, color = outline, style = Stroke(width = strokeW, join = StrokeJoin.Round))

        // Tappo
        drawRoundRect(
            color = RetroBlueDark,
            topLeft = Offset(w * 0.35f, h * 0.02f),
            size = Size(w * 0.3f, h * 0.08f),
            cornerRadius = CornerRadius(3f, 3f)
        )

        // Corpo bottiglia
        val bodyPath = Path().apply {
            moveTo(w * 0.38f, h * 0.25f)
            lineTo(w * 0.25f, h * 0.35f)
            lineTo(w * 0.25f, h * 0.9f)
            lineTo(w * 0.75f, h * 0.9f)
            lineTo(w * 0.75f, h * 0.35f)
            lineTo(w * 0.62f, h * 0.25f)
            close()
        }
        drawPath(path = bodyPath, color = RetroBlue.copy(alpha = 0.3f))

        // Acqua
        val waterPath = Path().apply {
            moveTo(w * 0.25f, h * 0.5f)
            lineTo(w * 0.75f, h * 0.5f)
            lineTo(w * 0.75f, h * 0.9f)
            lineTo(w * 0.25f, h * 0.9f)
            close()
        }
        drawPath(path = waterPath, color = RetroBlue.copy(alpha = 0.6f))

        drawPath(path = bodyPath, color = outline, style = Stroke(width = strokeW, join = StrokeJoin.Round))

        // Etichetta retro
        drawRoundRect(
            color = Color.White.copy(alpha = 0.4f),
            topLeft = Offset(w * 0.3f, h * 0.55f),
            size = Size(w * 0.4f, h * 0.2f),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawRoundRect(
            color = outline.copy(alpha = 0.4f),
            topLeft = Offset(w * 0.3f, h * 0.55f),
            size = Size(w * 0.4f, h * 0.2f),
            cornerRadius = CornerRadius(2f, 2f),
            style = Stroke(width = 1.5f)
        )

        // Riflesso
        drawLine(
            color = Color.White.copy(alpha = 0.4f),
            start = Offset(w * 0.32f, h * 0.36f),
            end = Offset(w * 0.32f, h * 0.85f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

private fun drawRetroBottle(scope: DrawScope) {
    with(scope) {
        val w = size.width
        val h = size.height
        val strokeW = 3f
        val outline = RetroOutline

        // Collo lungo
        val neckPath = Path().apply {
            moveTo(w * 0.4f, h * 0.05f)
            lineTo(w * 0.6f, h * 0.05f)
            lineTo(w * 0.6f, h * 0.28f)
            lineTo(w * 0.4f, h * 0.28f)
            close()
        }
        drawPath(path = neckPath, color = RetroBlue.copy(alpha = 0.3f))
        drawPath(path = neckPath, color = outline, style = Stroke(width = strokeW, join = StrokeJoin.Round))

        // Tappo
        drawRoundRect(
            color = RetroBlueDark,
            topLeft = Offset(w * 0.36f, h * 0.0f),
            size = Size(w * 0.28f, h * 0.07f),
            cornerRadius = CornerRadius(3f, 3f)
        )

        // Corpo grande
        val bodyPath = Path().apply {
            moveTo(w * 0.4f, h * 0.28f)
            lineTo(w * 0.2f, h * 0.38f)
            lineTo(w * 0.2f, h * 0.95f)
            lineTo(w * 0.8f, h * 0.95f)
            lineTo(w * 0.8f, h * 0.38f)
            lineTo(w * 0.6f, h * 0.28f)
            close()
        }
        drawPath(path = bodyPath, color = RetroBlue.copy(alpha = 0.3f))

        // Acqua
        val waterPath = Path().apply {
            moveTo(w * 0.2f, h * 0.45f)
            lineTo(w * 0.8f, h * 0.45f)
            lineTo(w * 0.8f, h * 0.95f)
            lineTo(w * 0.2f, h * 0.95f)
            close()
        }
        drawPath(path = waterPath, color = RetroBlue.copy(alpha = 0.6f))

        drawPath(path = bodyPath, color = outline, style = Stroke(width = strokeW, join = StrokeJoin.Round))

        // Etichetta retro grande
        drawRoundRect(
            color = Color.White.copy(alpha = 0.4f),
            topLeft = Offset(w * 0.27f, h * 0.52f),
            size = Size(w * 0.46f, h * 0.25f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawRoundRect(
            color = outline.copy(alpha = 0.4f),
            topLeft = Offset(w * 0.27f, h * 0.52f),
            size = Size(w * 0.46f, h * 0.25f),
            cornerRadius = CornerRadius(3f, 3f),
            style = Stroke(width = 1.5f)
        )

        // Linea decorativa
        drawLine(
            color = outline.copy(alpha = 0.2f),
            start = Offset(w * 0.25f, h * 0.85f),
            end = Offset(w * 0.75f, h * 0.85f),
            strokeWidth = 1.5f
        )

        // Riflesso
        drawLine(
            color = Color.White.copy(alpha = 0.4f),
            start = Offset(w * 0.28f, h * 0.4f),
            end = Offset(w * 0.28f, h * 0.9f),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round
        )
    }
}
