package com.jellydrink.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jellyfish_collection")
data class JellyfishEntity(
    @PrimaryKey val id: String,  // "rosa", "lunar", "abyssal", etc.
    val nameIt: String,
    val unlocked: Boolean = false,
    val selected: Boolean = false,
    val unlockCondition: String,
    val dateUnlocked: String? = null,
    val cost: Int = 0  // Cost in XP to purchase
)
