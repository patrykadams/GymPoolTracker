// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/add_workout/AddWorkoutViewModel.kt
package com.patrykadamski.gympooltracker.presentation.add_workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.model.WorkoutType
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddWorkoutViewModel @Inject constructor(
    private val repository: WorkoutRepository
) : ViewModel() {

    // Helper state to hold available workout types.
    // FIX: Added 'caloriesPerMinute' parameter to match the data class definition.
    val workoutTypes: StateFlow<List<WorkoutType>> = kotlinx.coroutines.flow.flowOf(
        listOf(
            WorkoutType(id = 1, name = "Strength", iconName = "dumbbell", caloriesPerMinute = 5),
            WorkoutType(id = 2, name = "Cardio", iconName = "run", caloriesPerMinute = 8),
            WorkoutType(id = 3, name = "Swimming", iconName = "pool", caloriesPerMinute = 10)
        )
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun createWorkout(type: WorkoutType, onWorkoutCreated: (Long) -> Unit) {
        viewModelScope.launch {
            // Create a new workout object
            val newWorkout = Workout(
                id = 0, // 0 means auto-generate ID
                type = type.name, // Storing type name as String
                date = System.currentTimeMillis(),
                durationMinutes = 0,
                caloriesBurned = 0,
                notes = ""
            )

            // Insert into repository and get the new ID
            val newId = repository.insertWorkout(newWorkout)

            // Navigate to the details screen for this new workout
            onWorkoutCreated(newId)
        }
    }
}