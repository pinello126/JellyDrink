package com.jellydrink.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DecorationPreview(
    decorationId: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h / 2f
        val u = w * 0.22f

        clipRect(0f, 0f, w, h) {
            when (decorationId) {
                // Troppo grandi → ridotti a u*1.0f
                // Pesce con direction=1f ha testa a destra di cx → shift cx leggermente a sx
                "fish_blue" -> drawRealisticBlueFish(
                    cx = cx + u * 0.05f, cy = cy, size = u * 1.0f,
                    swimPhase = 0f, index = 0, direction = 1f
                )
                "fish_orange" -> drawRealisticClownfish(
                    cx = cx + u * 0.05f, cy = cy, size = u * 1.0f,
                    swimPhase = 0f, index = 0, direction = 1f
                )
                // Okay → invariata
                "starfish" -> drawStarfish(
                    cx = cx, cy = cy, size = u * 1.5f
                )
                // Più grande e centrata: base più in basso, altezza maggiore
                "coral_pink" -> drawCoral(
                    baseX = cx, baseY = h * 0.88f, height = h * 0.72f
                )
                // Più grande: size da u*1.0 a u*1.35f, cy abbassato per centrare
                "treasure" -> drawTreasureChest(
                    cx = cx, cy = h * 0.72f, size = u * 1.35f, time = 0f
                )
                // Grandezza ok, tartaruga con direction=1f ha testa a destra → shift cx a sx
                "turtle" -> drawRealisticTurtle(
                    cx = cx - u * 0.25f, cy = cy, size = u * 1.5f,
                    time = 0f, index = 0, direction = 1f
                )
                // Corpo del cavalluccio curva a destra → shift cx più a sx
                "seahorse" -> drawRealisticSeahorse(
                    cx = cx - u * 0.3f, cy = cy * 1.1f, size = u * 1.2f,
                    phaseA = 0f, phaseB = 0f, index = 0
                )
                // Perfetto → invariato
                "crab" -> drawRealisticCrab(
                    cx = cx, cy = cy, size = u * 1.5f,
                    time = 0f, walkSpeed = 0f
                )
            }
        }
    }
}
