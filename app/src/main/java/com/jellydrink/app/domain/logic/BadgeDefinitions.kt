package com.jellydrink.app.domain.logic

import com.jellydrink.app.domain.model.BadgeDefinition
import com.jellydrink.app.domain.logic.GameConstants.CAT_GIORNI
import com.jellydrink.app.domain.logic.GameConstants.CAT_LITRI
import com.jellydrink.app.domain.logic.GameConstants.CAT_LIVELLI
import com.jellydrink.app.domain.logic.GameConstants.CAT_PRIMI_PASSI
import com.jellydrink.app.domain.logic.GameConstants.CAT_SFIDE
import com.jellydrink.app.domain.logic.GameConstants.CAT_STREAK

object BadgeDefinitions {

    val ALL_BADGES = listOf(
        // Primi passi
        BadgeDefinition("first_sip", "Primo Sorso", "Hai registrato il tuo primo consumo d'acqua", "\uD83D\uDCA7", 1, CAT_PRIMI_PASSI),
        BadgeDefinition("daily_goal", "Obiettivo Raggiunto", "Hai completato il tuo obiettivo giornaliero", "\uD83C\uDFAF", 2, CAT_PRIMI_PASSI),

        // Streak
        BadgeDefinition("streak_3", "Streak 3", "Tre giorni consecutivi al 100%", "\uD83D\uDD25", 3, CAT_STREAK),
        BadgeDefinition("streak_7", "Streak 7", "Una settimana intera al 100%", "\uD83D\uDD25", 4, CAT_STREAK),
        BadgeDefinition("streak_14", "Streak 14", "Due settimane consecutive al 100%", "\uD83D\uDD25", 5, CAT_STREAK),
        BadgeDefinition("streak_30", "Streak 30", "Un mese intero al 100%", "\uD83D\uDD25", 6, CAT_STREAK),
        BadgeDefinition("streak_100", "Streak 100", "Cento giorni consecutivi al 100%! Leggendario!", "\uD83D\uDD25", 7, CAT_STREAK),

        // Litri totali
        BadgeDefinition("liters_10", "10 Litri", "Hai bevuto 10 litri d'acqua in totale", "\uD83D\uDCA6", 8, CAT_LITRI),
        BadgeDefinition("liters_50", "50 Litri", "Hai bevuto 50 litri d'acqua in totale", "\uD83D\uDCA6", 9, CAT_LITRI),
        BadgeDefinition("liters_100", "100 Litri", "Hai bevuto 100 litri d'acqua in totale", "\uD83D\uDCA6", 10, CAT_LITRI),
        BadgeDefinition("liters_500", "500 Litri", "Hai bevuto 500 litri d'acqua in totale", "\uD83D\uDCA6", 11, CAT_LITRI),
        BadgeDefinition("liters_1000", "1000 Litri", "Hai bevuto 1000 litri d'acqua! Incredibile!", "\uD83D\uDCA6", 12, CAT_LITRI),

        // Giorni attivi
        BadgeDefinition("active_7", "7 Giorni Attivi", "Hai registrato acqua per 7 giorni", "\uD83D\uDCC5", 13, CAT_GIORNI),
        BadgeDefinition("active_30", "30 Giorni Attivi", "Hai registrato acqua per 30 giorni", "\uD83D\uDCC5", 14, CAT_GIORNI),
        BadgeDefinition("active_100", "100 Giorni Attivi", "Hai registrato acqua per 100 giorni", "\uD83D\uDCC5", 15, CAT_GIORNI),
        BadgeDefinition("active_365", "1 Anno Attivo", "Hai registrato acqua per 365 giorni!", "\uD83D\uDCC5", 16, CAT_GIORNI),

        // Livelli
        BadgeDefinition("level_5", "Livello 5", "Hai raggiunto il livello 5", "\u2B50", 17, CAT_LIVELLI),
        BadgeDefinition("level_10", "Livello 10", "Hai raggiunto il livello 10", "\u2B50", 18, CAT_LIVELLI),
        BadgeDefinition("level_20", "Livello 20", "Hai raggiunto il livello 20", "\u2B50", 19, CAT_LIVELLI),
        BadgeDefinition("level_50", "Livello 50", "Hai raggiunto il livello 50!", "\u2B50", 20, CAT_LIVELLI),

        // Sfide e Record
        BadgeDefinition("challenges_10", "10 Sfide", "Hai completato 10 sfide giornaliere", "\uD83C\uDFC6", 21, CAT_SFIDE),
        BadgeDefinition("challenges_50", "50 Sfide", "Hai completato 50 sfide giornaliere", "\uD83C\uDFC6", 22, CAT_SFIDE),
        BadgeDefinition("challenges_100", "100 Sfide", "Hai completato 100 sfide giornaliere", "\uD83C\uDFC6", 23, CAT_SFIDE),
        BadgeDefinition("challenges_150", "150 Sfide", "Hai completato 150 sfide giornaliere", "\uD83C\uDFC6", 24, CAT_SFIDE),
        BadgeDefinition("challenges_200", "200 Sfide", "Hai completato 200 sfide giornaliere", "\uD83C\uDFC6", 25, CAT_SFIDE)
    )

    /**
     * Data necessaria per valutare i badge (raccolta dal repository).
     */
    data class BadgeCheckData(
        val totalEntries: Int,
        val currentTotalMl: Int,
        val goal: Int,
        val streak: Int,
        val totalMlAllTime: Int,
        val activeDays: Int,
        val level: Int,
        val completedChallenges: Int,
        val earnedBadgeTypes: Set<String>
    )

    /**
     * Controlla quali badge nuovi possono essere assegnati.
     * Restituisce il primo badge non ancora assegnato le cui condizioni sono soddisfatte, oppure null.
     */
    fun checkNewBadge(data: BadgeCheckData): BadgeDefinition? {
        val checks = listOf(
            // Primi passi
            "first_sip" to (data.totalEntries > 0),
            "daily_goal" to (data.currentTotalMl >= data.goal),

            // Streak
            "streak_3" to (data.streak >= 3),
            "streak_7" to (data.streak >= 7),
            "streak_14" to (data.streak >= 14),
            "streak_30" to (data.streak >= 30),
            "streak_100" to (data.streak >= 100),

            // Litri totali
            "liters_10" to (data.totalMlAllTime >= 10_000),
            "liters_50" to (data.totalMlAllTime >= 50_000),
            "liters_100" to (data.totalMlAllTime >= 100_000),
            "liters_500" to (data.totalMlAllTime >= 500_000),
            "liters_1000" to (data.totalMlAllTime >= 1_000_000),

            // Giorni attivi
            "active_7" to (data.activeDays >= 7),
            "active_30" to (data.activeDays >= 30),
            "active_100" to (data.activeDays >= 100),
            "active_365" to (data.activeDays >= 365),

            // Livelli
            "level_5" to (data.level >= 5),
            "level_10" to (data.level >= 10),
            "level_20" to (data.level >= 20),
            "level_50" to (data.level >= 50),

            // Sfide
            "challenges_10" to (data.completedChallenges >= 10),
            "challenges_50" to (data.completedChallenges >= 50),
            "challenges_100" to (data.completedChallenges >= 100),
            "challenges_150" to (data.completedChallenges >= 150),
            "challenges_200" to (data.completedChallenges >= 200)
        )

        for ((type, condition) in checks) {
            if (condition && type !in data.earnedBadgeTypes) {
                return ALL_BADGES.find { it.type == type }
            }
        }
        return null
    }
}
