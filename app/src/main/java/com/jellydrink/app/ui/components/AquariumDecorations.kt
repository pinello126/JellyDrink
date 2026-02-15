package com.jellydrink.app.ui.components
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin// --- Starfish ---
internal fun DrawScope.drawStarfish(cx: Float, cy: Float, size: Float, mainColor: Color, darkColor: Color) {
    val arms = 5
    for (i in 0 until arms) {
        val angle = (i.toFloat() / arms) * 2f * PI.toFloat() - PI.toFloat() / 2f
        val endX = cx + cos(angle) * size
        val endY = cy + sin(angle) * size

        val armPath = Path().apply {
            moveTo(cx, cy)
            val ctrlAngle1 = angle - 0.3f
            val ctrlAngle2 = angle + 0.3f
            lineTo(cx + cos(ctrlAngle1) * size * 0.4f, cy + sin(ctrlAngle1) * size * 0.4f)
            lineTo(endX, endY)
            lineTo(cx + cos(ctrlAngle2) * size * 0.4f, cy + sin(ctrlAngle2) * size * 0.4f)
            close()
        }
        drawPath(armPath, mainColor)
        drawPath(armPath, darkColor, style = Stroke(1f))
    }

    // Center
    drawCircle(darkColor, size * 0.25f, Offset(cx, cy))
}

// --- Treasure Chest ---
internal fun DrawScope.drawTreasureChest(cx: Float, cy: Float, size: Float, time: Float) {
    val openAmount = (sin(time) * 0.5f + 0.5f) * 0.3f

    // Box
    drawRect(
        color = TreasureBrown,
        topLeft = Offset(cx - size * 0.5f, cy - size * 0.3f),
        size = Size(size, size * 0.6f)
    )

    // Lid (slightly open)
    val lidPath = Path().apply {
        moveTo(cx - size * 0.55f, cy - size * 0.3f)
        lineTo(cx - size * 0.55f, cy - size * 0.5f - openAmount * size)
        quadraticTo(cx, cy - size * 0.65f - openAmount * size * 1.5f, cx + size * 0.55f, cy - size * 0.5f - openAmount * size)
        lineTo(cx + size * 0.55f, cy - size * 0.3f)
        close()
    }
    drawPath(lidPath, TreasureBrown.copy(alpha = 0.9f))

    // Gold shine
    if (openAmount > 0.1f) {
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(TreasureGold, TreasureGold.copy(alpha = 0.3f), Color.Transparent),
                center = Offset(cx, cy - size * 0.35f),
                radius = size * 0.4f
            ),
            topLeft = Offset(cx - size * 0.3f, cy - size * 0.5f),
            size = Size(size * 0.6f, size * 0.3f)
        )
    }

    // Lock
    drawCircle(TreasureGold, size * 0.08f, Offset(cx, cy - size * 0.1f))
}


