package com.jellydrink.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beer_intake")
data class BeerIntakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,       // "yyyy-MM-dd"
    val amountCl: Int,      // centilitri
    val timestamp: Long
)
