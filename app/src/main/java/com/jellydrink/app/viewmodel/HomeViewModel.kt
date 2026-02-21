package com.jellydrink.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jellydrink.app.data.db.entity.BadgeEntity
import com.jellydrink.app.data.db.entity.DailyChallengeEntity
import com.jellydrink.app.data.db.entity.DecorationEntity
import com.jellydrink.app.data.db.entity.UserProfileEntity
import com.jellydrink.app.data.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val currentMl: Int = 0,
    val goalMl: Int = 2000,
    val glasses: List<Int> = listOf(200, 500, 1000),
    val streak: Int = 0,
    val badges: List<BadgeEntity> = emptyList(),
    val newBadge: BadgeEntity? = null,
    // XP and Level
    val xp: Int = 0,
    val level: Int = 1,
    val xpForCurrentLevel: Int = 0,
    val xpForNextLevel: Int = 100,
    // Challenge
    val todayChallenge: DailyChallengeEntity? = null,
    // Decorations
    val placedDecorations: List<DecorationEntity> = emptyList()
) {
    val percentage: Float
        get() = if (goalMl > 0) (currentMl.toFloat() / goalMl).coerceIn(0f, 1f) else 0f
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: WaterRepository
) : ViewModel() {

    private val _newBadge = MutableStateFlow<BadgeEntity?>(null)
    val newBadge: StateFlow<BadgeEntity?> = _newBadge.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getTodayTotal(),
        repository.getDailyGoal(),
        repository.getCustomGlasses(),
        repository.getAllBadges(),
        repository.getProfile(),
        repository.getTodayChallenge(),
        repository.getPlacedDecorations()
    ) { values ->
        val currentMl = values[0] as Int
        val goal = values[1] as Int
        val glasses = values[2] as List<Int>
        val badges = values[3] as List<BadgeEntity>
        val profile = values[4] as? UserProfileEntity
        val challenge = values[5] as? DailyChallengeEntity
        val placedDecorations = values[6] as List<DecorationEntity>

        HomeUiState(
            currentMl = currentMl,
            goalMl = goal,
            glasses = glasses,
            badges = badges,
            xp = profile?.xp ?: 0,
            level = profile?.level ?: 1,
            xpForCurrentLevel = repository.xpForLevel(profile?.level ?: 1),
            xpForNextLevel = repository.xpForNextLevel(profile?.xp ?: 0),
            todayChallenge = challenge,
            placedDecorations = placedDecorations
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    init {
        viewModelScope.launch {
            // Initialize data (profile, jellyfish collection, decorations, daily challenge)
            repository.initializeData()

            // Update streak
            val goal = repository.getDailyGoal().first()
            repository.calculateStreak(goal)
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.addWaterIntake(amountMl)

            // Check badge after adding water
            val currentTotal = repository.getTodayTotal().first()
            val goal = repository.getDailyGoal().first()
            val badge = repository.checkAndAwardBadges(currentTotal, goal)
            if (badge != null) {
                _newBadge.value = badge
            }
        }
    }

    fun dismissBadge() {
        _newBadge.value = null
    }
}
