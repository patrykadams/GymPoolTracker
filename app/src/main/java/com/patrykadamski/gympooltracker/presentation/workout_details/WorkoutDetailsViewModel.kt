// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/workout_details/WorkoutDetailsViewModel.kt
package com.patrykadamski.gympooltracker.presentation.workout_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykadamski.gympooltracker.domain.model.ExerciseWithSets
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutDetailsViewModel @Inject constructor(
    private val getWorkoutDetailsUseCase: GetWorkoutDetailsUseCase,
    private val addExerciseUseCase: AddExerciseUseCase,
    private val addSetUseCase: AddSetUseCase,
    private val updateSetUseCase: UpdateSetUseCase,
    private val toggleSetCompletionUseCase: ToggleSetCompletionUseCase,
    private val deleteSetUseCase: DeleteSetUseCase,
    private val deleteExerciseUseCase: DeleteExerciseUseCase,
    private val getExerciseNamesUseCase: GetExerciseNamesUseCase,
    private val getPersonalRecordUseCase: GetPersonalRecordUseCase,
    private val saveWorkoutAsRoutineUseCase: SaveWorkoutAsRoutineUseCase, // NEW
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutId: Int = checkNotNull(savedStateHandle["workoutId"])

    private val _uiState = MutableStateFlow<WorkoutDetailsUiState>(WorkoutDetailsUiState.Loading)
    val uiState: StateFlow<WorkoutDetailsUiState> = _uiState.asStateFlow()

    private val _exerciseSuggestions = MutableStateFlow<List<String>>(emptyList())
    val exerciseSuggestions: StateFlow<List<String>> = _exerciseSuggestions.asStateFlow()

    init {
        loadWorkoutDetails()
        loadExerciseSuggestions()
    }

    private fun loadWorkoutDetails() {
        viewModelScope.launch {
            val details = getWorkoutDetailsUseCase(workoutId)
            if (details != null) {
                // Fetch PRs for each exercise
                val exercisesWithPrs = details.exercises.map { exercise ->
                    val pr = getPersonalRecordUseCase(exercise.exerciseName)
                    exercise.copy(personalRecord = pr)
                }
                _uiState.value = WorkoutDetailsUiState.Success(
                    details = details.copy(exercises = exercisesWithPrs)
                )
            } else {
                _uiState.value = WorkoutDetailsUiState.Error("Workout not found")
            }
        }
    }

    private fun loadExerciseSuggestions() {
        viewModelScope.launch {
            _exerciseSuggestions.value = getExerciseNamesUseCase()
        }
    }

    fun addExercise(name: String) {
        viewModelScope.launch {
            addExerciseUseCase(workoutId, name)
            loadWorkoutDetails()
        }
    }

    fun addSet(exerciseId: Long, previousSetWeight: Double?, previousSetReps: String?) {
        viewModelScope.launch {
            val weight = previousSetWeight ?: 0.0
            val reps = previousSetReps ?: "0"
            addSetUseCase(exerciseId, reps, weight)
            loadWorkoutDetails()
        }
    }

    fun updateSet(setId: Long, reps: String, weight: String) {
        viewModelScope.launch {
            val weightValue = weight.toDoubleOrNull() ?: 0.0
            updateSetUseCase(setId, reps, weightValue)
            loadWorkoutDetails()
        }
    }

    fun toggleSetCompletion(setId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            toggleSetCompletionUseCase(setId, isCompleted)
            loadWorkoutDetails()
        }
    }

    fun deleteSet(setId: Long) {
        viewModelScope.launch {
            deleteSetUseCase(setId)
            loadWorkoutDetails()
        }
    }

    fun deleteExercise(exerciseId: Long) {
        viewModelScope.launch {
            deleteExerciseUseCase(exerciseId)
            loadWorkoutDetails()
        }
    }

    // NEW FUNCTION
    fun saveAsRoutine(name: String) {
        viewModelScope.launch {
            saveWorkoutAsRoutineUseCase(workoutId, name)
            // Optionally: Show a success message (One-time event)
        }
    }
}

sealed class WorkoutDetailsUiState {
    data object Loading : WorkoutDetailsUiState()
    data class Success(val details: WorkoutDetails) : WorkoutDetailsUiState()
    data class Error(val message: String) : WorkoutDetailsUiState()
}