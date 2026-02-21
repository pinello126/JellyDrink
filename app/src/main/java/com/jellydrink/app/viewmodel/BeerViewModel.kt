package com.jellydrink.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jellydrink.app.data.db.entity.DecorationEntity
import com.jellydrink.app.data.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BeerUiState(
    val todayTotalCl: Int = 0,
    val isPufferfishUnlocked: Boolean = false,
    val level: Int = 1,
    val spendableXp: Int = 0,
    val placedDecorations: List<DecorationEntity> = emptyList()
) {
    companion object {
        const val PUFFERFISH_COST = 300
        const val REQUIRED_LEVEL = 3
        // Scala di visualizzazione: 200cl = 100% fill
        const val MAX_DISPLAY_CL = 200
        // Soglie ubriachezza
        const val DRUNK_THRESHOLD_CL = 60
        const val VERY_DRUNK_THRESHOLD_CL = 100
    }
    val fillPercentage: Float get() = (todayTotalCl.toFloat() / MAX_DISPLAY_CL).coerceIn(0f, 1f)
    val isDrunk: Boolean get() = todayTotalCl >= DRUNK_THRESHOLD_CL
    val isVeryDrunk: Boolean get() = todayTotalCl >= VERY_DRUNK_THRESHOLD_CL
    val canUnlock: Boolean get() = level >= REQUIRED_LEVEL && spendableXp >= PUFFERFISH_COST
    val meetsLevelRequirement: Boolean get() = level >= REQUIRED_LEVEL
}

@HiltViewModel
class BeerViewModel @Inject constructor(
    private val repository: WaterRepository
) : ViewModel() {

    private val _unlockResult = MutableStateFlow<UnlockResult?>(null)
    val unlockResult: StateFlow<UnlockResult?> = _unlockResult.asStateFlow()

    val uiState: StateFlow<BeerUiState> = combine(
        repository.getTodayBeerTotal(),
        repository.getPufferfish(),
        repository.getProfile(),
        repository.getPlacedDecorations()
    ) { totalCl, pufferfish, profile, decorations ->
        BeerUiState(
            todayTotalCl = totalCl,
            isPufferfishUnlocked = pufferfish?.unlocked == true,
            level = profile?.level ?: 1,
            spendableXp = profile?.spendableXp ?: 0,
            placedDecorations = decorations
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BeerUiState()
    )

    fun addBeer(amountCl: Int) {
        viewModelScope.launch {
            repository.addBeerIntake(amountCl)
        }
    }

    fun unlockPufferfish() {
        viewModelScope.launch {
            val success = repository.unlockPufferfish()
            _unlockResult.value = if (success) UnlockResult.Success else UnlockResult.Failed
        }
    }

    fun dismissUnlockResult() {
        _unlockResult.value = null
    }

    sealed class UnlockResult {
        data object Success : UnlockResult()
        data object Failed : UnlockResult()
    }
}
