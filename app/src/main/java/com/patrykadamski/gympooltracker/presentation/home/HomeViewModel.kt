// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/home/HomeViewModel.kt
package com.patrykadamski.gympooltracker.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykadamski.gympooltracker.data.local.RoutineWithExercises // FIX: Import the correct Data class
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import com.patrykadamski.gympooltracker.domain.usecase.GetRoutinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    workoutRepository: WorkoutRepository,
    getRoutinesUseCase: GetRoutinesUseCase
) : ViewModel() {

    // Recent workouts list
    val recentWorkouts = workoutRepository.getWorkouts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // FIX: Explicitly use StateFlow<List<RoutineWithExercises>> to match the UseCase return type
    val routines: StateFlow<List<RoutineWithExercises>> = getRoutinesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}