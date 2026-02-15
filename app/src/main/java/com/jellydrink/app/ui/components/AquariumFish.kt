package com.jellydrink.app.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

// ═══════════════════════════════════════════════════════════════════
//  SHARED CARTOON FISH EYE HELPER
// ═══════════════════════════════════════════════════════════════════
internal data class CartoonEyeStyle(
    val offsetX: Float,
    val offsetY: Float,
    val radius: Float,
    val ovalStretch: Float,
    val irisRadiusFactor: Float,
    val pupilRadiusFactor: Float,
    val irisColors: List<Color>,
    val outlineColor: Color,
)

internal fun DrawScope.drawCartoonFishEye(
    cx: Float, bodyY: Float, size: Float, direction: Float,
    style: CartoonEyeStyle
) {
    val eyeX = cx + direction * size * style.offsetX
    val eyeY = bodyY + size * style.offsetY
    val r = size * style.radius

    // Eye white (slightly oval)
    drawOval(
        color = Color.White,
        topLeft = Offset(eyeX - r, eyeY - r * style.ovalStretch),
        size = Size(r * 2f, r * 2f * style.ovalStretch)
    )
    // Eye outline
    drawOval(
        color = style.outlineColor,
        topLeft = Offset(eyeX - r, eyeY - r * style.ovalStretch),
        size = Size(r * 2f, r * 2f * style.ovalStretch),
        style = Stroke(2f)
    )
    // Iris
    val irisR = r * style.irisRadiusFactor
    val irisCx = eyeX + direction * size * 0.05f
    drawCircle(
        brush = Brush.radialGradient(
            colors = style.irisColors,
            center = Offset(irisCx, eyeY),
            radius = irisR
        ),
        radius = irisR,
        center = Offset(irisCx, eyeY)
    )
    // Pupil
    drawCircle(
        Color.Black,
        r * style.pupilRadiusFactor,
        Offset(eyeX + direction * size * 0.08f, eyeY + size * 0.02f)
    )
    // Primary shine
    drawCircle(
        Color.White.copy(alpha = 0.95f),
        r * 0.17f,
        Offset(eyeX + direction * size * 0.12f, eyeY - size * 0.08f)
    )
    // Secondary shine
    drawCircle(
        Color.White.copy(alpha = 0.6f),
        r * 0.10f,
        Offset(eyeX - direction * size * 0.05f, eyeY + size * 0.1f)
    )
}

