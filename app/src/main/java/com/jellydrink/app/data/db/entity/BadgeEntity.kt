package com.jellydrink.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,           // tipo badge univoco
    val dateEarned: String,     // data ottenimento "yyyy-MM-dd"
    val description: String     // descrizione badge
)
