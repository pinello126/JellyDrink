package com.jellydrink.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_intake")
data class WaterIntakeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,       // formato "yyyy-MM-dd"
    val amountMl: Int,      // quantita' in millilitri
    val timestamp: Long     // System.currentTimeMillis()
)
