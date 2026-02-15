package com.jellydrink.app.ui.components

import androidx.compose.ui.graphics.Color

// === PALETTE OCEANO PROFONDO ===
internal val OceanTop = Color(0xFF030D1A)
internal val OceanMid1 = Color(0xFF061830)
internal val OceanMid2 = Color(0xFF0A2848)
internal val OceanMid3 = Color(0xFF0E3858)
internal val OceanMid4 = Color(0xFF124868)
internal val OceanMid5 = Color(0xFF185878)
internal val OceanBot1 = Color(0xFF1A6888)
internal val OceanBot2 = Color(0xFF1E7898)

internal val SandHighlight = Color(0xFFE8D8B0)
internal val SandMain = Color(0xFFD0B888)
internal val SandMid = Color(0xFFC0A870)
internal val SandDark = Color(0xFFA89058)
internal val SandDeep = Color(0xFF907848)

internal val SeaweedBright = Color(0xFF38B860)
internal val SeaweedMid = Color(0xFF289848)
internal val SeaweedDark = Color(0xFF1A7030)
internal val SeaweedOlive = Color(0xFF608838)

internal val CoralPink = Color(0xFFE08888)
internal val CoralOrange = Color(0xFFD8A060)
internal val RockDark = Color(0xFF586068)
internal val RockLight = Color(0xFF788088)
internal val ShellColor = Color(0xFFE8D0B8)

internal val BubbleWhite = Color(0xFFFFFFFF)
internal val LightRayColor = Color(0xFFFFFFFF)
internal val CausticColor = Color(0xFFB0E8FF)

// Decoration colors
internal val FishBlue = Color(0xFF2196F3)
internal val FishBlueDark = Color(0xFF1565C0)
internal val FishOrange = Color(0xFFFF5722)
internal val FishOrangeDark = Color(0xFFE64A19)
internal val FishWhite = Color(0xFFFFFFFF)
internal val StarfishOrange = Color(0xFFFF9800)
internal val StarfishDark = Color(0xFFE65100)
internal val TreasureGold = Color(0xFFFFD700)
internal val TreasureBrown = Color(0xFF8D6E63)
internal val TurtleGreen = Color(0xFF4CAF50)
internal val TurtleDark = Color(0xFF2E7D32)
internal val SeahorseYellow = Color(0xFFFFEB3B)
internal val SeahorseDark = Color(0xFFFBC02D)
internal val CrabRed = Color(0xFFF44336)
internal val CrabDark = Color(0xFFC62828)

data class Bubble(val x: Float, val baseY: Float, val radius: Float, val speed: Float, val phase: Float)
data class Seaweed(val x: Float, val height: Float, val width: Float, val phase: Float, val colorType: Int)
data class Particle(val x: Float, val y: Float, val size: Float, val speedX: Float, val speedY: Float, val phase: Float)
