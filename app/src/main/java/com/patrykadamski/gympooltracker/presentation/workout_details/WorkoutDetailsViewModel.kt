// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/workout_details/WorkoutDetailsViewModel.kt
package com.patrykadamski.gympooltracker.presentation.workout_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import com.patrykadamski.gympooltracker.domain.usecase.SaveWorkoutAsRoutineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutDetailsVM @Inject constructor(
    private val repository: WorkoutRepository,
    private val saveWorkoutAsRoutineUseCase: SaveWorkoutAsRoutineUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutId: Int = checkNotNull(savedStateHandle["workoutId"])

    private val _uiState = MutableStateFlow(WorkoutDetailsUiState())
    val uiState = _uiState.asStateFlow()

    // NEW: Timer State
    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()

    private var timerJob: Job? = null

    val workoutDetails: StateFlow<WorkoutDetails?> = repository.getWorkoutDetails(workoutId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val exerciseSuggestions: StateFlow<List<String>> = flowOf(
        listOf("Bench Press", "Squat", "Deadlift", "Pull Up", "Overhead Press", "Barbell Row")
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addExercise(name: String) {
        viewModelScope.launch {
            repository.addExercise(workoutId, name)
        }
    }

    fun deleteExercise(exerciseId: Long) {
        viewModelScope.launch {
            repository.deleteExercise(exerciseId, workoutId)
        }
    }

    fun addSet(exerciseId: Long, currentSetCount: Int) {
        viewModelScope.launch {
            repository.addSet(
                exerciseId = exerciseId,
                setNumber = currentSetCount + 1,
                reps = "0",
                weight = 0.0,
                restSeconds = 60
            )
        }
    }

    fun updateSet(set: GymSet) {
        viewModelScope.launch {
            repository.updateSet(set)
        }
    }

    // NEW: Toggle set completion and start timer if completed
    fun toggleSetCompletion(set: GymSet, isCompleted: Boolean) {
        val updatedSet = set.copy(isCompleted = isCompleted)
        updateSet(updatedSet)

        if (isCompleted) {
            startTimer(set.restSeconds)
        } else {
            // Optional: Cancel timer if unchecked?
            // cancelTimer()
        }
    }

    fun deleteSet(set: GymSet) {
        viewModelScope.launch {
            repository.deleteSet(set)
        }
    }

    fun updateDistance(distance: Int) {
        val currentDetails = workoutDetails.value ?: return
        val updatedWorkout = currentDetails.workout.copy(distanceMeters = distance)
        viewModelScope.launch {
            repository.insertWorkout(updatedWorkout)
        }
    }

    // --- Timer Logic ---

    fun startTimer(seconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _timerState.value = TimerState(remainingSeconds = seconds, isRunning = true, totalSeconds = seconds)
            while (_timerState.value.remainingSeconds > 0) {
                delay(1000L)
                _timerState.value = _timerState.value.copy(
                    remainingSeconds = _timerState.value.remainingSeconds - 1
                )
            }
            _timerState.value = _timerState.value.copy(isRunning = false)
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    fun addTime(seconds: Int) {
        _timerState.value = _timerState.value.copy(
            remainingSeconds = _timerState.value.remainingSeconds + seconds
        )
    }

    // --- Routine Dialog ---

    fun showSaveRoutineDialog() {
        _uiState.value = _uiState.value.copy(isSaveRoutineDialogVisible = true)
    }

    fun hideSaveRoutineDialog() {
        _uiState.value = _uiState.value.copy(isSaveRoutineDialogVisible = false)
    }

    fun saveAsRoutine(routineName: String) {
        val details = workoutDetails.value ?: return

        viewModelScope.launch {
            saveWorkoutAsRoutineUseCase(routineName, details)
            hideSaveRoutineDialog()
        }
    }
}

data class WorkoutDetailsUiState(
    val isSaveRoutineDialogVisible: Boolean = false
)

data class TimerState(
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 0,
    val isRunning: Boolean = false
)