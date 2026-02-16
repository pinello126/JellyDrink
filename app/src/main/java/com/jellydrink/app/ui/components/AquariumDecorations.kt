package com.jellydrink.app.ui.components
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.math.sin// --- Starfish (Realistic, on rock) ---
internal fun DrawScope.drawStarfish(cx: Float, cy: Float, size: Float) {
    // Palette — warm orange/coral tones
    val baseOrange = Color(0xFFE8722A)
    val brightOrange = Color(0xFFFF9040)
    val darkOrange = Color(0xFFC04E10)
    val deepOrange = Color(0xFF8B3508)
    val highlight = Color(0xFFFFBB80)
    val tubercle = Color(0xFFD45A18)
    val tubercleLight = Color(0xFFFFAA66)

    val armCount = 5
    // Slight rotation so it looks natural (not perfectly upright)
    val rotOffset = -0.2f

    // Shadow under the starfish (on rock surface)
    drawOval(
        color = Color.Black.copy(alpha = 0.18f),
        topLeft = Offset(cx - size * 0.85f, cy - size * 0.6f),
        size = Size(size * 1.7f, size * 1.3f)
    )

    // Draw each arm back-to-front
    for (i in 0 until armCount) {
        val angle = rotOffset + (i.toFloat() / armCount) * 2f * PI.toFloat() - PI.toFloat() / 2f
        val cosA = cos(angle)
        val sinA = sin(angle)

        // Perpendicular direction for arm width
        val perpX = -sinA
        val perpY = cosA

        // Arm tapers: wide at base, narrow at tip
        val armLen = size * (0.92f + (i % 2) * 0.06f) // slight length variation
        val baseWidth = size * 0.28f
        val tipWidth = size * 0.08f

        // Arm shape with organic curves (bezier)
        // Left edge of arm
        val baseL_X = cx + perpX * baseWidth
        val baseL_Y = cy + perpY * baseWidth
        val tipL_X = cx + cosA * armLen + perpX * tipWidth
        val tipL_Y = cy + sinA * armLen + perpY * tipWidth
        // Right edge of arm
        val baseR_X = cx - perpX * baseWidth
        val baseR_Y = cy - perpY * baseWidth
        val tipR_X = cx + cosA * armLen - perpX * tipWidth
        val tipR_Y = cy + sinA * armLen - perpY * tipWidth
        // Tip point (rounded)
        val tipX = cx + cosA * (armLen + tipWidth * 0.5f)
        val tipY = cy + sinA * (armLen + tipWidth * 0.5f)

        // Slight curve outward for organic look
        val bulgeFactor = 0.55f
        val midDist = armLen * bulgeFactor
        val bulge = size * 0.06f
        val ctrlL_X = cx + cosA * midDist + perpX * (baseWidth * 0.7f + bulge)
        val ctrlL_Y = cy + sinA * midDist + perpY * (baseWidth * 0.7f + bulge)
        val ctrlR_X = cx + cosA * midDist - perpX * (baseWidth * 0.7f + bulge)
        val ctrlR_Y = cy + sinA * midDist - perpY * (baseWidth * 0.7f + bulge)

        val armPath = Path().apply {
            moveTo(baseL_X, baseL_Y)
            quadraticTo(ctrlL_X, ctrlL_Y, tipL_X, tipL_Y)
            quadraticTo(tipX, tipY, tipR_X, tipR_Y)
            quadraticTo(ctrlR_X, ctrlR_Y, baseR_X, baseR_Y)
            close()
        }

        // Arm base fill — gradient from center (dark) to tip (bright)
        drawPath(
            armPath,
            brush = Brush.linearGradient(
                colors = listOf(darkOrange, baseOrange, brightOrange),
                start = Offset(cx, cy),
                end = Offset(tipX, tipY)
            )
        )

        // 3D shading: one side lighter, other darker
        val shadePath = Path().apply {
            moveTo(baseR_X, baseR_Y)
            quadraticTo(ctrlR_X, ctrlR_Y, tipR_X, tipR_Y)
            quadraticTo(tipX, tipY, tipL_X, tipL_Y)
            lineTo(cx + cosA * armLen * 0.5f, cy + sinA * armLen * 0.5f)
            close()
        }
        drawPath(shadePath, deepOrange.copy(alpha = 0.25f))

        // Highlight on the other side
        val hiPath = Path().apply {
            moveTo(baseL_X, baseL_Y)
            quadraticTo(ctrlL_X, ctrlL_Y, tipL_X, tipL_Y)
            lineTo(cx + cosA * armLen * 0.5f, cy + sinA * armLen * 0.5f)
            close()
        }
        drawPath(hiPath, highlight.copy(alpha = 0.15f))

        // Center ridge line down each arm
        drawLine(
            color = darkOrange.copy(alpha = 0.35f),
            start = Offset(cx + cosA * size * 0.15f, cy + sinA * size * 0.15f),
            end = Offset(cx + cosA * armLen * 0.9f, cy + sinA * armLen * 0.9f),
            strokeWidth = 1.2f,
            cap = StrokeCap.Round
        )
        // Ridge highlight
        drawLine(
            color = highlight.copy(alpha = 0.12f),
            start = Offset(cx + cosA * size * 0.15f + perpX * 1f, cy + sinA * size * 0.15f + perpY * 1f),
            end = Offset(cx + cosA * armLen * 0.85f + perpX * 0.5f, cy + sinA * armLen * 0.85f + perpY * 0.5f),
            strokeWidth = 0.7f,
            cap = StrokeCap.Round
        )

        // Tubercles (bumps) along the arm — 3-4 per arm
        for (t in 1..4) {
            val tFrac = t * 0.2f
            val tDist = armLen * tFrac
            val tWidth = baseWidth * (1f - tFrac * 0.65f) // narrows along arm
            val tX = cx + cosA * tDist
            val tY = cy + sinA * tDist
            val tR = size * (0.03f - t * 0.004f)

            // Left row of bumps
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(tubercleLight.copy(alpha = 0.5f), tubercle.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(tX + perpX * tWidth * 0.5f, tY + perpY * tWidth * 0.5f),
                    radius = tR * 1.5f
                ),
                radius = tR,
                center = Offset(tX + perpX * tWidth * 0.5f, tY + perpY * tWidth * 0.5f)
            )
            // Right row of bumps
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(tubercleLight.copy(alpha = 0.5f), tubercle.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(tX - perpX * tWidth * 0.5f, tY - perpY * tWidth * 0.5f),
                    radius = tR * 1.5f
                ),
                radius = tR,
                center = Offset(tX - perpX * tWidth * 0.5f, tY - perpY * tWidth * 0.5f)
            )
        }

        // Arm outline
        drawPath(armPath, deepOrange.copy(alpha = 0.3f), style = Stroke(0.8f))
    }

    // ---- CENTER DISC ----
    val centerR = size * 0.22f
    // Center body
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(baseOrange, darkOrange, deepOrange),
            center = Offset(cx - centerR * 0.15f, cy - centerR * 0.15f),
            radius = centerR * 1.3f
        ),
        radius = centerR,
        center = Offset(cx, cy)
    )
    // Center texture ring
    drawCircle(
        color = deepOrange.copy(alpha = 0.3f),
        radius = centerR * 0.7f,
        center = Offset(cx, cy),
        style = Stroke(0.8f)
    )
    // Center highlight
    drawCircle(
        brush = Brush.radialGradient(
            listOf(highlight.copy(alpha = 0.3f), Color.Transparent),
            center = Offset(cx - centerR * 0.2f, cy - centerR * 0.2f),
            radius = centerR * 0.6f
        ),
        radius = centerR * 0.5f,
        center = Offset(cx - centerR * 0.15f, cy - centerR * 0.15f)
    )
    // Center tubercles (small bumps around center)
    for (i in 0..4) {
        val bAngle = rotOffset + (i.toFloat() / 5f) * 2f * PI.toFloat()
        val bDist = centerR * 0.55f
        val bx = cx + cos(bAngle) * bDist
        val by = cy + sin(bAngle) * bDist
        drawCircle(
            brush = Brush.radialGradient(
                listOf(tubercleLight.copy(alpha = 0.4f), Color.Transparent),
                center = Offset(bx, by), radius = size * 0.03f
            ),
            radius = size * 0.02f,
            center = Offset(bx, by)
        )
    }
}