// --- Realistic Seahorse (Cartoon 3D) ---
internal fun DrawScope.drawRealisticSeahorse(
    cx: Float, cy: Float, size: Float,
    phaseA: Float, phaseB: Float, index: Int
) {
    val phaseOffset = index * 4.1f
    // Sway dolce senza moltiplicatori — perfettamente fluido
    val sway = sin(phaseA + phaseOffset) * size * 0.12f
    val tailCurl = sin(phaseB + phaseOffset) * 0.3f

    val bodyYellow = Color(0xFFFDD835) // Golden yellow
    val bodyOrange = Color(0xFFFFB74D) // Light orange
    val bodyDark = Color(0xFFF57C00) // Dark orange

    // Shadow
    drawOval(
        color = Color.Black.copy(alpha = 0.12f),
        topLeft = Offset(cx - size * 0.35f, cy + size * 0.9f),
        size = Size(size * 0.7f, size * 0.4f)
    )

    // === CURVED BODY/TAIL ===
    val bodyPath = Path().apply {
        moveTo(cx, cy - size * 0.5f) // Head
        // Body curves down and back
        cubicTo(
            cx + size * 0.35f, cy - size * 0.2f,
            cx + size * 0.4f + sway * 0.3f, cy + size * 0.2f,
            cx + size * 0.25f + sway * 0.5f, cy + size * 0.5f
        )
        // Tail curls
        cubicTo(
            cx + size * 0.1f + sway * 0.7f, cy + size * 0.75f,
            cx - size * 0.15f + sway, cy + size * 0.85f + tailCurl * size * 0.2f,
            cx - size * 0.25f + sway * 0.8f, cy + size * 0.75f + tailCurl * size * 0.3f
        )
    }

    drawPath(
        bodyPath,
        brush = Brush.linearGradient(
            colors = listOf(bodyYellow, bodyOrange, bodyDark),
            start = Offset(cx, cy - size * 0.5f),
            end = Offset(cx - size * 0.25f, cy + size * 0.75f)
        ),
        style = Stroke(size * 0.28f, cap = StrokeCap.Round)
    )

    // === HEAD (rounded) ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(bodyYellow, bodyOrange),
            center = Offset(cx, cy - size * 0.5f),
            radius = size * 0.35f
        ),
        topLeft = Offset(cx - size * 0.25f, cy - size * 0.75f),
        size = Size(size * 0.5f, size * 0.45f)
    )

    // === CROWN/SPIKES on head ===
    for (i in 0..3) {
        val spikeAngle = -PI.toFloat() * 0.6f + i * 0.15f
        val spikeLen = size * (0.2f + (i % 2) * 0.08f)
        val spikeStart = Offset(cx - size * 0.05f, cy - size * 0.72f)
        val spikeEnd = Offset(
            spikeStart.x + cos(spikeAngle) * spikeLen,
            spikeStart.y + sin(spikeAngle) * spikeLen
        )
        drawLine(
            color = bodyDark,
            start = spikeStart,
            end = spikeEnd,
            strokeWidth = size * 0.08f,
            cap = StrokeCap.Round
        )
    }

    // === SNOUT ===
    drawLine(
        color = bodyDark.copy(alpha = 0.9f),
        start = Offset(cx + size * 0.2f, cy - size * 0.58f),
        end = Offset(cx + size * 0.48f, cy - size * 0.54f),
        strokeWidth = size * 0.12f,
        cap = StrokeCap.Round
    )

    // === BIG CARTOON EYE ===
    val eyeX = cx + size * 0.05f
    val eyeY = cy - size * 0.58f
    val eyeRadius = size * 0.18f

    drawCircle(Color.White, eyeRadius, Offset(eyeX, eyeY))
    drawCircle(
        Color.Black.copy(alpha = 0.3f),
        eyeRadius,
        Offset(eyeX, eyeY),
        style = Stroke(1.5f)
    )

    // Iris
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF1976D2), Color(0xFF0D47A1)),
            center = Offset(eyeX + size * 0.02f, eyeY),
            radius = eyeRadius * 0.6f
        ),
        radius = eyeRadius * 0.6f,
        center = Offset(eyeX + size * 0.02f, eyeY)
    )

    // Pupil
    drawCircle(Color.Black, eyeRadius * 0.35f, Offset(eyeX + size * 0.03f, eyeY))

    // Shine
    drawCircle(
        Color.White.copy(alpha = 0.9f),
        eyeRadius * 0.15f,
        Offset(eyeX + size * 0.05f, eyeY - size * 0.04f)
    )

    // === DORSAL FIN (wavy) ===
    val finPath = Path().apply {
        moveTo(cx + size * 0.05f, cy - size * 0.25f)
        quadraticTo(
            cx + size * 0.25f + sway * 0.4f,
            cy,
            cx + size * 0.08f,
            cy + size * 0.25f
        )
    }
    drawPath(
        finPath,
        bodyYellow.copy(alpha = 0.6f),
        style = Stroke(size * 0.1f, cap = StrokeCap.Round)
    )

    // === BELLY RIDGES ===
    for (i in 0..4) {
        val ridgeY = cy - size * 0.3f + i * size * 0.18f
        val ridgeX = cx + size * 0.18f + sway * 0.2f
        drawLine(
            color = bodyDark.copy(alpha = 0.3f),
            start = Offset(ridgeX - size * 0.08f, ridgeY),
            end = Offset(ridgeX + size * 0.08f, ridgeY),
            strokeWidth = 1.8f
        )
    }
}

