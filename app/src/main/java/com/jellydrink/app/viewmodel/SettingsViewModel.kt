package com.jellydrink.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jellydrink.app.data.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val dailyGoal: Int = 2000,
    val customGlasses: List<Int> = listOf(200, 500, 1000),
    val notificationsEnabled: Boolean = true,
    val showResetConfirm: Boolean = false,
    val resetDone: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: WaterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val goal = repository.getDailyGoal().first()
            val glasses = repository.getCustomGlasses().first()
            val notificationsEnabled = repository.getNotificationsEnabled().first()
            _uiState.value = SettingsUiState(
                dailyGoal = goal,
                customGlasses = glasses,
                notificationsEnabled = notificationsEnabled
            )
        }
    }

    fun updateDailyGoal(goal: Int) {
        viewModelScope.launch {
            repository.setDailyGoal(goal)
            _uiState.value = _uiState.value.copy(dailyGoal = goal)
        }
    }

    fun updateGlasses(glasses: List<Int>) {
        viewModelScope.launch {
            repository.setCustomGlasses(glasses)
            _uiState.value = _uiState.value.copy(customGlasses = glasses.sorted())
        }
    }

    fun updateGlassAt(index: Int, newAmountMl: Int) {
        val current = _uiState.value.customGlasses.toMutableList()
        if (index in current.indices && newAmountMl > 0) {
            current[index] = newAmountMl
            updateGlasses(current)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationsEnabled(enabled)
            _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        }
    }

    fun showResetConfirm() {
        _uiState.value = _uiState.value.copy(showResetConfirm = true)
    }

    fun dismissResetConfirm() {
        _uiState.value = _uiState.value.copy(showResetConfirm = false)
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            _uiState.value = SettingsUiState(resetDone = true)
        }
    }

    fun dismissResetDone() {
        _uiState.value = _uiState.value.copy(resetDone = false)
    }
}
