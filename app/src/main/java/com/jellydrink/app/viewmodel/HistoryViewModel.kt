package com.jellydrink.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jellydrink.app.data.db.dao.BeerDailySummary
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
    val beerDailySummaries: List<BeerDailySummary> = emptyList(),
    val beerWeekSummaries: List<BeerDailySummary> = emptyList(),
    val showBeer: Boolean = false,
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

    fun toggleMode() {
        _uiState.value = _uiState.value.copy(showBeer = !_uiState.value.showBeer)
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

            // Riempi i giorni mancanti nella settimana (acqua)
            val filledWeek = mutableListOf<DailySummary>()
            for (i in 0L..6L) {
                val date = sevenDaysAgo.plusDays(i).format(dateFormatter)
                val existing = weeklySummary.find { it.date == date }
                filledWeek.add(existing ?: DailySummary(date, 0))
            }

            val beerMonthlySummary = repository.getBeerDailySummary(
                thirtyDaysAgo.format(dateFormatter),
                today.format(dateFormatter)
            )
            val beerWeeklySummary = repository.getBeerDailySummary(
                sevenDaysAgo.format(dateFormatter),
                today.format(dateFormatter)
            )

            // Riempi i giorni mancanti nella settimana (birra)
            val filledBeerWeek = mutableListOf<BeerDailySummary>()
            for (i in 0L..6L) {
                val date = sevenDaysAgo.plusDays(i).format(dateFormatter)
                val existing = beerWeeklySummary.find { it.date == date }
                filledBeerWeek.add(existing ?: BeerDailySummary(date, 0))
            }

            val currentShowBeer = _uiState.value.showBeer
            _uiState.value = HistoryUiState(
                dailySummaries = monthlySummary.reversed(),
                goalMl = goal,
                goalPerDay = goalPerDay,
                weekSummaries = filledWeek,
                beerDailySummaries = beerMonthlySummary.reversed(),
                beerWeekSummaries = filledBeerWeek,
                showBeer = currentShowBeer,
                isLoading = false
            )
        }
    }
}
