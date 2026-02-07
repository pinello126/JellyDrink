package com.jellydrink.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,  // Singleton
    val xp: Int = 0,  // XP totali guadagnati - usati solo per calcolare il livello
    val spendableXp: Int = 0,  // XP spendibili nel negozio
    val level: Int = 1,
    val totalMlAllTime: Int = 0,
    val bestStreak: Int = 0,
    val activeDays: Int = 0,
    val dailyRecord: Int = 0,
    val lastActiveDate: String = ""
)
