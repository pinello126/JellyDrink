package com.jellydrink.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jellydrink.app.data.db.entity.UserProfileEntity
import com.jellydrink.app.data.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ProfileUiState(
    val profile: UserProfileEntity? = null,
    val badges: List<WaterRepository.BadgeWithStatus> = emptyList(),
    val xpForCurrentLevel: Int = 0,
    val xpForNextLevel: Int = 100,
    val currentStreak: Int = 0
) {
    val level: Int get() = profile?.level ?: 1
    val xp: Int get() = profile?.xp ?: 0
    val totalLiters: Float get() = (profile?.totalMlAllTime ?: 0) / 1000f
    val bestStreak: Int get() = profile?.bestStreak ?: 0
    val activeDays: Int get() = profile?.activeDays ?: 0
    val dailyRecord: Int get() = profile?.dailyRecord ?: 0
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: WaterRepository
) : ViewModel() {

    private val badgesFlow = flow {
        while (true) {
            emit(repository.getAllBadgesWithStatus())
            kotlinx.coroutines.delay(1000) // Refresh every second
        }
    }

    val uiState: StateFlow<ProfileUiState> = combine(
        repository.getProfile(),
        badgesFlow
    ) { profile, badges ->
        val goal = repository.getDailyGoal().first()
        ProfileUiState(
            profile = profile,
            badges = badges,
            xpForCurrentLevel = repository.xpForLevel(profile?.level ?: 1),
            xpForNextLevel = repository.xpForNextLevel(profile?.xp ?: 0),
            currentStreak = repository.calculateStreak(goal)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )
}