// --- Realistic Blue Fish (Cartoon 3D Style like Fishdom) ---
internal fun DrawScope.drawRealisticBlueFish(
    cx: Float,
    cy: Float,
    size: Float,
    swimPhase: Float,
    index: Int,
    direction: Float = 1f
) {
    // Animazioni nuoto — swimPhase va 0→2π in 3s, perfettamente fluido
    val phaseOffset = index * 3.7f
    // Coda: oscillazione rapida (2× = intero, sin(4π)=0, seamless)
    val tailWag = sin(swimPhase * 2f + phaseOffset) * 0.3f
    // Corpo: bobbing gentile, usa la fase direttamente (nessun moltiplicatore)
    val bodyBounce = sin(swimPhase + phaseOffset + 1.2f) * 0.06f
    // Pinne: battito, 3× = intero (sin(6π)=0, seamless)
    val finFlap = sin(swimPhase * 3f + phaseOffset) * 0.4f

    // Colors - Turquoise/Cyan with yellow accents
    val bodyMainColor = Color(0xFF00CED1) // Turquoise
    val bodyLightColor = Color(0xFF7FFFD4) // Aquamarine light
    val bodyDarkColor = Color(0xFF008B8B) // Dark cyan
    val bellyColor = Color(0xFFB0E0E6) // Powder blue
    val accentYellow = Color(0xFFFDD835) // Golden yellow
    val accentOrange = Color(0xFFFFB74D) // Light orange
    val finColor = Color(0xFF40E0D0) // Turquoise
    val finLightColor = Color(0xFFAFEEEE) // Pale turquoise

    // Direction passed as parameter - fish faces the direction it's swimming
    val bodyY = cy + bodyBounce * size

    // === SHADOW ===
    drawOval(
        color = Color.Black.copy(alpha = 0.15f),
        topLeft = Offset(cx - size * 1.4f, bodyY + size * 0.9f),
        size = Size(size * 2.8f, size * 0.6f)
    )

    // === TAIL FIN (Caudal) - Behind body ===
    val tailPath = Path().apply {
        val tailBase = cx - direction * size * 1.0f
        val tailSpread = size * 1.2f
        moveTo(tailBase, bodyY)
        // Top lobe
        cubicTo(
            tailBase - direction * size * 0.4f, bodyY - tailSpread * 0.6f + tailWag * size * 0.8f,
            tailBase - direction * size * 0.8f, bodyY - tailSpread * 0.7f + tailWag * size,
            tailBase - direction * size * 1.1f, bodyY - tailSpread * 0.5f + tailWag * size * 0.9f
        )
        // Middle
        lineTo(tailBase - direction * size * 0.9f, bodyY + tailWag * size * 0.3f)
        // Bottom lobe
        cubicTo(
            tailBase - direction * size * 0.8f, bodyY + tailSpread * 0.7f + tailWag * size,
            tailBase - direction * size * 0.4f, bodyY + tailSpread * 0.6f + tailWag * size * 0.8f,
            tailBase, bodyY
        )
        close()
    }

    // Tail gradient
    drawPath(
        tailPath,
        brush = Brush.radialGradient(
            colors = listOf(
                finColor,
                finLightColor.copy(alpha = 0.9f),
                finLightColor.copy(alpha = 0.7f)
            ),
            center = Offset(cx - direction * size * 1.0f, bodyY),
            radius = size * 1.5f
        )
    )

    // Tail fin rays
    for (i in 0..6) {
        val rayAngle = -0.6f + i * 0.2f
        val rayLen = size * (0.9f + (i % 2) * 0.15f)
        val rayStart = Offset(cx - direction * size * 1.0f, bodyY)
        val rayEnd = Offset(
            rayStart.x - direction * cos(rayAngle) * rayLen,
            rayStart.y + sin(rayAngle) * rayLen + tailWag * size * 0.8f
        )
        drawLine(
            color = bodyDarkColor.copy(alpha = 0.3f),
            start = rayStart,
            end = rayEnd,
            strokeWidth = 1.2f,
            cap = StrokeCap.Round
        )
    }

    // === MAIN BODY (Chubby, round) ===
    val bodyPath = Path().apply {
        // Create a rounded, chubby fish body
        moveTo(cx + direction * size * 1.0f, bodyY) // Front (head)
        // Top curve
        cubicTo(
            cx + direction * size * 0.8f, bodyY - size * 0.9f,
            cx + direction * size * 0.2f, bodyY - size * 1.1f,
            cx - direction * size * 0.6f, bodyY - size * 0.8f
        )
        // Back top to tail
        cubicTo(
            cx - direction * size * 0.9f, bodyY - size * 0.5f,
            cx - direction * size * 1.0f, bodyY - size * 0.2f,
            cx - direction * size * 1.0f, bodyY
        )
        // Back bottom to tail
        cubicTo(
            cx - direction * size * 1.0f, bodyY + size * 0.2f,
            cx - direction * size * 0.9f, bodyY + size * 0.5f,
            cx - direction * size * 0.6f, bodyY + size * 0.8f
        )
        // Bottom curve
        cubicTo(
            cx + direction * size * 0.2f, bodyY + size * 1.1f,
            cx + direction * size * 0.8f, bodyY + size * 0.9f,
            cx + direction * size * 1.0f, bodyY
        )
        close()
    }

    // Body base gradient (3D effect)
    drawPath(
        bodyPath,
        brush = Brush.radialGradient(
            colors = listOf(
                bodyLightColor,
                bodyMainColor,
                bodyDarkColor
            ),
            center = Offset(cx + direction * size * 0.3f, bodyY - size * 0.2f),
            radius = size * 1.5f
        )
    )

    // === BELLY HIGHLIGHT (lighter area) ===
    val bellyPath = Path().apply {
        moveTo(cx + direction * size * 0.8f, bodyY)
        cubicTo(
            cx + direction * size * 0.5f, bodyY + size * 0.7f,
            cx, bodyY + size * 0.85f,
            cx - direction * size * 0.5f, bodyY + size * 0.6f
        )
        cubicTo(
            cx - direction * size * 0.3f, bodyY + size * 0.3f,
            cx + direction * size * 0.3f, bodyY + size * 0.2f,
            cx + direction * size * 0.8f, bodyY
        )
        close()
    }
    drawPath(bellyPath, bellyColor.copy(alpha = 0.6f))

    // === YELLOW/GOLD ACCENT near gills ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                accentYellow.copy(alpha = 0.7f),
                accentOrange.copy(alpha = 0.5f),
                Color.Transparent
            ),
            center = Offset(cx + direction * size * 0.2f, bodyY - size * 0.3f),
            radius = size * 0.5f
        ),
        topLeft = Offset(cx + direction * size * 0.0f, bodyY - size * 0.6f),
        size = Size(size * 0.5f, size * 0.6f)
    )

    // === SCALES PATTERN (detailed) ===
    for (row in 0..4) {
        for (col in 0..6) {
            val scaleX = cx + direction * (size * 0.6f - col * size * 0.25f)
            val scaleY = bodyY - size * 0.6f + row * size * 0.3f
            // Check if scale is within body bounds (rough check)
            if (scaleX > cx - size * 1.0f && scaleX < cx + size * 0.9f) {
                drawCircle(
                    color = bodyDarkColor.copy(alpha = 0.15f),
                    radius = size * 0.12f,
                    center = Offset(scaleX, scaleY),
                    style = Stroke(1.5f)
                )
                // Inner highlight on scale
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = size * 0.07f,
                    center = Offset(scaleX - size * 0.03f, scaleY - size * 0.03f)
                )
            }
        }
    }

    // === TOP GLOSSY HIGHLIGHT (3D shine effect) ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.5f),
                Color.White.copy(alpha = 0.25f),
                Color.Transparent
            ),
            center = Offset(cx + direction * size * 0.3f, bodyY - size * 0.5f),
            radius = size * 0.8f
        ),
        topLeft = Offset(cx + direction * size * 0.1f, bodyY - size * 0.9f),
        size = Size(size * 0.8f, size * 0.6f)
    )

    // === DORSAL FIN (Top) ===
    val dorsalPath = Path().apply {
        moveTo(cx + direction * size * 0.1f, bodyY - size * 0.9f)
        cubicTo(
            cx - direction * size * 0.1f, bodyY - size * 1.6f,
            cx - direction * size * 0.4f, bodyY - size * 1.5f,
            cx - direction * size * 0.5f, bodyY - size * 1.0f
        )
        lineTo(cx - direction * size * 0.3f, bodyY - size * 0.8f)
        close()
    }
    drawPath(
        dorsalPath,
        brush = Brush.linearGradient(
            colors = listOf(finColor, finLightColor),
            start = Offset(cx, bodyY - size * 1.6f),
            end = Offset(cx, bodyY - size * 0.8f)
        )
    )
    // Dorsal fin rays
    for (i in 0..4) {
        val rayT = i / 4f
        val rayStart = Offset(
            cx + direction * size * (0.1f - rayT * 0.6f),
            bodyY - size * (0.9f + rayT * 0.1f)
        )
        val rayEnd = Offset(
            cx - direction * size * (0.1f + rayT * 0.4f),
            bodyY - size * (1.6f - rayT * 0.6f)
        )
        drawLine(
            color = bodyDarkColor.copy(alpha = 0.3f),
            start = rayStart,
            end = rayEnd,
            strokeWidth = 1.2f
        )
    }

    // === PECTORAL FINS (side fins) - Animated ===
    val pectoralPath = Path().apply {
        moveTo(cx + direction * size * 0.3f, bodyY + size * 0.2f)
        cubicTo(
            cx + direction * size * (0.2f + finFlap * 0.5f), bodyY + size * 0.6f,
            cx + direction * size * (0.3f + finFlap * 0.4f), bodyY + size * 0.9f,
            cx + direction * size * 0.5f, bodyY + size * 0.5f
        )
        close()
    }
    drawPath(pectoralPath, finColor.copy(alpha = 0.7f))
    // Pectoral rays
    for (i in 0..3) {
        val rayStart = Offset(cx + direction * size * 0.3f, bodyY + size * 0.2f)
        val rayEnd = Offset(
            cx + direction * size * (0.3f + finFlap * 0.4f + i * 0.05f),
            bodyY + size * (0.4f + i * 0.15f)
        )
        drawLine(
            color = bodyDarkColor.copy(alpha = 0.25f),
            start = rayStart,
            end = rayEnd,
            strokeWidth = 1f
        )
    }

    // Second pectoral (other side, partially visible)
    drawOval(
        color = finColor.copy(alpha = 0.4f),
        topLeft = Offset(cx + direction * size * 0.2f - finFlap * size * 0.3f, bodyY - size * 0.1f),
        size = Size(size * 0.35f, size * 0.6f)
    )

    // === VENTRAL FINS (bottom small fins) ===
    drawOval(
        color = finColor.copy(alpha = 0.6f),
        topLeft = Offset(cx + direction * size * 0.0f, bodyY + size * 0.7f),
        size = Size(size * 0.25f, size * 0.4f)
    )

    // === EYEBROW/CREST (golden arc above eye) ===
    val eyebrowPath = Path().apply {
        moveTo(cx + direction * size * 0.5f, bodyY - size * 0.5f)
        quadraticTo(
            cx + direction * size * 0.7f, bodyY - size * 0.6f,
            cx + direction * size * 0.85f, bodyY - size * 0.45f
        )
    }
    drawPath(
        eyebrowPath,
        brush = Brush.linearGradient(
            colors = listOf(accentYellow, accentOrange),
            start = Offset(cx + direction * size * 0.5f, bodyY - size * 0.5f),
            end = Offset(cx + direction * size * 0.85f, bodyY - size * 0.45f)
        ),
        style = Stroke(size * 0.15f, cap = StrokeCap.Round)
    )

    // === HUGE CARTOON EYES ===
    drawCartoonFishEye(
        cx, bodyY, size, direction, CartoonEyeStyle(
            offsetX = 0.65f, offsetY = -0.2f, radius = 0.45f, ovalStretch = 1.1f,
            irisRadiusFactor = 0.6f, pupilRadiusFactor = 0.35f,
            irisColors = listOf(Color(0xFFD946EF), Color(0xFF9333EA), Color(0xFF6B21A8)),
            outlineColor = bodyDarkColor.copy(alpha = 0.3f)
        )
    )

    // === SMILING MOUTH (large, open) ===
    val mouthPath = Path().apply {
        val mouthCenterX = cx + direction * size * 0.85f
        val mouthY = bodyY + size * 0.15f
        moveTo(mouthCenterX - size * 0.15f, mouthY)
        quadraticTo(
            mouthCenterX, mouthY + size * 0.2f,
            mouthCenterX + size * 0.15f, mouthY
        )
    }

    // Mouth opening (dark)
    drawPath(
        mouthPath,
        Color(0xFF2C1810),
        style = Stroke(size * 0.12f, cap = StrokeCap.Round)
    )

    // Tongue (pink, inside mouth)
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFF6B9D),
                Color(0xFFE91E63)
            ),
            center = Offset(cx + direction * size * 0.85f, bodyY + size * 0.22f),
            radius = size * 0.15f
        ),
        topLeft = Offset(cx + direction * size * 0.78f, bodyY + size * 0.15f),
        size = Size(size * 0.14f, size * 0.12f)
    )

    // Mouth outline/smile
    drawPath(
        mouthPath,
        bodyDarkColor.copy(alpha = 0.5f),
        style = Stroke(size * 0.08f, cap = StrokeCap.Round)
    )

    // === CHEEK DOTS (cute detail) ===
    drawCircle(
        Color(0xFFFF9ECD).copy(alpha = 0.3f),
        size * 0.12f,
        Offset(cx + direction * size * 0.55f, bodyY + size * 0.25f)
    )

    // === BODY OUTLINE (subtle, for definition) ===
    drawPath(
        bodyPath,
        bodyDarkColor.copy(alpha = 0.2f),
        style = Stroke(2f)
    )
}

