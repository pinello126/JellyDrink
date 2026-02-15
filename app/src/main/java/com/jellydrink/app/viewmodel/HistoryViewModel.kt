package com.jellydrink.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jellydrink.app.data.db.dao.DailySummary
import com.jellydrink.app.data.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HistoryUiState(
    val dailySummaries: List<DailySummary> = emptyList(),
    val goalMl: Int = 2000,
    val goalPerDay: Map<String, Int> = emptyMap(),
    val weekSummaries: List<DailySummary> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: WaterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            val goal = repository.getDailyGoal().first()
            val today = LocalDate.now()
            val thirtyDaysAgo = today.minusDays(29)
            val sevenDaysAgo = today.minusDays(6)

            val monthlySummary = repository.getDailySummary(
                thirtyDaysAgo.format(dateFormatter),
                today.format(dateFormatter)
            )

            val weeklySummary = repository.getDailySummary(
                sevenDaysAgo.format(dateFormatter),
                today.format(dateFormatter)
            )

            val goalPerDay = repository.getGoalsForRange(
                thirtyDaysAgo.format(dateFormatter),
                today.format(dateFormatter)
            )

            // Riempi i giorni mancanti nella settimana
            val filledWeek = mutableListOf<DailySummary>()
            for (i in 0L..6L) {
                val date = sevenDaysAgo.plusDays(i).format(dateFormatter)
                val existing = weeklySummary.find { it.date == date }
                filledWeek.add(existing ?: DailySummary(date, 0))
            }

            _uiState.value = HistoryUiState(
                dailySummaries = monthlySummary.reversed(),
                goalMl = goal,
                goalPerDay = goalPerDay,
                weekSummaries = filledWeek,
                isLoading = false
            )
        }
    }
}