// --- Treasure Chest (Realistic, always open, with coin shimmer) ---
internal fun DrawScope.drawTreasureChest(cx: Float, cy: Float, size: Float, time: Float) {
    // Chest is ALWAYS OPEN. `time` drives coin shimmer & sparkle effects.

    // ======== DIMENSIONS ========
    val bodyW = size * 1.8f
    val bodyH = size * 0.95f
    val left = cx - bodyW / 2f
    val right = cx + bodyW / 2f
    val bottom = cy
    val bodyTop = bottom - bodyH
    val interiorH = size * 0.55f

    // ======== PALETTE ========
    val woodDarkest = Color(0xFF2E1810)
    val woodDark = Color(0xFF4A2E1A)
    val woodBase = Color(0xFF6B4226)
    val woodMid = Color(0xFF7E5535)
    val woodLight = Color(0xFF956840)
    val woodHi = Color(0xFFAA7D50)

    val goldDark = Color(0xFF8B6914)
    val gold = Color(0xFFB8941C)
    val goldBright = Color(0xFFD4A520)
    val goldShiny = Color(0xFFFFD700)
    val goldWhite = Color(0xFFFFE880)
    val goldPale = Color(0xFFFFF3C0)
    val interior = Color(0xFF0C0603)
    val interiorWall = Color(0xFF1A0E06)

    // ================================================================
    // 1. SHADOW
    // ================================================================
    drawOval(
        color = Color.Black.copy(alpha = 0.2f),
        topLeft = Offset(left - size * 0.1f, bottom - size * 0.04f),
        size = Size(bodyW + size * 0.2f, size * 0.22f)
    )

    // ================================================================
    // 2. LID (behind chest, tilted backward — always open)
    // ================================================================
    val lidH = size * 0.5f
    val lidW = bodyW + size * 0.06f
    val lidLeft = cx - lidW / 2f
    val lidRight = cx + lidW / 2f
    val lidBottom = bodyTop
    val lidTop = lidBottom - lidH * 0.85f

    // Lid inner face (dark underside)
    val lidInnerPath = Path().apply {
        moveTo(lidLeft, lidBottom)
        lineTo(lidLeft, lidTop + lidH * 0.3f)
        quadraticTo(lidLeft, lidTop, lidLeft + lidW * 0.15f, lidTop - lidH * 0.08f)
        quadraticTo(cx, lidTop - lidH * 0.2f, lidRight - lidW * 0.15f, lidTop - lidH * 0.08f)
        quadraticTo(lidRight, lidTop, lidRight, lidTop + lidH * 0.3f)
        lineTo(lidRight, lidBottom)
        close()
    }
    drawPath(
        lidInnerPath,
        brush = Brush.verticalGradient(
            colors = listOf(interiorWall, interior, interiorWall),
            startY = lidTop - lidH * 0.2f,
            endY = lidBottom
        )
    )

    // Lid back edge (wood visible at top rim)
    val lidBackPath = Path().apply {
        moveTo(lidLeft + size * 0.02f, lidTop + lidH * 0.22f)
        quadraticTo(cx, lidTop - lidH * 0.25f, lidRight - size * 0.02f, lidTop + lidH * 0.22f)
    }
    drawPath(
        lidBackPath,
        brush = Brush.linearGradient(
            colors = listOf(woodDark, woodMid, woodLight, woodHi, woodLight, woodMid, woodDark),
            start = Offset(lidLeft, lidTop),
            end = Offset(lidRight, lidTop)
        ),
        style = Stroke(size * 0.12f, cap = StrokeCap.Butt)
    )
    // Metal trim on lid arch
    drawPath(
        lidBackPath,
        brush = Brush.linearGradient(
            colors = listOf(goldDark, goldBright, goldShiny, goldBright, goldDark),
            start = Offset(lidLeft, lidTop),
            end = Offset(lidRight, lidTop)
        ),
        style = Stroke(size * 0.035f, cap = StrokeCap.Round)
    )
    // Lid clasp at top center
    val claspY = lidTop - lidH * 0.15f
    val claspPath = Path().apply {
        moveTo(cx - size * 0.06f, claspY + size * 0.08f)
        quadraticTo(cx, claspY - size * 0.05f, cx + size * 0.06f, claspY + size * 0.08f)
    }
    drawPath(
        claspPath,
        brush = Brush.linearGradient(
            listOf(goldDark, goldShiny, goldWhite, goldShiny, goldDark),
            start = Offset(cx - size * 0.06f, claspY),
            end = Offset(cx + size * 0.06f, claspY)
        ),
        style = Stroke(size * 0.035f, cap = StrokeCap.Round)
    )
    // Thin vertical bands on lid interior
    val lidBandXs = listOf(lidLeft + lidW * 0.25f, cx, lidLeft + lidW * 0.75f)
    for (bx in lidBandXs) {
        drawLine(
            color = goldDark.copy(alpha = 0.35f),
            start = Offset(bx, lidBottom),
            end = Offset(bx, lidTop + lidH * 0.15f),
            strokeWidth = size * 0.025f
        )
    }

    // ================================================================
    // 3. DARK INTERIOR
    // ================================================================
    drawRect(
        color = interior,
        topLeft = Offset(left + size * 0.04f, bodyTop - interiorH),
        size = Size(bodyW - size * 0.08f, interiorH + size * 0.02f)
    )
    // Side wall depth shading
    val wallW = size * 0.07f
    drawRect(
        brush = Brush.horizontalGradient(
            listOf(interiorWall, Color.Transparent),
            startX = left + size * 0.04f,
            endX = left + size * 0.04f + wallW
        ),
        topLeft = Offset(left + size * 0.04f, bodyTop - interiorH),
        size = Size(wallW, interiorH)
    )
    drawRect(
        brush = Brush.horizontalGradient(
            listOf(Color.Transparent, interiorWall),
            startX = right - size * 0.04f - wallW,
            endX = right - size * 0.04f
        ),
        topLeft = Offset(right - size * 0.04f - wallW, bodyTop - interiorH),
        size = Size(wallW, interiorH)
    )

    // ================================================================
    // 4. GOLD COINS + GEMS PILE + SHIMMER + SPARKLE
    // ================================================================
    val pileH = interiorH * 0.92f

    // Gem palette
    val rubyBase = Color(0xFFCC1133)
    val rubyBright = Color(0xFFFF2255)
    val rubyDark = Color(0xFF8B0022)
    val emeraldBase = Color(0xFF1B9E4B)
    val emeraldBright = Color(0xFF2EE070)
    val emeraldDark = Color(0xFF0D6630)
    val sapphireBase = Color(0xFF2255CC)
    val sapphireBright = Color(0xFF4488FF)
    val sapphireDark = Color(0xFF112266)
    val diamondBase = Color(0xFFCCDDFF)
    val diamondBright = Color(0xFFEEF4FF)
    val diamondDark = Color(0xFF8899BB)

    // Base pile mound shape
    val pilePath = Path().apply {
        moveTo(left + size * 0.1f, bodyTop)
        quadraticTo(left + bodyW * 0.25f, bodyTop - pileH * 1.05f, cx, bodyTop - pileH)
        quadraticTo(right - bodyW * 0.25f, bodyTop - pileH * 1.05f, right - size * 0.1f, bodyTop)
        close()
    }
    drawPath(
        pilePath,
        brush = Brush.verticalGradient(
            colors = listOf(goldShiny, goldBright, gold, goldDark),
            startY = bodyTop - pileH,
            endY = bodyTop
        )
    )
    // Pile highlight (top crescent)
    drawPath(
        Path().apply {
            moveTo(left + bodyW * 0.3f, bodyTop - pileH * 0.6f)
            quadraticTo(cx, bodyTop - pileH * 1.02f, right - bodyW * 0.3f, bodyTop - pileH * 0.6f)
            quadraticTo(cx, bodyTop - pileH * 0.82f, left + bodyW * 0.3f, bodyTop - pileH * 0.6f)
            close()
        },
        goldWhite.copy(alpha = 0.25f)
    )

    // ---- DETAILED COINS with thickness, emboss, shadow ----
    val coins = listOf(
        0.18f to 0.30f, 0.25f to 0.52f, 0.32f to 0.72f, 0.38f to 0.85f,
        0.44f to 0.93f, 0.50f to 0.98f, 0.56f to 0.94f, 0.62f to 0.86f,
        0.68f to 0.73f, 0.75f to 0.54f, 0.82f to 0.32f,
        0.30f to 0.42f, 0.42f to 0.68f, 0.50f to 0.80f, 0.58f to 0.70f,
        0.70f to 0.44f, 0.36f to 0.58f, 0.64f to 0.60f,
        0.46f to 0.78f, 0.54f to 0.82f, 0.28f to 0.62f, 0.72f to 0.63f,
        0.40f to 0.75f, 0.60f to 0.77f
    )
    for ((i, coin) in coins.withIndex()) {
        val (xFrac, hFrac) = coin
        val coinX = left + bodyW * xFrac
        val coinY = bodyTop - pileH * hFrac
        val coinR = size * (0.050f + (i % 3) * 0.008f)
        val tilt = (i % 4) * 0.08f // slight perspective variation

        // Shimmer wave
        val shimmer = (sin(time + i * 0.7f + coinX * 0.015f) * 0.5f + 0.5f)
        val hiColor = lerp(goldBright, goldPale, shimmer)
        val baseColor = lerp(gold, goldShiny, shimmer * 0.5f)

        // Coin thickness (dark edge below)
        drawOval(
            color = goldDark.copy(alpha = 0.6f),
            topLeft = Offset(coinX - coinR, coinY - coinR * (0.45f - tilt) + coinR * 0.12f),
            size = Size(coinR * 2f, coinR * (0.9f + tilt))
        )

        // Coin face (main ellipse with rich gradient)
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(hiColor, baseColor, goldDark),
                center = Offset(coinX - coinR * 0.2f, coinY - coinR * 0.12f),
                radius = coinR * 1.3f
            ),
            topLeft = Offset(coinX - coinR, coinY - coinR * (0.45f - tilt)),
            size = Size(coinR * 2f, coinR * (0.9f + tilt))
        )

        // Outer rim (raised edge)
        drawOval(
            color = goldDark.copy(alpha = 0.5f),
            topLeft = Offset(coinX - coinR, coinY - coinR * (0.45f - tilt)),
            size = Size(coinR * 2f, coinR * (0.9f + tilt)),
            style = Stroke(0.9f)
        )

        // Inner embossed circle (coin stamp)
        drawOval(
            color = lerp(goldDark, goldBright, shimmer * 0.3f).copy(alpha = 0.25f),
            topLeft = Offset(coinX - coinR * 0.55f, coinY - coinR * (0.25f - tilt * 0.5f)),
            size = Size(coinR * 1.1f, coinR * (0.5f + tilt * 0.5f)),
            style = Stroke(0.5f)
        )

        // Highlight crescent (top-left light catch)
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(goldPale.copy(alpha = shimmer * 0.6f), Color.Transparent),
                center = Offset(coinX - coinR * 0.3f, coinY - coinR * 0.2f),
                radius = coinR * 0.6f
            ),
            topLeft = Offset(coinX - coinR * 0.7f, coinY - coinR * 0.4f),
            size = Size(coinR * 0.8f, coinR * 0.4f)
        )
    }

    // ---- PRECIOUS GEMS scattered among coins ----
    // Each gem: (xFrac, hFrac, type) where type: 0=ruby, 1=emerald, 2=sapphire, 3=diamond
    data class Gem(val xFrac: Float, val hFrac: Float, val type: Int)
    val gems = listOf(
        Gem(0.35f, 0.88f, 0),  // ruby
        Gem(0.55f, 0.92f, 2),  // sapphire
        Gem(0.45f, 0.70f, 1),  // emerald
        Gem(0.65f, 0.78f, 3),  // diamond
        Gem(0.28f, 0.58f, 2),  // sapphire
        Gem(0.72f, 0.55f, 0),  // ruby
        Gem(0.50f, 0.60f, 1),  // emerald
        Gem(0.60f, 0.68f, 3),  // diamond
        Gem(0.40f, 0.48f, 0),  // ruby
    )

    for ((i, gem) in gems.withIndex()) {
        val gx = left + bodyW * gem.xFrac
        val gy = bodyTop - pileH * gem.hFrac
        val gemR = size * (0.04f + (i % 2) * 0.012f)
        val gemShimmer = sin(time + i * 0.9f + 2.5f).coerceAtLeast(0f)

        val (gemDark, gemBase, gemBright) = when (gem.type) {
            0 -> Triple(rubyDark, rubyBase, rubyBright)
            1 -> Triple(emeraldDark, emeraldBase, emeraldBright)
            2 -> Triple(sapphireDark, sapphireBase, sapphireBright)
            else -> Triple(diamondDark, diamondBase, diamondBright)
        }

        // Gem shadow
        drawOval(
            color = Color.Black.copy(alpha = 0.25f),
            topLeft = Offset(gx - gemR * 0.8f, gy + gemR * 0.05f),
            size = Size(gemR * 1.6f, gemR * 0.7f)
        )

        // Gem body — faceted look with radial gradient
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    lerp(gemBright, Color.White, gemShimmer * 0.5f),
                    gemBright,
                    gemBase,
                    gemDark
                ),
                center = Offset(gx - gemR * 0.15f, gy - gemR * 0.1f),
                radius = gemR * 1.2f
            ),
            topLeft = Offset(gx - gemR * 0.8f, gy - gemR * 0.5f),
            size = Size(gemR * 1.6f, gemR)
        )

        // Gem outline
        drawOval(
            color = gemDark.copy(alpha = 0.6f),
            topLeft = Offset(gx - gemR * 0.8f, gy - gemR * 0.5f),
            size = Size(gemR * 1.6f, gemR),
            style = Stroke(0.7f)
        )

        // Facet lines (give diamond-cut look)
        // Horizontal facet
        drawLine(
            color = gemDark.copy(alpha = 0.2f),
            start = Offset(gx - gemR * 0.55f, gy),
            end = Offset(gx + gemR * 0.55f, gy),
            strokeWidth = 0.4f
        )
        // Diagonal facets
        drawLine(
            color = gemDark.copy(alpha = 0.15f),
            start = Offset(gx, gy - gemR * 0.35f),
            end = Offset(gx - gemR * 0.5f, gy + gemR * 0.1f),
            strokeWidth = 0.4f
        )
        drawLine(
            color = gemDark.copy(alpha = 0.15f),
            start = Offset(gx, gy - gemR * 0.35f),
            end = Offset(gx + gemR * 0.5f, gy + gemR * 0.1f),
            strokeWidth = 0.4f
        )

        // Bright highlight (light catch on facet)
        drawOval(
            brush = Brush.radialGradient(
                listOf(Color.White.copy(alpha = 0.7f + gemShimmer * 0.3f), Color.Transparent),
                center = Offset(gx - gemR * 0.2f, gy - gemR * 0.18f),
                radius = gemR * 0.4f
            ),
            topLeft = Offset(gx - gemR * 0.45f, gy - gemR * 0.35f),
            size = Size(gemR * 0.5f, gemR * 0.3f)
        )

        // Secondary smaller highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.3f + gemShimmer * 0.2f),
            radius = gemR * 0.1f,
            center = Offset(gx + gemR * 0.25f, gy + gemR * 0.08f)
        )
    }

    // ---- SPARKLE EFFECTS on coins AND gems ----
    val sparkles = listOf(
        0.30f to 0.70f, 0.45f to 0.92f, 0.55f to 0.85f,
        0.65f to 0.68f, 0.50f to 0.75f, 0.38f to 0.55f,
        0.60f to 0.55f, 0.48f to 0.65f,
        // Sparkles on gems
        0.35f to 0.88f, 0.55f to 0.92f, 0.65f to 0.78f, 0.45f to 0.70f
    )
    for ((i, sparkle) in sparkles.withIndex()) {
        val (xFrac, hFrac) = sparkle
        val sx = left + bodyW * xFrac
        val sy = bodyTop - pileH * hFrac

        val raw = sin(time + i * 1.047f).coerceAtLeast(0f)
        val intensity = raw * raw * raw

        if (intensity > 0.05f) {
            val sparkR = size * 0.04f * intensity
            // Main cross
            drawLine(
                color = Color.White.copy(alpha = intensity * 0.85f),
                start = Offset(sx - sparkR * 2.5f, sy),
                end = Offset(sx + sparkR * 2.5f, sy),
                strokeWidth = 1.5f * intensity + 0.5f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = intensity * 0.85f),
                start = Offset(sx, sy - sparkR * 2.5f),
                end = Offset(sx, sy + sparkR * 2.5f),
                strokeWidth = 1.5f * intensity + 0.5f,
                cap = StrokeCap.Round
            )
            // Diagonal arms
            drawLine(
                color = Color.White.copy(alpha = intensity * 0.4f),
                start = Offset(sx - sparkR * 1.5f, sy - sparkR * 1.5f),
                end = Offset(sx + sparkR * 1.5f, sy + sparkR * 1.5f),
                strokeWidth = 1f * intensity + 0.3f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White.copy(alpha = intensity * 0.4f),
                start = Offset(sx + sparkR * 1.5f, sy - sparkR * 1.5f),
                end = Offset(sx - sparkR * 1.5f, sy + sparkR * 1.5f),
                strokeWidth = 1f * intensity + 0.3f,
                cap = StrokeCap.Round
            )
            // Center glow
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.White.copy(alpha = intensity * 0.9f), goldPale.copy(alpha = intensity * 0.3f), Color.Transparent),
                    center = Offset(sx, sy), radius = sparkR * 2f
                ),
                radius = sparkR * 2f, center = Offset(sx, sy)
            )
        }
    }

    // ---- GEM-COLORED SPARKLES (colored flashes on gems) ----
    val gemSparkles = listOf(
        Triple(0.35f, 0.88f, rubyBright),
        Triple(0.55f, 0.92f, sapphireBright),
        Triple(0.45f, 0.70f, emeraldBright),
        Triple(0.65f, 0.78f, diamondBright),
        Triple(0.50f, 0.60f, emeraldBright),
        Triple(0.60f, 0.68f, diamondBright),
    )
    for ((i, gs) in gemSparkles.withIndex()) {
        val (xFrac, hFrac, gemColor) = gs
        val gsx = left + bodyW * xFrac
        val gsy = bodyTop - pileH * hFrac

        // Offset phase so gem sparkles don't sync with gold sparkles
        val raw = sin(time + i * 1.3f + PI.toFloat() * 0.5f).coerceAtLeast(0f)
        val intensity = raw * raw * raw * raw // pow(4) — even sharper

        if (intensity > 0.08f) {
            val sR = size * 0.035f * intensity
            // Colored glow
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(gemColor.copy(alpha = intensity * 0.8f), gemColor.copy(alpha = intensity * 0.2f), Color.Transparent),
                    center = Offset(gsx, gsy), radius = sR * 3f
                ),
                radius = sR * 3f, center = Offset(gsx, gsy)
            )
            // White core
            drawCircle(
                color = Color.White.copy(alpha = intensity * 0.9f),
                radius = sR * 0.8f, center = Offset(gsx, gsy)
            )
        }
    }

    // Gold ambient glow (pulsing slightly)
    val glowPulse = sin(time) * 0.04f + 0.18f
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(
                goldShiny.copy(alpha = glowPulse),
                goldShiny.copy(alpha = glowPulse * 0.3f),
                Color.Transparent
            ),
            center = Offset(cx, bodyTop - pileH * 0.4f),
            radius = bodyW * 0.55f
        ),
        topLeft = Offset(cx - bodyW * 0.55f, bodyTop - pileH - size * 0.3f),
        size = Size(bodyW * 1.1f, pileH + size * 0.5f)
    )

    // ================================================================
    // 5. CHEST BODY FRONT FACE
    // ================================================================
    val bodyCorner = size * 0.04f
    val frontPath = Path().apply {
        moveTo(left + bodyCorner, bodyTop)
        lineTo(right - bodyCorner, bodyTop)
        quadraticTo(right, bodyTop, right, bodyTop + bodyCorner)
        lineTo(right, bottom - bodyCorner)
        quadraticTo(right, bottom, right - bodyCorner, bottom)
        lineTo(left + bodyCorner, bottom)
        quadraticTo(left, bottom, left, bottom - bodyCorner)
        lineTo(left, bodyTop + bodyCorner)
        quadraticTo(left, bodyTop, left + bodyCorner, bodyTop)
        close()
    }
    // Wood with 3D horizontal shading (lighter center, darker edges)
    drawPath(
        frontPath,
        brush = Brush.horizontalGradient(
            colors = listOf(woodDark, woodBase, woodMid, woodLight, woodMid, woodBase, woodDark),
            startX = left,
            endX = right
        )
    )
    // Vertical gradient overlay for depth
    drawPath(
        frontPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent, Color.Black.copy(alpha = 0.1f)),
            startY = bodyTop,
            endY = bottom
        )
    )

    // ================================================================
    // 6. WOOD PLANK LINES
    // ================================================================
    val plankCount = 4
    for (i in 1 until plankCount) {
        val py = bodyTop + bodyH * i / plankCount
        drawLine(woodDarkest.copy(alpha = 0.3f), Offset(left + size * 0.03f, py), Offset(right - size * 0.03f, py), 0.8f)
        drawLine(woodHi.copy(alpha = 0.07f), Offset(left + size * 0.03f, py + 1.2f), Offset(right - size * 0.03f, py + 1.2f), 0.5f)
    }
    // Subtle grain
    for (i in 0..5) {
        val gy = bodyTop + bodyH * 0.12f + i * bodyH * 0.15f
        val gx1 = left + bodyW * (0.08f + (i % 3) * 0.12f)
        val gx2 = gx1 + bodyW * (0.12f + (i % 2) * 0.08f)
        drawLine(woodDarkest.copy(alpha = 0.07f), Offset(gx1, gy), Offset(gx2, gy + size * 0.01f), 0.5f)
    }

    // ================================================================
    // 7. METAL BANDS (thin & subtle)
    // ================================================================
    val bandW = size * 0.04f
    val bandXs = listOf(left + bodyW * 0.22f, left + bodyW * 0.78f)
    for (bx in bandXs) {
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(goldDark, gold, goldBright, goldShiny, goldBright, gold, goldDark),
                startX = bx - bandW / 2f,
                endX = bx + bandW / 2f
            ),
            topLeft = Offset(bx - bandW / 2f, bodyTop),
            size = Size(bandW, bodyH)
        )
        // Rivets
        for (ry in listOf(bodyTop + bodyH * 0.25f, bodyTop + bodyH * 0.5f, bodyTop + bodyH * 0.75f)) {
            drawCircle(
                brush = Brush.radialGradient(listOf(goldWhite, goldShiny, goldDark), center = Offset(bx, ry), radius = size * 0.025f),
                radius = size * 0.018f,
                center = Offset(bx, ry)
            )
        }
    }
    // Horizontal bands (top & bottom trim)
    val hBandH = size * 0.045f
    for (hy in listOf(bodyTop, bottom - hBandH)) {
        drawRect(
            brush = Brush.verticalGradient(
                listOf(goldDark, gold, goldBright, goldShiny, goldBright, gold, goldDark),
                startY = hy, endY = hy + hBandH
            ),
            topLeft = Offset(left, hy),
            size = Size(bodyW, hBandH)
        )
    }

    // ================================================================
    // 8. CORNER BRACKETS (small L-shapes)
    // ================================================================
    val cS = size * 0.1f
    val cT = size * 0.03f
    for ((cxP, cyP) in listOf(
        left to bodyTop, right - cS to bodyTop,
        left to bottom - cS, right - cS to bottom - cS
    )) {
        drawRect(
            brush = Brush.verticalGradient(listOf(goldDark, goldShiny, goldDark), startY = cyP, endY = cyP + cT),
            topLeft = Offset(cxP, cyP), size = Size(cS, cT)
        )
        drawRect(
            brush = Brush.horizontalGradient(listOf(goldDark, goldShiny, goldDark), startX = cxP, endX = cxP + cT),
            topLeft = Offset(cxP, cyP), size = Size(cT, cS)
        )
        drawCircle(goldWhite, size * 0.013f, Offset(cxP + cT * 0.6f, cyP + cT * 0.6f))
    }

    // ================================================================
    // 9. LOCK / KEYHOLE
    // ================================================================
    val lockX = cx
    val lockY = bodyTop + bodyH * 0.45f
    val lockR = size * 0.08f
    // Circular lock plate
    drawCircle(
        brush = Brush.radialGradient(
            listOf(goldWhite, goldShiny, goldBright, goldDark),
            center = Offset(lockX - lockR * 0.15f, lockY - lockR * 0.15f),
            radius = lockR * 1.5f
        ),
        radius = lockR, center = Offset(lockX, lockY)
    )
    drawCircle(goldDark.copy(alpha = 0.5f), lockR, Offset(lockX, lockY), style = Stroke(0.8f))
    // Keyhole
    drawCircle(interior, lockR * 0.3f, Offset(lockX, lockY - lockR * 0.08f))
    drawRect(
        color = interior,
        topLeft = Offset(lockX - lockR * 0.1f, lockY + lockR * 0.05f),
        size = Size(lockR * 0.2f, lockR * 0.4f)
    )

    // ================================================================
    // 10. SIDE HANDLE
    // ================================================================
    val handleX = left + size * 0.03f
    val handleY = bodyTop + bodyH * 0.45f
    val handlePath = Path().apply {
        moveTo(handleX, handleY - size * 0.05f)
        quadraticTo(handleX - size * 0.1f, handleY, handleX, handleY + size * 0.05f)
    }
    drawPath(handlePath, goldBright, style = Stroke(size * 0.03f, cap = StrokeCap.Round))
    drawCircle(goldShiny, size * 0.022f, Offset(handleX, handleY - size * 0.05f))
    drawCircle(goldShiny, size * 0.022f, Offset(handleX, handleY + size * 0.05f))

    // ================================================================
    // 11. OVERFLOW & SCATTERED COINS
    // ================================================================
    // Coins sitting on front edge
    val overflowCoins = listOf(-0.28f, -0.08f, 0.06f, 0.22f, 0.38f)
    for ((i, dx) in overflowCoins.withIndex()) {
        val ocX = cx + dx * size
        val ocY = bodyTop + size * 0.01f * (i % 2)
        val ocR = size * 0.045f
        val shimmer = sin(time + i * 1.3f) * 0.5f + 0.5f
        drawOval(
            brush = Brush.radialGradient(
                listOf(lerp(goldBright, goldPale, shimmer), goldBright, goldDark),
                center = Offset(ocX, ocY), radius = ocR
            ),
            topLeft = Offset(ocX - ocR, ocY - ocR * 0.45f),
            size = Size(ocR * 2f, ocR * 0.9f)
        )
    }
    // Scattered on ground
    val groundCoins = listOf(-0.55f to 0.1f, -0.35f to 0.14f, 0.42f to 0.08f, 0.62f to 0.12f, 0.12f to 0.16f)
    for ((dx, dy) in groundCoins) {
        val gcX = cx + dx * size
        val gcY = bottom + dy * size
        val gcR = size * 0.04f
        drawOval(
            brush = Brush.radialGradient(listOf(goldShiny, goldBright, goldDark), center = Offset(gcX, gcY), radius = gcR),
            topLeft = Offset(gcX - gcR, gcY - gcR * 0.4f),
            size = Size(gcR * 2f, gcR * 0.8f)
        )
    }

    // Body outline
    drawPath(frontPath, woodDarkest.copy(alpha = 0.35f), style = Stroke(1.2f))
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
