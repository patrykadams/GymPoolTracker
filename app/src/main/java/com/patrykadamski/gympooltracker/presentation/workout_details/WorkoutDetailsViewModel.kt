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

    // Holds the current state of the workout (exercises, sets, etc.)
    private val _state = MutableStateFlow<WorkoutDetails?>(null)
    val state: StateFlow<WorkoutDetails?> = _state.asStateFlow()

    // Retrieve the workout ID passed from the previous screen
    private val workoutId: Int = checkNotNull(savedStateHandle["workoutId"])

    init {
        loadWorkoutDetails()
    }

    private fun loadWorkoutDetails() {
        viewModelScope.launch {
            // Observe the workout details from the database in real-time
            repository.getWorkoutDetails(workoutId).collect { details ->
                _state.value = details
            }
        }
    }

    // Called when user types a name and clicks "Add" in the dialog
    fun addExercise(name: String) {
        viewModelScope.launch {
            repository.addExercise(workoutId, name)
        }
    }

    // Called when user clicks "Add Set" button
    fun addSet(exerciseId: Int) {
        viewModelScope.launch {
            repository.addSet(exerciseId)
        }
    }

    // Called when user changes weight, reps, or completion status
    fun updateSet(setId: Int, reps: String, weight: Double, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateSet(setId, reps, weight, isCompleted)
        }
    }

    // Called when user swipes or clicks delete on a set
    fun deleteSet(setId: Int) {
        viewModelScope.launch {
            repository.deleteSet(setId)
        }
    }

    // Called when user deletes an entire exercise
    fun deleteExercise(exerciseId: Int) {
        viewModelScope.launch {
            repository.deleteExercise(exerciseId)
        }
    }
}