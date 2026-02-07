package com.jellydrink.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private fun formatLiters(ml: Int): String {
    return when {
        ml % 1000 == 0 -> "${ml / 1000}L"
        else -> "%.1fL".format(ml / 1000f)
    }
}

@Composable
fun WaterProgressBar(
    currentMl: Int,
    goalMl: Int,
    modifier: Modifier = Modifier
) {
    val percentage = if (goalMl > 0) (currentMl.toFloat() / goalMl).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 600),
        label = "progressAnimation"
    )
    val isComplete = percentage >= 1f

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Testo litri sopra
        Text(
            text = formatLiters(currentMl),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Barra verticale bianca
        Canvas(
            modifier = Modifier
                .width(22.dp)
                .height(140.dp)
        ) {
            val barW = size.width
            val barH = size.height
            val cr = CornerRadius(barW / 2f, barW / 2f)

            // Sfondo barra (bianco semi-trasparente)
            drawRoundRect(
                color = Color.White.copy(alpha = 0.20f),
                size = Size(barW, barH),
                cornerRadius = cr
            )

            // Riempimento dal basso verso l'alto
            if (animatedProgress > 0f) {
                val fillH = barH * animatedProgress
                val fillTop = barH - fillH

                drawRoundRect(
                    color = if (isComplete) Color.White else Color.White.copy(alpha = 0.85f),
                    topLeft = Offset(0f, fillTop),
                    size = Size(barW, fillH),
                    cornerRadius = cr
                )

                // Riflesso luminoso laterale
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    ),
                    topLeft = Offset(0f, fillTop),
                    size = Size(barW * 0.45f, fillH),
                    cornerRadius = cr
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Obiettivo sotto
        Text(
            text = formatLiters(goalMl),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.6f)
        )

        // Percentuale
        Text(
            text = "${(percentage * 100).toInt()}%",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}
