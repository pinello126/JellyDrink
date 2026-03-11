package com.jellydrink.app.domain.model

data class BadgeDefinition(
    val type: String,
    val name: String,
    val description: String,
    val icon: String,
    val order: Int,
    val category: String
)
