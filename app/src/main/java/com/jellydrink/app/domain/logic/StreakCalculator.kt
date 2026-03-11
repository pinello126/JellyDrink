package com.jellydrink.app.domain.logic

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object StreakCalculator {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * Calcola lo streak consecutivo di giorni in cui l'obiettivo e' stato raggiunto.
     * @param datesWithGoalMet lista di date (formato "yyyy-MM-dd") in cui l'obiettivo e' stato soddisfatto
     * @param today data odierna
     * @return numero di giorni consecutivi (streak)
     */
    fun calculateStreak(datesWithGoalMet: List<String>, today: LocalDate = LocalDate.now()): Int {
        if (datesWithGoalMet.isEmpty()) return 0

        val sortedDates = datesWithGoalMet
            .map { LocalDate.parse(it, dateFormatter) }
            .sortedDescending()

        // Lo streak deve iniziare da oggi o ieri
        if (ChronoUnit.DAYS.between(sortedDates.first(), today) > 1) return 0

        var streak = 1
        for (i in 0 until sortedDates.size - 1) {
            val diff = ChronoUnit.DAYS.between(sortedDates[i + 1], sortedDates[i])
            if (diff == 1L) {
                streak++
            } else {
                break
            }
        }
        return streak
    }
}