// --- Realistic Crab (Cartoon 3D) ---
internal fun DrawScope.drawRealisticCrab(cx: Float, cy: Float, size: Float, time: Float, walkSpeed: Float = 0.5f) {
    // Chele: oscillazione gentile, più ampia quando cammina
    val clawMove = sin(time) * (0.03f + walkSpeed * 0.08f)
    val eyeWave = sin(time * 3f) * 0.04f

    // Zampe: ritmo di camminata proporzionale alla velocità
    val legCycle = time * 2f  // ciclo zampe (2× = intero, seamless)

    val crabRed = Color(0xFFE53935)
    val crabDark = Color(0xFFC62828)
    val crabLight = Color(0xFFFF6F60)

    // Shadow
    drawOval(
        color = Color.Black.copy(alpha = 0.18f),
        topLeft = Offset(cx - size * 0.7f, cy + size * 0.15f),
        size = Size(size * 1.4f, size * 0.4f)
    )

    // === LEGS (behind body, walking animation) ===
    for (i in 0..2) {
        for (side in listOf(-1f, 1f)) {
            val legBaseX = cx + side * (size * 0.35f + i * size * 0.14f)
            val legMidX = cx + side * (size * 0.55f + i * size * 0.14f)
            // Zampe alternate: pari avanti quando dispari indietro
            val legPhase = legCycle + i * 2.1f + (if (side > 0) 0f else PI.toFloat())
            val legLift = sin(legPhase) * size * 0.08f * walkSpeed
            val legStride = cos(legPhase) * size * 0.06f * walkSpeed
            val legEndX = legMidX + side * size * 0.18f + legStride
            val legY = cy + size * 0.05f

            // Segmento superiore (femore)
            drawLine(
                color = crabRed.copy(alpha = 0.85f),
                start = Offset(legBaseX, legY),
                end = Offset(legMidX + legStride * 0.5f, legY + size * 0.12f - legLift),
                strokeWidth = size * 0.07f,
                cap = StrokeCap.Round
            )
            // Segmento inferiore (tibia)
            drawLine(
                color = crabDark.copy(alpha = 0.8f),
                start = Offset(legMidX + legStride * 0.5f, legY + size * 0.12f - legLift),
                end = Offset(legEndX, legY + size * 0.28f),
                strokeWidth = size * 0.05f,
                cap = StrokeCap.Round
            )
        }
    }

    // === BODY (oval shell) ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(crabLight, crabRed, crabDark),
            center = Offset(cx, cy - size * 0.15f),
            radius = size * 0.65f
        ),
        topLeft = Offset(cx - size * 0.6f, cy - size * 0.35f),
        size = Size(size * 1.2f, size * 0.7f)
    )

    // Body spots
    for (i in 0..3) {
        val spotX = cx - size * 0.3f + (i % 2) * size * 0.3f
        val spotY = cy - size * 0.25f + (i / 2) * size * 0.2f
        drawCircle(crabDark.copy(alpha = 0.3f), size * 0.08f, Offset(spotX, spotY))
    }

    // === EYES ON STALKS ===
    for (side in listOf(-1f, 1f)) {
        val eyeStalkX = cx + side * size * 0.3f
        val eyeTopY = cy - size * 0.6f + eyeWave * size
        drawLine(
            brush = Brush.linearGradient(
                listOf(crabRed, crabLight),
                start = Offset(eyeStalkX, cy - size * 0.3f),
                end = Offset(eyeStalkX + side * size * 0.08f, eyeTopY)
            ),
            start = Offset(eyeStalkX, cy - size * 0.3f),
            end = Offset(eyeStalkX + side * size * 0.08f, eyeTopY),
            strokeWidth = size * 0.12f, cap = StrokeCap.Round
        )
        drawCircle(Color.White, size * 0.14f, Offset(eyeStalkX + side * size * 0.08f, eyeTopY - size * 0.02f))
        drawCircle(Color.Black, size * 0.08f, Offset(eyeStalkX + side * size * 0.11f, eyeTopY - size * 0.02f))
        drawCircle(Color.White.copy(alpha = 0.8f), size * 0.04f, Offset(eyeStalkX + side * size * 0.12f, eyeTopY - size * 0.05f))
    }

    // === CLAWS (pincers) ===
    for (side in listOf(-1f, 1f)) {
        val clawX = cx + side * size * 0.7f
        val clawOpenAngle = clawMove * side

        // Arm
        drawLine(
            brush = Brush.linearGradient(
                listOf(crabRed, crabDark),
                start = Offset(cx + side * size * 0.5f, cy - size * 0.05f),
                end = Offset(clawX, cy - size * 0.15f)
            ),
            start = Offset(cx + side * size * 0.5f, cy - size * 0.05f),
            end = Offset(clawX, cy - size * 0.15f),
            strokeWidth = size * 0.18f, cap = StrokeCap.Round
        )

        // Top pincer
        drawOval(
            brush = Brush.radialGradient(
                listOf(crabLight, crabDark),
                center = Offset(clawX, cy - size * 0.3f), radius = size * 0.25f
            ),
            topLeft = Offset(clawX - size * 0.2f, cy - size * 0.4f + clawOpenAngle * size),
            size = Size(size * 0.4f, size * 0.22f)
        )

        // Bottom pincer
        drawOval(
            brush = Brush.radialGradient(
                listOf(crabLight, crabDark),
                center = Offset(clawX, cy), radius = size * 0.25f
            ),
            topLeft = Offset(clawX - size * 0.2f, cy - size * 0.08f - clawOpenAngle * size),
            size = Size(size * 0.4f, size * 0.22f)
        )
    }
}
