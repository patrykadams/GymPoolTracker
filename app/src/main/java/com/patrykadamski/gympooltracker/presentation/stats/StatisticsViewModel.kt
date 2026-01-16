package com.patrykadamski.gympooltracker.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.usecase.GetWorkoutsUseCase
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

data class StatsUiState(
    val totalWorkouts: Int = 0,
    val gymPercentage: Float = 0.5f, // 0.0 - 1.0
    val poolPercentage: Float = 0.5f,
    val chartEntryModel: ChartEntryModel? = null,
    val bottomAxisLabels: Map<Float, String> = emptyMap()
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    getWorkoutsUseCase: GetWorkoutsUseCase
) : ViewModel() {

    private val chartEntryModelProducer = ChartEntryModelProducer()

    val uiState: StateFlow<StatsUiState> = getWorkoutsUseCase()
        .map { workouts -> calculateStats(workouts) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatsUiState()
        )

    private fun calculateStats(workouts: List<Workout>): StatsUiState {
        if (workouts.isEmpty()) return StatsUiState()

        val gymCount = workouts.count { !isPool(it.type) }
        val poolCount = workouts.count { isPool(it.type) }
        val total = (gymCount + poolCount).coerceAtLeast(1).toFloat()

        val today = LocalDate.now()
        val last7Days = (0..6).map { today.minusDays(it.toLong()) }.reversed()

        val caloriesPerDay = workouts
            .filter { it.date?.toLocalDate()?.isAfter(today.minusDays(7)) == true }
            .groupBy { it.date!!.toLocalDate() }
            .mapValues { entry -> entry.value.sumOf { it.caloriesBurned } }

        val entries = last7Days.mapIndexed { index, date ->
            val calories = caloriesPerDay[date] ?: 0
            entryOf(index.toFloat(), calories.toFloat())
        }

        // Tworzymy mapę etykiet dla osi X (np. "Pn", "Wt")
        val axisLabels = last7Days.mapIndexed { index, date ->
            index.toFloat() to date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pl"))
        }.toMap()

        chartEntryModelProducer.setEntries(entries)

        return StatsUiState(
            totalWorkouts = workouts.size,
            gymPercentage = gymCount / total,
            poolPercentage = poolCount / total,
            chartEntryModel = chartEntryModelProducer.getModel(),
            bottomAxisLabels = axisLabels
        )
    }

    private fun isPool(type: String): Boolean {
        return type.contains("basen", ignoreCase = true) || type.equals("POOL", ignoreCase = true)
    }
}