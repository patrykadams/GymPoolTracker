package com.patrykadamski.gympooltracker.presentation.workout_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykadamski.gympooltracker.domain.model.DefaultExercises
import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TimerState(
    val isRunning: Boolean = false,
    val timeLeft: Int = 0,
    val totalTime: Int = 1
)

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
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutId: Int = checkNotNull(savedStateHandle["workoutId"])

    val uiState: StateFlow<WorkoutDetails?> = getWorkoutDetailsUseCase(workoutId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /**
     * Combined list of exercises:
     * 1. Exercises from history (DB)
     * 2. Default built-in exercises
     * Filtered for uniqueness and sorted alphabetically.
     */
    val exerciseHistory: StateFlow<List<String>> = combine(
        getExerciseNamesUseCase(),
        flowOf(DefaultExercises.all)
    ) { history, defaults ->
        (history + defaults).distinct().sorted()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DefaultExercises.all // Start with defaults immediately
    )

    // --- Personal Records Cache ---
    private val _personalRecords = MutableStateFlow<Map<Long, Double?>>(emptyMap())
    val personalRecords = _personalRecords.asStateFlow()

    // --- Timer State ---
    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()
    private var timerJob: Job? = null

    init {
        // Observe exercises to fetch their PRs automatically
        viewModelScope.launch {
            uiState.collect { details ->
                details?.exercises?.forEach { exercise ->
                    loadPersonalRecord(exercise.id, exercise.name)
                }
            }
        }
    }

    private fun loadPersonalRecord(exerciseId: Long, name: String) {
        viewModelScope.launch {
            getPersonalRecordUseCase(name).collect { pr ->
                _personalRecords.value = _personalRecords.value.toMutableMap().apply {
                    put(exerciseId, pr)
                }
            }
        }
    }

    // --- Actions ---

    fun addExercise(name: String) {
        viewModelScope.launch { addExerciseUseCase(workoutId, name) }
    }

    fun addSet(exerciseId: Long, currentSets: List<GymSet>) {
        viewModelScope.launch {
            val lastSet = currentSets.lastOrNull()
            val newSetNumber = currentSets.size + 1

            if (lastSet != null) {
                addSetUseCase(
                    exerciseId = exerciseId,
                    setNumber = newSetNumber,
                    reps = lastSet.reps,
                    weight = lastSet.weight,
                    restSeconds = lastSet.restSeconds
                )
            } else {
                addSetUseCase(exerciseId = exerciseId, setNumber = newSetNumber)
            }
        }
    }

    fun updateSet(set: GymSet, reps: String, weight: Double, rpe: Double) {
        viewModelScope.launch {
            val updatedSet = set.copy(reps = reps, weight = weight, rpe = rpe)
            updateSetUseCase(updatedSet)
        }
    }

    fun updateSetRestTime(set: GymSet, newRestSeconds: Int) {
        viewModelScope.launch {
            val updatedSet = set.copy(restSeconds = newRestSeconds)
            updateSetUseCase(updatedSet)
        }
    }

    fun toggleSetCompleted(set: GymSet) {
        viewModelScope.launch {
            if (!set.isCompleted) {
                startTimer(set.restSeconds)
            }
            toggleSetCompletionUseCase(set)
        }
    }

    fun deleteSet(set: GymSet) {
        viewModelScope.launch { deleteSetUseCase(set) }
    }

    fun deleteExercise(exerciseId: Long) {
        viewModelScope.launch { deleteExerciseUseCase(exerciseId, workoutId) }
    }

    // --- Timer Logic ---

    private fun startTimer(seconds: Int) {
        stopTimer()
        if (seconds <= 0) return

        timerJob = viewModelScope.launch {
            _timerState.value = TimerState(isRunning = true, timeLeft = seconds, totalTime = seconds)
            while (_timerState.value.timeLeft > 0) {
                delay(1000)
                _timerState.value = _timerState.value.copy(
                    timeLeft = _timerState.value.timeLeft - 1
                )
            }
            stopTimer()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState(isRunning = false)
    }

    fun addTime(seconds: Int) {
        val current = _timerState.value
        if (current.isRunning) {
            _timerState.value = current.copy(
                timeLeft = current.timeLeft + seconds,
                totalTime = current.totalTime + seconds
            )
        }
    }
}