// --- Realistic Clownfish (Orange with white stripes) ---
internal fun DrawScope.drawRealisticClownfish(
    cx: Float,
    cy: Float,
    size: Float,
    swimPhase: Float,
    index: Int,
    direction: Float = 1f
) {
    // Animazioni nuoto — swimPhase va 0→2π in 3s, perfettamente fluido
    val phaseOffset = index * 4.2f
    // Coda: oscillazione rapida (2× = intero, seamless)
    val tailWag = sin(swimPhase * 2f + phaseOffset) * 0.35f
    // Corpo: bobbing gentile
    val bodyBounce = sin(swimPhase + phaseOffset + 0.8f) * 0.06f
    // Pinne: battito (3× = intero, seamless)
    val finFlap = sin(swimPhase * 3f + phaseOffset) * 0.4f

    // Colors - Orange/Red with white stripes
    val bodyOrange = Color(0xFFFF6B35) // Bright orange
    val bodyOrangeDark = Color(0xFFD84315) // Dark orange/red
    val bodyOrangeLight = Color(0xFFFFB74D) // Light orange
    val whiteStripe = Color(0xFFFFFFFF) // White
    val blackOutline = Color(0xFF1A1A1A) // Black for outlines

    // Direction passed as parameter - fish faces the direction it's swimming
    val bodyY = cy + bodyBounce * size

    // === SHADOW ===
    drawOval(
        color = Color.Black.copy(alpha = 0.15f),
        topLeft = Offset(cx - size * 1.3f, bodyY + size * 0.9f),
        size = Size(size * 2.6f, size * 0.6f)
    )

    // === TAIL FIN ===
    val tailPath = Path().apply {
        val tailBase = cx - direction * size * 0.9f
        val tailSpread = size * 1.0f
        moveTo(tailBase, bodyY)
        cubicTo(
            tailBase - direction * size * 0.3f, bodyY - tailSpread * 0.5f + tailWag * size * 0.7f,
            tailBase - direction * size * 0.6f, bodyY - tailSpread * 0.6f + tailWag * size * 0.8f,
            tailBase - direction * size * 0.8f, bodyY - tailSpread * 0.4f + tailWag * size
        )
        lineTo(tailBase - direction * size * 0.7f, bodyY + tailWag * size * 0.2f)
        cubicTo(
            tailBase - direction * size * 0.6f, bodyY + tailSpread * 0.6f + tailWag * size * 0.8f,
            tailBase - direction * size * 0.3f, bodyY + tailSpread * 0.5f + tailWag * size * 0.7f,
            tailBase, bodyY
        )
        close()
    }

    // Tail - Orange with gradient
    drawPath(
        tailPath,
        brush = Brush.radialGradient(
            colors = listOf(bodyOrange, bodyOrangeDark),
            center = Offset(cx - direction * size * 0.9f, bodyY),
            radius = size * 1.2f
        )
    )

    // === MAIN BODY ===
    val bodyPath = Path().apply {
        moveTo(cx + direction * size * 0.95f, bodyY)
        cubicTo(
            cx + direction * size * 0.75f, bodyY - size * 0.85f,
            cx + direction * size * 0.15f, bodyY - size * 1.0f,
            cx - direction * size * 0.5f, bodyY - size * 0.75f
        )
        cubicTo(
            cx - direction * size * 0.85f, bodyY - size * 0.45f,
            cx - direction * size * 0.9f, bodyY - size * 0.15f,
            cx - direction * size * 0.9f, bodyY
        )
        cubicTo(
            cx - direction * size * 0.9f, bodyY + size * 0.15f,
            cx - direction * size * 0.85f, bodyY + size * 0.45f,
            cx - direction * size * 0.5f, bodyY + size * 0.75f
        )
        cubicTo(
            cx + direction * size * 0.15f, bodyY + size * 1.0f,
            cx + direction * size * 0.75f, bodyY + size * 0.85f,
            cx + direction * size * 0.95f, bodyY
        )
        close()
    }

    // Body gradient
    drawPath(
        bodyPath,
        brush = Brush.radialGradient(
            colors = listOf(
                bodyOrangeLight,
                bodyOrange,
                bodyOrangeDark
            ),
            center = Offset(cx + direction * size * 0.2f, bodyY - size * 0.2f),
            radius = size * 1.3f
        )
    )

    // === WHITE STRIPES (characteristic of clownfish) ===
    // Stripe 1 - Near head
    val stripe1Path = Path().apply {
        val stripeX = cx + direction * size * 0.45f
        val stripeWidth = size * 0.35f
        moveTo(stripeX - stripeWidth * 0.5f, bodyY - size * 0.75f)
        cubicTo(
            stripeX, bodyY - size * 0.85f,
            stripeX, bodyY - size * 0.85f,
            stripeX + stripeWidth * 0.5f, bodyY - size * 0.75f
        )
        lineTo(stripeX + stripeWidth * 0.45f, bodyY + size * 0.75f)
        cubicTo(
            stripeX, bodyY + size * 0.85f,
            stripeX, bodyY + size * 0.85f,
            stripeX - stripeWidth * 0.45f, bodyY + size * 0.75f
        )
        close()
    }
    drawPath(stripe1Path, whiteStripe)
    drawPath(stripe1Path, blackOutline.copy(alpha = 0.3f), style = Stroke(2f))

    // Stripe 2 - Middle
    val stripe2Path = Path().apply {
        val stripeX = cx + direction * size * 0.05f
        val stripeWidth = size * 0.3f
        moveTo(stripeX - stripeWidth * 0.5f, bodyY - size * 0.88f)
        cubicTo(
            stripeX, bodyY - size * 0.95f,
            stripeX, bodyY - size * 0.95f,
            stripeX + stripeWidth * 0.5f, bodyY - size * 0.88f
        )
        lineTo(stripeX + stripeWidth * 0.45f, bodyY + size * 0.88f)
        cubicTo(
            stripeX, bodyY + size * 0.95f,
            stripeX, bodyY + size * 0.95f,
            stripeX - stripeWidth * 0.45f, bodyY + size * 0.88f
        )
        close()
    }
    drawPath(stripe2Path, whiteStripe)
    drawPath(stripe2Path, blackOutline.copy(alpha = 0.3f), style = Stroke(2f))

    // Stripe 3 - Near tail
    val stripe3Path = Path().apply {
        val stripeX = cx - direction * size * 0.45f
        val stripeWidth = size * 0.25f
        moveTo(stripeX - stripeWidth * 0.5f, bodyY - size * 0.65f)
        lineTo(stripeX + stripeWidth * 0.5f, bodyY - size * 0.65f)
        lineTo(stripeX + stripeWidth * 0.45f, bodyY + size * 0.65f)
        lineTo(stripeX - stripeWidth * 0.45f, bodyY + size * 0.65f)
        close()
    }
    drawPath(stripe3Path, whiteStripe)
    drawPath(stripe3Path, blackOutline.copy(alpha = 0.3f), style = Stroke(2f))

    // === GLOSSY HIGHLIGHT ===
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.5f),
                Color.White.copy(alpha = 0.2f),
                Color.Transparent
            ),
            center = Offset(cx + direction * size * 0.25f, bodyY - size * 0.45f),
            radius = size * 0.7f
        ),
        topLeft = Offset(cx + direction * size * 0.05f, bodyY - size * 0.8f),
        size = Size(size * 0.7f, size * 0.5f)
    )

    // === DORSAL FIN ===
    val dorsalPath = Path().apply {
        moveTo(cx + direction * size * 0.15f, bodyY - size * 0.85f)
        cubicTo(
            cx - direction * size * 0.05f, bodyY - size * 1.5f,
            cx - direction * size * 0.35f, bodyY - size * 1.4f,
            cx - direction * size * 0.45f, bodyY - size * 0.95f
        )
        lineTo(cx - direction * size * 0.25f, bodyY - size * 0.75f)
        close()
    }
    drawPath(
        dorsalPath,
        brush = Brush.linearGradient(
            colors = listOf(bodyOrange, bodyOrangeDark),
            start = Offset(cx, bodyY - size * 1.5f),
            end = Offset(cx, bodyY - size * 0.75f)
        )
    )

    // === PECTORAL FINS ===
    val pectoralPath = Path().apply {
        moveTo(cx + direction * size * 0.35f, bodyY + size * 0.15f)
        cubicTo(
            cx + direction * size * (0.25f + finFlap * 0.5f), bodyY + size * 0.55f,
            cx + direction * size * (0.35f + finFlap * 0.4f), bodyY + size * 0.85f,
            cx + direction * size * 0.55f, bodyY + size * 0.45f
        )
        close()
    }
    drawPath(pectoralPath, bodyOrange.copy(alpha = 0.7f))

    // === HUGE CARTOON EYES ===
    drawCartoonFishEye(
        cx, bodyY, size, direction, CartoonEyeStyle(
            offsetX = 0.7f, offsetY = -0.15f, radius = 0.38f, ovalStretch = 1.05f,
            irisRadiusFactor = 0.55f, pupilRadiusFactor = 0.32f,
            irisColors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB), Color(0xFF1E40AF)),
            outlineColor = blackOutline.copy(alpha = 0.3f)
        )
    )

    // === SMILING MOUTH ===
    val mouthPath = Path().apply {
        val mouthCenterX = cx + direction * size * 0.88f
        val mouthY = bodyY + size * 0.12f
        moveTo(mouthCenterX - size * 0.12f, mouthY)
        quadraticTo(
            mouthCenterX, mouthY + size * 0.15f,
            mouthCenterX + size * 0.12f, mouthY
        )
    }

    drawPath(
        mouthPath,
        Color(0xFF2C1810),
        style = Stroke(size * 0.1f, cap = StrokeCap.Round)
    )

    drawPath(
        mouthPath,
        bodyOrangeDark.copy(alpha = 0.6f),
        style = Stroke(size * 0.06f, cap = StrokeCap.Round)
    )

    // === BODY OUTLINE ===
    drawPath(
        bodyPath,
        blackOutline.copy(alpha = 0.2f),
        style = Stroke(2f)
    )
}

