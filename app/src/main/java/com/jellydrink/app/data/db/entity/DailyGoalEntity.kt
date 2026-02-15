package com.jellydrink.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goal")
data class DailyGoalEntity(
    @PrimaryKey val date: String,   // "yyyy-MM-dd"
    val goalMl: Int
)
