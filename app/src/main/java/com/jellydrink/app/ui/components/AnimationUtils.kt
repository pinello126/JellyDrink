package com.jellydrink.app.ui.components

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlin.math.PI

/** Smooth 0→2π phase animation – the standard pattern used across all aquarium animations. */
@Composable
internal fun InfiniteTransition.smoothPhase(durationMs: Int, label: String): State<Float> =
    animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(durationMs, easing = LinearEasing), RepeatMode.Restart),
        label = label
    )