// --- Realistic Turtle (Cartoon 3D, swimming motion) ---
internal fun DrawScope.drawRealisticTurtle(
    cx: Float,
    cy: Float,
    size: Float,
    time: Float,
    index: Int,
    direction: Float = 1f
) {

    val phaseOffset = index * 2.3f
    // Gentle body bob — turtle glides smoothly
    val bobY = sin(time * 0.8f + phaseOffset) * 0.04f
    val bodyY = cy + bobY * size

    val shellDark = Color(0xFF2E7D32)
    val shellMid = Color(0xFF4CAF50)
    val shellLight = Color(0xFFA5D6A7)
    val skin = Color(0xFF8BC34A)
    val skinLight = Color(0xFFC5E1A5)
    val skinDark = Color(0xFF558B2F)
    val eyeColor = Color(0xFF1A1A1A)

    // =======================
    // SWIMMING FLIPPER CYCLE
    // =======================
    // time is now a smooth slow phase (fishPhase2 ~83s cycle) — no jumps.
    // Front flippers do synchronized gentle strokes (like a sea turtle gliding).
    val strokePhase = time * 12f + phaseOffset
    // Front flippers — synchronized sweep
    val frontFlipperAngle = sin(strokePhase) * 0.55f
    // Back flippers — gentle paddle, slightly faster
    val backFlipperKick = sin(strokePhase * 1.3f) * 0.3f

    // =======================
    // SHADOW
    // =======================
    drawOval(
        color = Color.Black.copy(alpha = 0.08f),
        topLeft = Offset(cx - size * 0.9f, bodyY + size * 0.7f),
        size = Size(size * 1.8f, size * 0.3f)
    )

    // =======================
    // BACK FLIPPERS (small paddle fins)
    // =======================
    for (side in listOf(-1f, 1f)) {
        val kickAngle = backFlipperKick * side
        val bfBaseX = cx - direction * size * 0.55f
        val bfBaseY = bodyY + side * size * 0.25f

        val backFlipperPath = Path().apply {
            moveTo(bfBaseX, bfBaseY)
            quadraticTo(
                bfBaseX - direction * size * 0.35f,
                bfBaseY + side * size * (0.25f + kickAngle * 0.4f),
                bfBaseX - direction * size * 0.15f,
                bfBaseY + side * size * (0.4f + kickAngle * 0.3f)
            )
            quadraticTo(
                bfBaseX + direction * size * 0.05f,
                bfBaseY + side * size * 0.2f,
                bfBaseX, bfBaseY
            )
        }
        drawPath(
            backFlipperPath,
            brush = Brush.radialGradient(
                colors = listOf(skinLight, skin, skinDark),
                center = Offset(bfBaseX, bfBaseY),
                radius = size * 0.5f
            )
        )
        // Flipper outline
        drawPath(
            backFlipperPath,
            color = skinDark.copy(alpha = 0.4f),
            style = Stroke(width = size * 0.02f, cap = StrokeCap.Round)
        )
    }

    // =======================
    // FRONT FLIPPERS (side-view: extend up/down from shell, synchronized)
    // Drawn BEHIND shell
    // =======================
    for (side in listOf(-1f, 1f)) {
        // Both flippers move together (synchronized swimming)
        val flipAngle = frontFlipperAngle

        // Base: near the front edge of the shell
        val ffBaseX = cx + direction * size * 0.2f
        val ffBaseY = bodyY + side * size * 0.15f

        // Tip sweeps outward (up/down) with the stroke (+40%)
        val tipX = ffBaseX + direction * size * (0.46f + flipAngle * 0.21f)
        val tipY = ffBaseY + side * size * (0.81f + flipAngle * 0.35f)

        // Control points for a paddle shape (+40%)
        val cp1X = ffBaseX + direction * size * 0.73f
        val cp1Y = ffBaseY + side * size * (0.21f + flipAngle * 0.14f)
        val cp2X = ffBaseX + direction * size * 0.07f
        val cp2Y = ffBaseY + side * size * (0.63f + flipAngle * 0.21f)

        val frontFlipperPath = Path().apply {
            moveTo(ffBaseX, ffBaseY)
            quadraticTo(cp1X, cp1Y, tipX, tipY)
            quadraticTo(cp2X, cp2Y, ffBaseX, ffBaseY)
        }

        drawPath(
            frontFlipperPath,
            brush = Brush.radialGradient(
                colors = listOf(skinLight, skin, skinDark),
                center = Offset(ffBaseX, ffBaseY),
                radius = size * 0.6f
            )
        )
        drawPath(
            frontFlipperPath,
            color = skinDark.copy(alpha = 0.3f),
            style = Stroke(width = size * 0.02f, cap = StrokeCap.Round)
        )
    }

    // =======================
    // SHELL (dome, cartoon 3D)
    // =======================
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(shellLight, shellMid, shellDark),
            center = Offset(cx - size * 0.1f, bodyY - size * 0.25f),
            radius = size
        ),
        topLeft = Offset(cx - size * 0.8f, bodyY - size * 0.6f),
        size = Size(size * 1.6f, size * 1.2f)
    )

    // Shell pattern — hexagonal scute lines
    val patternColor = shellDark.copy(alpha = 0.2f)
    val patternStroke = Stroke(width = size * 0.025f, cap = StrokeCap.Round)
    // Center scute
    drawOval(
        color = patternColor,
        topLeft = Offset(cx - size * 0.35f, bodyY - size * 0.25f),
        size = Size(size * 0.7f, size * 0.55f),
        style = patternStroke
    )
    // Side scutes (small arcs)
    for (side in listOf(-1f, 1f)) {
        val arcPath = Path().apply {
            moveTo(cx + side * size * 0.35f, bodyY - size * 0.2f)
            quadraticTo(
                cx + side * size * 0.65f, bodyY,
                cx + side * size * 0.35f, bodyY + size * 0.25f
            )
        }
        drawPath(arcPath, patternColor, style = patternStroke)
    }

    // Shell highlight (glossy dome)
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.35f), Color.Transparent),
            center = Offset(cx - size * 0.25f, bodyY - size * 0.4f),
            radius = size * 0.55f
        ),
        topLeft = Offset(cx - size * 0.65f, bodyY - size * 0.65f),
        size = Size(size * 1.0f, size * 0.7f)
    )

    // Shell rim
    drawOval(
        color = shellDark.copy(alpha = 0.3f),
        topLeft = Offset(cx - size * 0.8f, bodyY - size * 0.6f),
        size = Size(size * 1.6f, size * 1.2f),
        style = Stroke(width = size * 0.03f)
    )

    // =======================
    // NECK (connects shell to head)
    // =======================
    val neckPath = Path().apply {
        moveTo(cx + direction * size * 0.7f, bodyY - size * 0.1f)
        quadraticTo(
            cx + direction * size * 0.9f, bodyY - size * 0.15f,
            cx + direction * size * 1.0f, bodyY - size * 0.05f
        )
        quadraticTo(
            cx + direction * size * 0.9f, bodyY + size * 0.12f,
            cx + direction * size * 0.7f, bodyY + size * 0.1f
        )
    }
    drawPath(neckPath, color = skin)

    // =======================
    // HEAD (cute round)
    // =======================
    val headCenterX = cx + direction * size * 1.05f
    val headCenterY = bodyY - size * 0.02f

    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(skinLight, skin, skinDark),
            center = Offset(headCenterX - direction * size * 0.05f, headCenterY - size * 0.05f),
            radius = size * 0.5f
        ),
        topLeft = Offset(
            headCenterX - size * 0.4f,
            headCenterY - size * 0.28f
        ),
        size = Size(size * 0.8f, size * 0.56f)
    )
    // Head highlight
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.25f), Color.Transparent),
            center = Offset(headCenterX - direction * size * 0.05f, headCenterY - size * 0.15f),
            radius = size * 0.25f
        ),
        topLeft = Offset(headCenterX - size * 0.3f, headCenterY - size * 0.25f),
        size = Size(size * 0.5f, size * 0.3f)
    )

    // =======================
    // EYE
    // =======================
    val eyeX = headCenterX + direction * size * 0.15f
    val eyeY = headCenterY - size * 0.04f

    // Eye white
    drawCircle(Color.White, size * 0.14f, Offset(eyeX, eyeY))
    // Iris
    drawCircle(
        Color(0xFF2E7D32),
        size * 0.09f,
        Offset(eyeX + direction * size * 0.02f, eyeY)
    )
    // Pupil
    drawCircle(
        eyeColor,
        size * 0.055f,
        Offset(eyeX + direction * size * 0.025f, eyeY + size * 0.005f)
    )
    // Eye shine
    drawCircle(
        Color.White.copy(alpha = 0.9f),
        size * 0.04f,
        Offset(eyeX + direction * size * 0.04f, eyeY - size * 0.04f)
    )

    // Cute tiny smile
    val smile = Path().apply {
        moveTo(headCenterX + direction * size * 0.08f, headCenterY + size * 0.16f)
        quadraticTo(
            headCenterX + direction * size * 0.28f,
            headCenterY + size * 0.24f,
            headCenterX + direction * size * 0.42f,
            headCenterY + size * 0.14f
        )
    }
    drawPath(
        smile,
        eyeColor.copy(alpha = 0.5f),
        style = Stroke(width = size * 0.025f, cap = StrokeCap.Round)
    )

    // Nostril dot
    drawCircle(
        skinDark.copy(alpha = 0.4f),
        size * 0.02f,
        Offset(headCenterX + direction * size * 0.32f, headCenterY + size * 0.02f)
    )
}

