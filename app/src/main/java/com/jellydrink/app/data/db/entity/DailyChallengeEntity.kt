package com.jellydrink.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_challenges")
data class DailyChallengeEntity(
    @PrimaryKey val date: String,  // yyyy-MM-dd
    val type: String,
    val targetValue: Int,
    val currentProgress: Int = 0,
    val completed: Boolean = false,
    val xpReward: Int = 30
)
