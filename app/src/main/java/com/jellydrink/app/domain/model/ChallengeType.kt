package com.jellydrink.app.domain.model

data class ChallengeType(
    val id: String,
    val description: String,
    val target: Int,
    val xpReward: Int
)
