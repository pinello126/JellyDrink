package com.jellydrink.app.domain.logic

import com.jellydrink.app.domain.model.DecorationInfo

object GameConstants {

    const val DEFAULT_GOAL = 2000
    val DEFAULT_GLASSES = listOf(200, 500, 1000)
    const val DAILY_LIMIT_ML = 10_000

    // Badge categories
    const val CAT_PRIMI_PASSI = "Primi Passi"
    const val CAT_STREAK = "Streak"
    const val CAT_LITRI = "Litri Totali"
    const val CAT_GIORNI = "Giorni Attivi"
    const val CAT_LIVELLI = "Livelli"
    const val CAT_SFIDE = "Sfide e Record"

    val BADGE_CATEGORIES_ORDER = listOf(
        CAT_PRIMI_PASSI, CAT_STREAK, CAT_LITRI, CAT_GIORNI, CAT_LIVELLI, CAT_SFIDE
    )

    val DECORATIONS = listOf(
        DecorationInfo("fish_blue", "Pesciolino Blu", 100),
        DecorationInfo("fish_orange", "Pesce Pagliaccio", 200),
        DecorationInfo("starfish", "Stella Marina", 80),
        DecorationInfo("coral_pink", "Corallo Rosa", 150),
        DecorationInfo("treasure", "Forziere", 300),
        DecorationInfo("turtle", "Tartaruga", 500),
        DecorationInfo("seahorse", "Cavalluccio", 250),
        DecorationInfo("crab", "Granchio", 120)
    )
}
