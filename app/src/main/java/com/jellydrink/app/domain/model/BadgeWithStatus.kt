package com.jellydrink.app.domain.model

data class BadgeWithStatus(
    val type: String,
    val name: String,
    val description: String,
    val icon: String,
    val order: Int,
    val category: String,
    val isEarned: Boolean,
    val dateEarned: String?
)
