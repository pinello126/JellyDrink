package com.jellydrink.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jellydrink.app.data.db.dao.BadgeDao
import com.jellydrink.app.data.db.dao.DailyChallengeDao
import com.jellydrink.app.data.db.dao.DailyGoalDao
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
import com.jellydrink.app.domain.logic.BadgeDefinitions
import com.jellydrink.app.domain.logic.ChallengeDefinitions
import com.jellydrink.app.domain.logic.GameConstants
import com.jellydrink.app.domain.logic.StreakCalculator
import com.jellydrink.app.domain.logic.XpCalculator
import com.jellydrink.app.domain.model.BadgeWithStatus
import com.jellydrink.app.widget.JellyfishWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

internal val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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
        private val PENDING_BADGE_KEY = stringPreferencesKey("pending_badge_type")
    }

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
        dailyGoalDao.seedMissingDates(currentGoal)
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
            jellyfishDao.insert(
                JellyfishEntity(
                    id = "rosa",
                    nameIt = "Rosa Classica",
                    unlocked = true,
                    selected = true,
                    unlockCondition = "Default",
                    dateUnlocked = today(),
                    cost = 0
                )
            )
        }
    }

    private suspend fun initDecorations() {
        if (decorationDao.getCount() == 0) {
            GameConstants.DECORATIONS.forEach { deco ->
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

        // Calculate XP using domain logic
        val xpEarned = XpCalculator.calculateXpEarned(
            amountMl = amountMl,
            previousTotal = previousTotal,
            currentTotal = currentTotal,
            goal = goal,
            streak = streak
        )

        // Update XP and level
        val newXp = profile.xp + xpEarned
        val newLevel = XpCalculator.calculateLevel(newXp)

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
                spendableXp = profile.spendableXp + xpEarned,
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

        // Update widget
        JellyfishWidget.updateAllWidgets(context)

        // Update lock screen notification
        val glasses = getCustomGlasses().first()
        com.jellydrink.app.notification.WaterNotificationHelper.showWaterProgressNotification(
            context,
            currentTotal,
            goal,
            glasses
        )
    }

    fun getTodayTotal(): Flow<Int> = waterIntakeDao.getTotalForDate(today())

    fun getTodayIntakes(): Flow<List<WaterIntakeEntity>> =
        waterIntakeDao.getIntakesForDate(today())

    suspend fun getTodayIntakesCount(): Int {
        return waterIntakeDao.getIntakesForDate(today()).first().size
    }

    suspend fun getDailySummary(startDate: String, endDate: String): List<com.jellydrink.app.data.db.dao.DailySummary> =
        waterIntakeDao.getDailySummary(startDate, endDate)

    suspend fun getGoalsForRange(startDate: String, endDate: String): Map<String, Int> =
        dailyGoalDao.getGoalsForRange(startDate, endDate).associate { it.date to it.goalMl }

    // --- Profile & XP (delegate to XpCalculator) ---

    fun getProfile(): Flow<UserProfileEntity?> = userProfileDao.getProfile()

    fun calculateLevel(xp: Int): Int = XpCalculator.calculateLevel(xp)

    fun xpForLevel(level: Int): Int = XpCalculator.xpForLevel(level)

    fun xpForNextLevel(currentXp: Int): Int = XpCalculator.xpForNextLevel(currentXp)

    // --- Goals ---

    fun getDailyGoal(): Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[DAILY_GOAL_KEY] ?: GameConstants.DEFAULT_GOAL
    }

    suspend fun setDailyGoal(goal: Int) {
        context.dataStore.edit { prefs ->
            prefs[DAILY_GOAL_KEY] = goal
        }
        dailyGoalDao.upsert(DailyGoalEntity(date = today(), goalMl = goal))
    }

    fun getCustomGlasses(): Flow<List<Int>> = context.dataStore.data.map { prefs ->
        prefs[CUSTOM_GLASSES_KEY]?.map { it.toInt() }?.sorted() ?: GameConstants.DEFAULT_GLASSES
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

    // --- Badge pendente (assegnato da notifica, da mostrare alla riapertura della home) ---

    suspend fun getPendingBadgeType(): String? =
        context.dataStore.data.map { it[PENDING_BADGE_KEY] }.first()

    suspend fun setPendingBadge(type: String) {
        context.dataStore.edit { it[PENDING_BADGE_KEY] = type }
    }

    suspend fun clearPendingBadge() {
        context.dataStore.edit { it.remove(PENDING_BADGE_KEY) }
    }

    suspend fun getBadgeByType(type: String): BadgeEntity? =
        badgeDao.getAllBadges().first().find { it.type == type }

    // --- Streak (delegate to StreakCalculator) ---

    suspend fun calculateStreak(goal: Int): Int {
        val dates = waterIntakeDao.getDatesWithGoalMet(goal)
        return StreakCalculator.calculateStreak(dates)
    }

    // --- Daily Challenges ---

    fun getTodayChallenge(): Flow<DailyChallengeEntity?> =
        dailyChallengeDao.getChallengeForDate(today())

    suspend fun generateDailyChallenge() {
        val existing = dailyChallengeDao.getChallengeForDateSync(today())
        if (existing != null) return

        val randomChallenge = ChallengeDefinitions.CHALLENGE_TYPES.random()
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

        // Delegate pure logic to ChallengeDefinitions
        val result = ChallengeDefinitions.evaluateProgress(
            ChallengeDefinitions.ChallengeInput(
                challengeType = challenge.type,
                targetValue = challenge.targetValue,
                currentProgress = challenge.currentProgress,
                amountMl = amountMl,
                currentTotal = currentTotal,
                goal = goal,
                intakesToday = intakesToday,
                hourOfDay = now.hour,
                minuteOfDay = now.minute
            )
        )

        dailyChallengeDao.updateProgress(today(), result.progress, result.completed)

        // Award XP if completed
        if (result.completed && !challenge.completed) {
            val profile = userProfileDao.getProfileSync() ?: return
            val newXp = profile.xp + challenge.xpReward
            userProfileDao.upsert(profile.copy(
                xp = newXp,
                spendableXp = profile.spendableXp + challenge.xpReward,
                level = XpCalculator.calculateLevel(newXp)
            ))
        }
    }

    // --- Badges (delegate to BadgeDefinitions) ---

    fun getAllBadges(): Flow<List<BadgeEntity>> = badgeDao.getAllBadges()

    suspend fun getAllBadgesWithStatus(): List<BadgeWithStatus> {
        val earnedBadges = badgeDao.getAllBadges().first()

        return BadgeDefinitions.ALL_BADGES.map { definition ->
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

    suspend fun checkAndAwardBadges(currentTotalMl: Int, goal: Int): BadgeEntity? {
        val today = today()
        val profile = userProfileDao.getProfileSync() ?: return null
        val streak = calculateStreak(goal)
        val completedChallenges = dailyChallengeDao.getCompletedChallengesCount()
        val earnedBadgeTypes = badgeDao.getAllBadges().first().map { it.type }.toSet()

        val newBadge = BadgeDefinitions.checkNewBadge(
            BadgeDefinitions.BadgeCheckData(
                totalEntries = waterIntakeDao.getTotalEntries(),
                currentTotalMl = currentTotalMl,
                goal = goal,
                streak = streak,
                totalMlAllTime = profile.totalMlAllTime,
                activeDays = profile.activeDays,
                level = profile.level,
                completedChallenges = completedChallenges,
                earnedBadgeTypes = earnedBadgeTypes
            )
        ) ?: return null

        val badge = BadgeEntity(
            type = newBadge.type,
            dateEarned = today,
            description = newBadge.description
        )
        badgeDao.insert(badge)
        return badge
    }

    // --- Jellyfish Collection ---

    fun getSelectedJellyfish(): Flow<JellyfishEntity?> = jellyfishDao.getSelectedJellyfish()

    // --- Decorations ---

    fun getAllDecorations(): Flow<List<DecorationEntity>> = decorationDao.getAllDecorations()

    fun getPlacedDecorations(): Flow<List<DecorationEntity>> = decorationDao.getPlacedDecorations()

    suspend fun purchaseDecoration(id: String): Boolean {
        val decoration = decorationDao.getDecorationById(id) ?: return false
        if (decoration.owned) return false

        val profile = userProfileDao.getProfileSync() ?: return false
        if (profile.spendableXp < decoration.cost) return false

        userProfileDao.upsert(profile.copy(spendableXp = profile.spendableXp - decoration.cost))
        decorationDao.purchase(id)
        return true
    }

    suspend fun toggleDecorationPlaced(id: String) {
        val decoration = decorationDao.getDecorationById(id) ?: return
        if (!decoration.owned) return

        decorationDao.setPlaced(id, !decoration.placed)
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
