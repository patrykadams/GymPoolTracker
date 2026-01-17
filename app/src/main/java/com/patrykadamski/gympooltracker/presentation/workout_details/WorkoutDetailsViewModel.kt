// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/workout_details/WorkoutDetailsViewModel.kt
package com.patrykadamski.gympooltracker.presentation.workout_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutDetailsViewModel @Inject constructor(
    private val repository: WorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // State holding the workout details (workout info + exercises + sets)
    private val _state = MutableStateFlow<WorkoutDetails?>(null)
    val state: StateFlow<WorkoutDetails?> = _state.asStateFlow()

    init {
        // Retrieve 'workoutId' from the navigation arguments automatically via SavedStateHandle
        val workoutId = savedStateHandle.get<Int>("workoutId")
        if (workoutId != null) {
            loadWorkoutDetails(workoutId)
        }
    }

    private fun loadWorkoutDetails(id: Int) {
        viewModelScope.launch {
            repository.getWorkoutDetails(id).collect { details ->
                _state.value = details
            }
        }
    }
}