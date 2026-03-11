package com.jellydrink.app.domain.logic

import kotlin.math.sqrt

object XpCalculator {

    const val XP_PER_100ML = 1
    const val XP_GOAL_BONUS = 50

    /** Formula: level = sqrt(xp / 100) + 1 */
    fun calculateLevel(xp: Int): Int {
        return (sqrt(xp.toFloat() / 100f) + 1).toInt()
    }

    /** Formula: xpRequired = (level - 1)^2 * 100 */
    fun xpForLevel(level: Int): Int {
        return ((level - 1) * (level - 1)) * 100
    }

    fun xpForNextLevel(currentXp: Int): Int {
        val currentLevel = calculateLevel(currentXp)
        return xpForLevel(currentLevel + 1)
    }

    /**
     * Calcola gli XP guadagnati per un'aggiunta di acqua.
     * @param amountMl quantita' aggiunta
     * @param previousTotal totale prima dell'aggiunta
     * @param currentTotal totale dopo l'aggiunta
     * @param goal obiettivo giornaliero
     * @param streak streak attuale (per il moltiplicatore)
     * @return XP guadagnati
     */
    fun calculateXpEarned(
        amountMl: Int,
        previousTotal: Int,
        currentTotal: Int,
        goal: Int,
        streak: Int
    ): Int {
        var xpEarned = (amountMl / 100) * XP_PER_100ML

        // Streak multiplier (10% bonus per streak day, max 50%)
        val streakMultiplier = 1f + (streak.coerceAtMost(5) * 0.1f)
        xpEarned = (xpEarned * streakMultiplier).toInt()

        // Goal bonus (only once per day when goal is first reached)
        if (previousTotal < goal && currentTotal >= goal) {
            xpEarned += XP_GOAL_BONUS
        }

        return xpEarned
    }
}
