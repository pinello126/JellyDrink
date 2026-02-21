package com.jellydrink.app.ui.components
import androidx.compose.ui.graphics.Color// ═══════════════════════════════════════════════════════════════════
//  PALETTE — Cartoon premium multi-layer, Fishdom / Splash Fish
// ═══════════════════════════════════════════════════════════════════

// Outer glow shell
internal val OuterGlow = Color(0xFFFFE0F0)
// Body layers (from highlight to deep shadow)
internal val BodyHL1 = Color(0xFFFFF0F6)    // quasi bianco-rosa
internal val BodyHL2 = Color(0xFFFFD8EA)    // rosa pallido
internal val BodyMain = Color(0xFFFFACD0)   // rosa principale
internal val BodyDeep = Color(0xFFEE80B0)   // rosa intenso
internal val BodyShadow = Color(0xFFD06090) // ombra rosa scuro
internal val BodyDark = Color(0xFFB04878)   // ombra profonda

// Rim light (controluce)
internal val RimLight = Color(0xFFFFE8F8)

// Macchie
internal val Spot1 = Color(0x40E060A0)
internal val Spot2 = Color(0x30F088B8)

// Bordo smerlato
internal val ScallopHL = Color(0xFFFFD0E0)
internal val ScallopShadow = Color(0xFFCC6898)

// Acqua riempimento
internal val WaterSurf = Color(0xFFA8F0FF)
internal val WaterMid = Color(0xFF68D8F0)
internal val WaterDeep = Color(0xFF28A8D0)
internal val WaterFloor = Color(0xFF1890B8)

// Occhi
internal val EyeWhite = Color(0xFFFFFFFF)
internal val EyeShadowTop = Color(0x28884068)
internal val IrisOuter = Color(0xFF2828A0)
internal val IrisMid = Color(0xFF4848D0)
internal val IrisInner = Color(0xFF6868E8)
internal val PupilColor = Color(0xFF080828)
internal val EyeShine = Color(0xFFFFFFFF)

// Faccia
internal val CheekColor = Color(0xFFFF7898)
internal val MouthColor = Color(0xFFA03860)
internal val MouthFill = Color(0xFF802848)

// Tentacoli — braccia orali (spesse) e filamenti (sottili)
internal val ArmLight = Color(0xFFFFB8D0)
internal val ArmMid = Color(0xFFE890B0)
internal val ArmDark = Color(0xFFD07098)
internal val FilLight = Color(0xB0FFC0D8)
internal val FilDark = Color(0x80D080A8)

// Contorno
internal val Outline = Color(0xFF904868)

// Effetti
internal val GlowGold = Color(0xFFFFD060)
internal val BubbleColor = Color(0xCCFFFFFF)

// ═══════════════════════════════════════════════════════════════════
//  SPECIES COLOR PALETTES
// ═══════════════════════════════════════════════════════════════════
data class JellyfishPalette(
    val bodyHL1: Color,
    val bodyHL2: Color,
    val bodyMain: Color,
    val bodyDeep: Color,
    val bodyShadow: Color,
    val bodyDark: Color,
    val outerGlow: Color,
    val glowGold: Color
)

internal val PaletteRosa = JellyfishPalette(
    bodyHL1 = Color(0xFFFFF0F6),
    bodyHL2 = Color(0xFFFFD8EA),
    bodyMain = Color(0xFFFFACD0),
    bodyDeep = Color(0xFFEE80B0),
    bodyShadow = Color(0xFFD06090),
    bodyDark = Color(0xFFB04878),
    outerGlow = Color(0xFFFFE0F0),
    glowGold = Color(0xFFFFD060)
)


// ═══════════════════════════════════════════════════════════════════
//  JELLYFISH CONFIGURATION
// ═══════════════════════════════════════════════════════════════════
internal val scale = 0.7f
internal val tentacleCount = 5
internal val filamentCount = 8
internal val showEyes = true
internal val showMouth = true
internal val glowIntensity = 1.0f
