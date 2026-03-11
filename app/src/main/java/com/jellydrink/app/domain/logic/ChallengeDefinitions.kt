package com.jellydrink.app.domain.logic

import com.jellydrink.app.domain.model.ChallengeType

object ChallengeDefinitions {

    val CHALLENGE_TYPES = listOf(
        ChallengeType("early_bird", "Bevi prima delle 9:00", 1, 30),
        ChallengeType("consistent", "Bevi almeno 5 volte oggi", 5, 30),
        ChallengeType("big_gulp", "Bevi 0,5L in una volta", 500, 35),
        ChallengeType("afternoon_goal", "Raggiungi l'obiettivo entro le 21:00", 1, 40),
        ChallengeType("full_tank", "Bevi il 120% dell'obiettivo", 120, 50)
    )

    /**
     * Input necessario per valutare il progresso di una sfida.
     */
    data class ChallengeInput(
        val challengeType: String,
        val targetValue: Int,
        val currentProgress: Int,
        val amountMl: Int,
        val currentTotal: Int,
        val goal: Int,
        val intakesToday: Int,
        val hourOfDay: Int,
        val minuteOfDay: Int
    )

    /**
     * Risultato della valutazione del progresso di una sfida.
     */
    data class ChallengeResult(
        val progress: Int,
        val completed: Boolean
    )

    /**
     * Calcola il progresso di una sfida in base ai dati forniti.
     * Logica pura senza dipendenze Android.
     */
    fun evaluateProgress(input: ChallengeInput): ChallengeResult {
        var progress = input.currentProgress
        var completed = false

        when (input.challengeType) {
            "early_bird" -> {
                val beforeNine = input.hourOfDay < 9 || (input.hourOfDay == 9 && input.minuteOfDay == 0)
                if (beforeNine) {
                    progress = 1
                    completed = true
                }
            }
            "consistent" -> {
                progress = input.intakesToday
                completed = input.intakesToday >= input.targetValue
            }
            "big_gulp" -> {
                if (input.amountMl >= input.targetValue) {
                    progress = input.amountMl
                    completed = true
                }
            }
            "afternoon_goal" -> {
                val beforeNine = input.hourOfDay < 21 || (input.hourOfDay == 21 && input.minuteOfDay == 0)
                if (input.currentTotal >= input.goal && beforeNine) {
                    progress = 1
                    completed = true
                }
            }
            "full_tank" -> {
                val percentage = (input.currentTotal * 100) / input.goal
                progress = percentage
                completed = percentage >= input.targetValue
            }
        }

        return ChallengeResult(progress, completed)
    }
}
