package com.jellydrink.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jellydrink.app.data.db.dao.BadgeDao
import com.jellydrink.app.data.db.dao.DailyChallengeDao
import com.jellydrink.app.data.db.dao.DailyGoalDao
import com.jellydrink.app.data.db.dao.DailySummary
import com.jellydrink.app.data.db.dao.DecorationDao
import com.jellydrink.app.data.db.dao.JellyfishDao
import com.jellydrink.app.data.db.dao.UserProfileDao
import com.jellydrink.app.data.db.dao.WaterIntakeDao
import com.jellydrink.app.data.db.entity.BadgeEntity
import com.jellydrink.app.data.db.entity.DailyGoalEntity
import com.jellydrink.app.data.db.entity.DailyChallengeEntity
import com.jellydrink.app.data.db.entity.DecorationEntity
import com.jellydrink.app.data.db.entity.JellyfishEntity
import com.jellydrink.app.data.db.entity.UserProfileEntity
import com.jellydrink.app.data.db.entity.WaterIntakeEntity
import com.jellydrink.app.widget.JellyfishWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class WaterRepository @Inject constructor(
    private val waterIntakeDao: WaterIntakeDao,
    private val badgeDao: BadgeDao,
    private val userProfileDao: UserProfileDao,
    private val dailyChallengeDao: DailyChallengeDao,
    private val jellyfishDao: JellyfishDao,
    private val decorationDao: DecorationDao,
    private val dailyGoalDao: DailyGoalDao,
    @ApplicationContext private val context: Context
) {
    companion object {
        private val DAILY_GOAL_KEY = intPreferencesKey("daily_goal")
        private val CUSTOM_GLASSES_KEY = stringSetPreferencesKey("custom_glasses")
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        const val DEFAULT_GOAL = 2000
        val DEFAULT_GLASSES = listOf(200, 500, 1000)

        // Badge categories
        const val CAT_PRIMI_PASSI = "Primi Passi"
        const val CAT_STREAK = "Streak"
        const val CAT_LITRI = "Litri Totali"
        const val CAT_GIORNI = "Giorni Attivi"
        const val CAT_LIVELLI = "Livelli"
        const val CAT_SFIDE = "Sfide e Record"

        val BADGE_CATEGORIES_ORDER = listOf(CAT_PRIMI_PASSI, CAT_STREAK, CAT_LITRI, CAT_GIORNI, CAT_LIVELLI, CAT_SFIDE)

        // XP Configuration
        const val XP_PER_100ML = 1
        const val XP_GOAL_BONUS = 50

        // Challenge types
        val CHALLENGE_TYPES = listOf(
            ChallengeType("early_bird", "Bevi prima delle 9:00", 1, 30),
            ChallengeType("consistent", "Registra 5 assunzioni oggi", 5, 30),
            ChallengeType("big_gulp", "Bevi 500ml in una volta", 500, 35),
            ChallengeType("afternoon_goal", "Raggiungi l'obiettivo entro le 15:00", 1, 40),
            ChallengeType("full_tank", "Bevi il 120% dell'obiettivo", 120, 50)
        )

        // Jellyfish species
        val JELLYFISH_SPECIES = listOf(
            JellyfishSpecies("rosa", "Rosa Classica", "Default", 0),
            JellyfishSpecies("lunar", "Medusa Lunare", "Streak 30 giorni o 500 XP", 500),
            JellyfishSpecies("abyssal", "Medusa Abissale", "100L totali o 800 XP", 800),
            JellyfishSpecies("aurora", "Medusa Aurora", "Livello 10 o 600 XP", 600),
            JellyfishSpecies("crystal", "Medusa Cristallo", "30 sfide o 1000 XP", 1000),
            JellyfishSpecies("golden", "Medusa Dorata", "Livello 20 o 1500 XP", 1500)
        )

        // Decorations
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

        // All possible badges (nel companion per accesso statico)
        val ALL_BADGES = listOf(
            // Primi passi
            BadgeDefinition("first_sip", "Primo Sorso", "Hai registrato il tuo primo consumo d'acqua", "💧", 1, CAT_PRIMI_PASSI),
            BadgeDefinition("daily_goal", "Obiettivo Raggiunto", "Hai completato il tuo obiettivo giornaliero", "🎯", 2, CAT_PRIMI_PASSI),

            // Streak
            BadgeDefinition("streak_3", "Streak 3", "Tre giorni consecutivi al 100%", "🔥", 3, CAT_STREAK),
            BadgeDefinition("streak_7", "Streak 7", "Una settimana intera al 100%", "🔥", 4, CAT_STREAK),
            BadgeDefinition("streak_14", "Streak 14", "Due settimane consecutive al 100%", "🔥", 5, CAT_STREAK),
            BadgeDefinition("streak_30", "Streak 30", "Un mese intero al 100%", "🔥", 6, CAT_STREAK),
            BadgeDefinition("streak_100", "Streak 100", "Cento giorni consecutivi al 100%! Leggendario!", "🔥", 7, CAT_STREAK),

            // Litri totali
            BadgeDefinition("liters_10", "10 Litri", "Hai bevuto 10 litri d'acqua in totale", "💦", 8, CAT_LITRI),
            BadgeDefinition("liters_50", "50 Litri", "Hai bevuto 50 litri d'acqua in totale", "💦", 9, CAT_LITRI),
            BadgeDefinition("liters_100", "100 Litri", "Hai bevuto 100 litri d'acqua in totale", "💦", 10, CAT_LITRI),
            BadgeDefinition("liters_500", "500 Litri", "Hai bevuto 500 litri d'acqua in totale", "💦", 11, CAT_LITRI),
            BadgeDefinition("liters_1000", "1000 Litri", "Hai bevuto 1000 litri d'acqua! Incredibile!", "💦", 12, CAT_LITRI),

            // Giorni attivi
            BadgeDefinition("active_7", "7 Giorni Attivi", "Hai registrato acqua per 7 giorni", "📅", 13, CAT_GIORNI),
            BadgeDefinition("active_30", "30 Giorni Attivi", "Hai registrato acqua per 30 giorni", "📅", 14, CAT_GIORNI),
            BadgeDefinition("active_100", "100 Giorni Attivi", "Hai registrato acqua per 100 giorni", "📅", 15, CAT_GIORNI),
            BadgeDefinition("active_365", "1 Anno Attivo", "Hai registrato acqua per 365 giorni!", "📅", 16, CAT_GIORNI),

            // Livelli
            BadgeDefinition("level_5", "Livello 5", "Hai raggiunto il livello 5", "⭐", 17, CAT_LIVELLI),
            BadgeDefinition("level_10", "Livello 10", "Hai raggiunto il livello 10", "⭐", 18, CAT_LIVELLI),
            BadgeDefinition("level_20", "Livello 20", "Hai raggiunto il livello 20", "⭐", 19, CAT_LIVELLI),
            BadgeDefinition("level_50", "Livello 50", "Hai raggiunto il livello 50!", "⭐", 20, CAT_LIVELLI),

            // Sfide e Record
            BadgeDefinition("challenges_10", "10 Sfide", "Hai completato 10 sfide giornaliere", "🏆", 21, CAT_SFIDE),
            BadgeDefinition("challenges_50", "50 Sfide", "Hai completato 50 sfide giornaliere", "🏆", 22, CAT_SFIDE),
            BadgeDefinition("challenges_100", "100 Sfide", "Hai completato 100 sfide giornaliere", "🏆", 23, CAT_SFIDE),
            BadgeDefinition("challenges_150", "150 Sfide", "Hai completato 150 sfide giornaliere", "🏆", 24, CAT_SFIDE),
            BadgeDefinition("challenges_200", "200 Sfide", "Hai completato 200 sfide giornaliere", "🏆", 25, CAT_SFIDE)
        )
    }

    data class ChallengeType(val id: String, val description: String, val target: Int, val xpReward: Int)
    data class JellyfishSpecies(val id: String, val nameIt: String, val unlockCondition: String, val cost: Int)
    data class DecorationInfo(val id: String, val nameIt: String, val cost: Int)
    data class BadgeDefinition(
        val type: String,
        val name: String,
        val description: String,
        val icon: String,
        val order: Int,
        val category: String
    )

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private fun today(): String = LocalDate.now().format(dateFormatter)

    // --- Initialization ---

    suspend fun initializeData() {
        initProfile()
        initJellyfishCollection()
        initDecorations()
        generateDailyChallenge()
        seedDailyGoals()
    }

    private suspend fun seedDailyGoals() {
        val currentGoal = getDailyGoal().first()
        // Seed historical dates missing from daily_goal with current goal
        dailyGoalDao.seedMissingDates(currentGoal)
        // Ensure today has a snapshot
        dailyGoalDao.insertIfAbsent(DailyGoalEntity(date = today(), goalMl = currentGoal))
    }

    private suspend fun initProfile() {
        val existing = userProfileDao.getProfileSync()
        if (existing == null) {
            userProfileDao.upsert(UserProfileEntity())
        }
    }

    private suspend fun initJellyfishCollection() {
        if (jellyfishDao.getCount() == 0) {
            val today = today()
            JELLYFISH_SPECIES.forEachIndexed { index, species ->
                jellyfishDao.insert(
                    JellyfishEntity(
                        id = species.id,
                        nameIt = species.nameIt,
                        unlocked = index == 0, // Rosa is unlocked by default
                        selected = index == 0, // Rosa is selected by default
                        unlockCondition = species.unlockCondition,
                        dateUnlocked = if (index == 0) today else null,
                        cost = species.cost
                    )
                )
            }
        }
    }

    private suspend fun initDecorations() {
        if (decorationDao.getCount() == 0) {
            DECORATIONS.forEach { deco ->
                decorationDao.insert(
                    DecorationEntity(
                        id = deco.id,
                        nameIt = deco.nameIt,
                        cost = deco.cost
                    )
                )
            }
        }
    }

    // --- Water Intake ---

    suspend fun addWaterIntake(amountMl: Int) {
        val now = today()
        val timestamp = System.currentTimeMillis()

        waterIntakeDao.insert(
            WaterIntakeEntity(
                date = now,
                amountMl = amountMl,
                timestamp = timestamp
            )
        )

        // Snapshot today's goal (only if not already present)
        val goal = getDailyGoal().first()
        dailyGoalDao.insertIfAbsent(DailyGoalEntity(date = now, goalMl = goal))

        // Update profile
        val profile = userProfileDao.getProfileSync() ?: UserProfileEntity()
        val currentTotal = waterIntakeDao.getTotalForDate(now).first()
        val previousTotal = currentTotal - amountMl
        val streak = calculateStreak(goal)

        // Calculate XP
        var xpEarned = (amountMl / 100) * XP_PER_100ML

        // Streak multiplier (10% bonus per streak day, max 50%)
        val streakMultiplier = 1f + (streak.coerceAtMost(5) * 0.1f)
        xpEarned = (xpEarned * streakMultiplier).toInt()

        // Goal bonus (only once per day when goal is first reached)
        if (previousTotal < goal && currentTotal >= goal) {
            xpEarned += XP_GOAL_BONUS
        }

        // Update XP and level
        val newXp = profile.xp + xpEarned
        val newLevel = calculateLevel(newXp)

        // Update total ml and daily record
        val newDailyRecord = if (currentTotal > profile.dailyRecord) currentTotal else profile.dailyRecord

        // Update best streak
        val newBestStreak = if (streak > profile.bestStreak) streak else profile.bestStreak

        // Increment active days only if this is the first intake of the day
        val isFirstIntakeToday = profile.lastActiveDate != now
        val newActiveDays = if (isFirstIntakeToday) profile.activeDays + 1 else profile.activeDays

        userProfileDao.upsert(
            profile.copy(
                xp = newXp,
                spendableXp = profile.spendableXp + xpEarned,  // Aggiungi XP spendibili
                level = newLevel,
                totalMlAllTime = profile.totalMlAllTime + amountMl,
                bestStreak = newBestStreak,
                activeDays = newActiveDays,
                dailyRecord = newDailyRecord,
                lastActiveDate = now
            )
        )

        // Update daily challenge progress
        updateChallengeProgress(amountMl, currentTotal, goal)

        // Check for jellyfish unlocks
        checkJellyfishUnlocks(newLevel, newBestStreak, profile.totalMlAllTime + amountMl)

        // Update widget
        JellyfishWidget.updateAllWidgets(context)

        // Update lock screen notification
        com.jellydrink.app.notification.WaterNotificationHelper.showWaterProgressNotification(
            context,
            currentTotal,
            goal
        )
    }

    fun getTodayTotal(): Flow<Int> = waterIntakeDao.getTotalForDate(today())

    fun getTodayIntakes(): Flow<List<WaterIntakeEntity>> =
        waterIntakeDao.getIntakesForDate(today())

    suspend fun getTodayIntakesCount(): Int {
        return waterIntakeDao.getIntakesForDate(today()).first().size
    }

    suspend fun getDailySummary(startDate: String, endDate: String): List<DailySummary> =
        waterIntakeDao.getDailySummary(startDate, endDate)

    suspend fun getGoalsForRange(startDate: String, endDate: String): Map<String, Int> =
        dailyGoalDao.getGoalsForRange(startDate, endDate).associate { it.date to it.goalMl }

    // --- Profile & XP ---

    fun getProfile(): Flow<UserProfileEntity?> = userProfileDao.getProfile()

    fun calculateLevel(xp: Int): Int {
        // Formula: level = sqrt(xp / 100) + 1
        return (sqrt(xp.toFloat() / 100f) + 1).toInt()
    }

    fun xpForLevel(level: Int): Int {
        // Formula: xpRequired = (level - 1)^2 * 100
        return ((level - 1) * (level - 1)) * 100
    }

    fun xpForNextLevel(currentXp: Int): Int {
        val currentLevel = calculateLevel(currentXp)
        return xpForLevel(currentLevel + 1)
    }

    // --- Goals ---

    fun getDailyGoal(): Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[DAILY_GOAL_KEY] ?: DEFAULT_GOAL
    }

    suspend fun setDailyGoal(goal: Int) {
        context.dataStore.edit { prefs ->
            prefs[DAILY_GOAL_KEY] = goal
        }
        // Update today's snapshot with the new goal
        dailyGoalDao.upsert(DailyGoalEntity(date = today(), goalMl = goal))
    }

    fun getCustomGlasses(): Flow<List<Int>> = context.dataStore.data.map { prefs ->
        prefs[CUSTOM_GLASSES_KEY]?.map { it.toInt() }?.sorted() ?: DEFAULT_GLASSES
    }

    suspend fun setCustomGlasses(glasses: List<Int>) {
        context.dataStore.edit { prefs ->
            prefs[CUSTOM_GLASSES_KEY] = glasses.map { it.toString() }.toSet()
        }
    }

    // --- Notifications ---

    fun getNotificationsEnabled(): Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_ENABLED_KEY] ?: true
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }

    // --- Streak ---

    suspend fun calculateStreak(goal: Int): Int {
        val dates = waterIntakeDao.getDatesWithGoalMet(goal)
        if (dates.isEmpty()) return 0

        val sortedDates = dates.map { LocalDate.parse(it, dateFormatter) }.sortedDescending()
        val todayDate = LocalDate.now()

        // Lo streak deve iniziare da oggi o ieri
        if (ChronoUnit.DAYS.between(sortedDates.first(), todayDate) > 1) return 0

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

    // --- Daily Challenges ---

    fun getTodayChallenge(): Flow<DailyChallengeEntity?> =
        dailyChallengeDao.getChallengeForDate(today())

    suspend fun generateDailyChallenge() {
        val existing = dailyChallengeDao.getChallengeForDateSync(today())
        if (existing != null) return

        val randomChallenge = CHALLENGE_TYPES.random()
        dailyChallengeDao.insert(
            DailyChallengeEntity(
                date = today(),
                type = randomChallenge.id,
                targetValue = randomChallenge.target,
                xpReward = randomChallenge.xpReward
            )
        )
    }

    private suspend fun updateChallengeProgress(amountMl: Int, currentTotal: Int, goal: Int) {
        val challenge = dailyChallengeDao.getChallengeForDateSync(today()) ?: return
        if (challenge.completed) return

        val now = LocalTime.now()
        val intakesToday = getTodayIntakesCount()

        var progress = challenge.currentProgress
        var completed = false

        when (challenge.type) {
            "early_bird" -> {
                if (now.isBefore(LocalTime.of(9, 0))) {
                    progress = 1
                    completed = true
                }
            }
            "consistent" -> {
                progress = intakesToday
                completed = intakesToday >= challenge.targetValue
            }
            "big_gulp" -> {
                if (amountMl >= challenge.targetValue) {
                    progress = amountMl
                    completed = true
                }
            }
            "afternoon_goal" -> {
                if (currentTotal >= goal && now.isBefore(LocalTime.of(15, 0))) {
                    progress = 1
                    completed = true
                }
            }
            "full_tank" -> {
                val percentage = (currentTotal * 100) / goal
                progress = percentage
                completed = percentage >= challenge.targetValue
            }
        }

        dailyChallengeDao.updateProgress(today(), progress, completed)

        // Award XP if completed
        if (completed && !challenge.completed) {
            val profile = userProfileDao.getProfileSync() ?: return
            val newXp = profile.xp + challenge.xpReward
            userProfileDao.upsert(profile.copy(
                xp = newXp,
                spendableXp = profile.spendableXp + challenge.xpReward,  // Aggiungi XP spendibili
                level = calculateLevel(newXp)
            ))
        }
    }

    // --- Badges ---

    fun getAllBadges(): Flow<List<BadgeEntity>> = badgeDao.getAllBadges()

    suspend fun getAllBadgesWithStatus(): List<BadgeWithStatus> {
        val earnedBadges = badgeDao.getAllBadges().first()
        val earnedTypes = earnedBadges.map { it.type }.toSet()

        return ALL_BADGES.map { definition ->
            val earned = earnedBadges.find { it.type == definition.type }
            BadgeWithStatus(
                type = definition.type,
                name = definition.name,
                description = definition.description,
                icon = definition.icon,
                order = definition.order,
                category = definition.category,
                isEarned = earned != null,
                dateEarned = earned?.dateEarned
            )
        }.sortedBy { it.order }
    }

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

    suspend fun checkAndAwardBadges(currentTotalMl: Int, goal: Int): BadgeEntity? {
        val today = today()
        val profile = userProfileDao.getProfileSync() ?: return null
        val streak = calculateStreak(goal)
        val completedChallenges = dailyChallengeDao.getCompletedChallengesCount()

        // Check all badges in order
        val badgeChecks = listOf(
            // Primi passi
            Triple("first_sip", waterIntakeDao.getTotalEntries() > 0, "Hai registrato il tuo primo consumo d'acqua"),
            Triple("daily_goal", currentTotalMl >= goal, "Hai completato il tuo obiettivo giornaliero"),

            // Streak
            Triple("streak_3", streak >= 3, "Tre giorni consecutivi al 100%"),
            Triple("streak_7", streak >= 7, "Una settimana intera al 100%"),
            Triple("streak_14", streak >= 14, "Due settimane consecutive al 100%"),
            Triple("streak_30", streak >= 30, "Un mese intero al 100%"),
            Triple("streak_100", streak >= 100, "Cento giorni consecutivi al 100%! Leggendario!"),

            // Litri totali
            Triple("liters_10", profile.totalMlAllTime >= 10000, "Hai bevuto 10 litri d'acqua in totale"),
            Triple("liters_50", profile.totalMlAllTime >= 50000, "Hai bevuto 50 litri d'acqua in totale"),
            Triple("liters_100", profile.totalMlAllTime >= 100000, "Hai bevuto 100 litri d'acqua in totale"),
            Triple("liters_500", profile.totalMlAllTime >= 500000, "Hai bevuto 500 litri d'acqua in totale"),
            Triple("liters_1000", profile.totalMlAllTime >= 1000000, "Hai bevuto 1000 litri d'acqua! Incredibile!"),

            // Giorni attivi
            Triple("active_7", profile.activeDays >= 7, "Hai registrato acqua per 7 giorni"),
            Triple("active_30", profile.activeDays >= 30, "Hai registrato acqua per 30 giorni"),
            Triple("active_100", profile.activeDays >= 100, "Hai registrato acqua per 100 giorni"),
            Triple("active_365", profile.activeDays >= 365, "Hai registrato acqua per 365 giorni!"),

            // Livelli
            Triple("level_5", profile.level >= 5, "Hai raggiunto il livello 5"),
            Triple("level_10", profile.level >= 10, "Hai raggiunto il livello 10"),
            Triple("level_20", profile.level >= 20, "Hai raggiunto il livello 20"),
            Triple("level_50", profile.level >= 50, "Hai raggiunto il livello 50!"),

            // Sfide
            Triple("challenges_10", completedChallenges >= 10, "Hai completato 10 sfide giornaliere"),
            Triple("challenges_50", completedChallenges >= 50, "Hai completato 50 sfide giornaliere"),
            Triple("challenges_100", completedChallenges >= 100, "Hai completato 100 sfide giornaliere"),

            Triple("challenges_150", completedChallenges >= 150, "Hai completato 150 sfide giornaliere"),
            Triple("challenges_200", completedChallenges >= 200, "Hai completato 200 sfide giornaliere")
        )

        // Check each badge
        for ((type, condition, description) in badgeChecks) {
            if (condition && !badgeDao.hasBadge(type)) {
                val badge = BadgeEntity(
                    type = type,
                    dateEarned = today,
                    description = description
                )
                badgeDao.insert(badge)
                return badge
            }
        }

        return null
    }

    // --- Jellyfish Collection ---

    fun getAllJellyfish(): Flow<List<JellyfishEntity>> = jellyfishDao.getAllJellyfish()

    fun getSelectedJellyfish(): Flow<JellyfishEntity?> = jellyfishDao.getSelectedJellyfish()

    suspend fun selectJellyfish(id: String) {
        val jellyfish = jellyfishDao.getJellyfishById(id) ?: return
        if (!jellyfish.unlocked) return

        jellyfishDao.deselectAll()
        jellyfishDao.select(id)
    }

    private suspend fun checkJellyfishUnlocks(level: Int, bestStreak: Int, totalMl: Int) {
        val today = today()
        val completedChallenges = dailyChallengeDao.getCompletedChallengesCount()

        // Lunar: Streak 30 giorni
        if (bestStreak >= 30) {
            val lunar = jellyfishDao.getJellyfishById("lunar")
            if (lunar != null && !lunar.unlocked) {
                jellyfishDao.unlock("lunar", today)
            }
        }

        // Abyssal: 100L totali (100000ml)
        if (totalMl >= 100000) {
            val abyssal = jellyfishDao.getJellyfishById("abyssal")
            if (abyssal != null && !abyssal.unlocked) {
                jellyfishDao.unlock("abyssal", today)
            }
        }

        // Aurora: Livello 10
        if (level >= 10) {
            val aurora = jellyfishDao.getJellyfishById("aurora")
            if (aurora != null && !aurora.unlocked) {
                jellyfishDao.unlock("aurora", today)
            }
        }

        // Crystal: 30 sfide completate
        if (completedChallenges >= 30) {
            val crystal = jellyfishDao.getJellyfishById("crystal")
            if (crystal != null && !crystal.unlocked) {
                jellyfishDao.unlock("crystal", today)
            }
        }

        // Golden: Livello 20
        if (level >= 20) {
            val golden = jellyfishDao.getJellyfishById("golden")
            if (golden != null && !golden.unlocked) {
                jellyfishDao.unlock("golden", today)
            }
        }
    }

    // --- Decorations ---

    fun getAllDecorations(): Flow<List<DecorationEntity>> = decorationDao.getAllDecorations()

    fun getOwnedDecorations(): Flow<List<DecorationEntity>> = decorationDao.getOwnedDecorations()

    fun getPlacedDecorations(): Flow<List<DecorationEntity>> = decorationDao.getPlacedDecorations()

    suspend fun purchaseDecoration(id: String): Boolean {
        val decoration = decorationDao.getDecorationById(id) ?: return false
        if (decoration.owned) return false

        val profile = userProfileDao.getProfileSync() ?: return false
        if (profile.spendableXp < decoration.cost) return false  // Controlla XP spendibili

        // Deduct XP spendibili and purchase
        userProfileDao.upsert(profile.copy(spendableXp = profile.spendableXp - decoration.cost))
        decorationDao.purchase(id)
        return true
    }

    suspend fun toggleDecorationPlaced(id: String) {
        val decoration = decorationDao.getDecorationById(id) ?: return
        if (!decoration.owned) return

        decorationDao.setPlaced(id, !decoration.placed)
    }

    suspend fun purchaseJellyfish(id: String): Boolean {
        val jellyfish = jellyfishDao.getJellyfishById(id) ?: return false
        if (jellyfish.unlocked) return false

        val profile = userProfileDao.getProfileSync() ?: return false
        if (profile.spendableXp < jellyfish.cost) return false  // Controlla XP spendibili

        // Deduct XP spendibili and unlock jellyfish
        val today = today()
        userProfileDao.upsert(profile.copy(spendableXp = profile.spendableXp - jellyfish.cost))
        jellyfishDao.unlock(id, today)
        return true
    }

    // --- Reset ---

    suspend fun resetAllData() {
        waterIntakeDao.deleteAll()
        badgeDao.deleteAll()
        userProfileDao.deleteAll()
        dailyChallengeDao.deleteAll()
        jellyfishDao.deleteAll()
        decorationDao.deleteAll()
        dailyGoalDao.deleteAll()
        context.dataStore.edit { it.clear() }

        // Re-initialize
        initializeData()
    }
}
