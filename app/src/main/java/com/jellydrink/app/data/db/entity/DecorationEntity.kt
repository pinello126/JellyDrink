package com.jellydrink.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decorations")
data class DecorationEntity(
    @PrimaryKey val id: String,
    val nameIt: String,
    val cost: Int,
    val owned: Boolean = false,
    val placed: Boolean = false
)
