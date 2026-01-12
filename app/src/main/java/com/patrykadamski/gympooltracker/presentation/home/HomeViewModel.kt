// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/home/HomeViewModel.kt
package com.patrykadamski.gympooltracker.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykadamski.gympooltracker.data.prefs.UserPreferencesRepository
import com.patrykadamski.gympooltracker.domain.model.Routine
import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.usecase.CreateWorkoutFromRoutineUseCase
import com.patrykadamski.gympooltracker.domain.usecase.DeleteWorkoutUseCase
import com.patrykadamski.gympooltracker.domain.usecase.GetRoutinesUseCase
import com.patrykadamski.gympooltracker.domain.usecase.GetWorkoutsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val workouts: List<Workout> = emptyList(),
    val routines: List<Routine> = emptyList(), // NEW
    val totalWorkouts: Int = 0,
    val totalCalories: Int = 0,
    val gymCount: Int = 0,
    val poolCount: Int = 0,
    val weeklyGoalKcal: Int = 2000,
    val currentWeekKcal: Int = 0,
    val progress: Float = 0f
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getWorkoutsUseCase: GetWorkoutsUseCase,
    getRoutinesUseCase: GetRoutinesUseCase, // NEW
    private val createWorkoutFromRoutineUseCase: CreateWorkoutFromRoutineUseCase, // NEW
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // Combining streams: Workouts + Goal + Routines
    val uiState: StateFlow<HomeUiState> = combine(
        getWorkoutsUseCase(),
        userPreferencesRepository.weeklyGoalFlow,
        getRoutinesUseCase()
    ) { workouts, goal, routines ->
        calculateState(workouts, goal, routines)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    private fun calculateState(workouts: List<Workout>, goal: Int, routines: List<Routine>): HomeUiState {
        val totalCalories = workouts.sumOf { it.caloriesBurned }
        val gymCount = workouts.count { !isPool(it.type) }
        val poolCount = workouts.count { isPool(it.type) }
        val currentWeekKcal = calculateCurrentWeekCalories(workouts)
        val progress = if (goal > 0) (currentWeekKcal.toFloat() / goal.toFloat()).coerceIn(0f, 1f) else 0f

        return HomeUiState(
            workouts = workouts,
            routines = routines, // Pass routines to UI
            totalWorkouts = workouts.size,
            totalCalories = totalCalories,
            gymCount = gymCount,
            poolCount = poolCount,
            weeklyGoalKcal = goal,
            currentWeekKcal = currentWeekKcal,
            progress = progress
        )
    }

    private fun calculateCurrentWeekCalories(workouts: List<Workout>): Int {
        val now = LocalDate.now()
        val weekFields = WeekFields.of(Locale.getDefault())
        val currentWeek = now.get(weekFields.weekOfWeekBasedYear())
        val currentYear = now.get(weekFields.weekBasedYear())

        return workouts
            .filter {
                val date = it.date.toLocalDate()
                val w = date.get(weekFields.weekOfWeekBasedYear())
                val y = date.get(weekFields.weekBasedYear())
                w == currentWeek && y == currentYear
            }
            .sumOf { it.caloriesBurned }
    }

    private fun isPool(type: String): Boolean {
        return type.contains("basen", ignoreCase = true) || type.equals("POOL", ignoreCase = true)
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            deleteWorkoutUseCase(workout)
        }
    }

    fun updateWeeklyGoal(newGoal: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateWeeklyGoal(newGoal)
        }
    }

    fun startRoutine(routine: Routine, onWorkoutCreated: (Int) -> Unit) {
        viewModelScope.launch {
            val workoutId = createWorkoutFromRoutineUseCase(routine)
            onWorkoutCreated(workoutId)
        }
    }
}