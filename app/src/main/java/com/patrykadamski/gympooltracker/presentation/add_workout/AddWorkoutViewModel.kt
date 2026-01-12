package com.patrykadamski.gympooltracker.presentation.add_workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.model.WorkoutType
import com.patrykadamski.gympooltracker.domain.usecase.GetWorkoutByIdUseCase
import com.patrykadamski.gympooltracker.domain.usecase.GetWorkoutTypesUseCase
import com.patrykadamski.gympooltracker.domain.usecase.InsertWorkoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class AddWorkoutViewModel @Inject constructor(
    private val insertWorkoutUseCase: InsertWorkoutUseCase,
    private val getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
    private val getWorkoutTypesUseCase: GetWorkoutTypesUseCase, // Nowy UseCase
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutId: Int? = savedStateHandle.get<Int>("workoutId")

    // Pobieramy dostępne typy z bazy (StateFlow)
    val availableTypes: StateFlow<List<WorkoutType>> = getWorkoutTypesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Zamiast Stringa, przechowujemy wybrany obiekt typu.
    // Na start null, ustawimy go jak pobierzemy dane lub wczytamy trening
    var selectedType by mutableStateOf<WorkoutType?>(null)

    var startTime by mutableStateOf(LocalTime.now().minusHours(1))
    var endTime by mutableStateOf(LocalTime.now())
    var notes by mutableStateOf("")
    var isEditMode by mutableStateOf(false)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveSuccess : UiEvent()
    }

    init {
        if (workoutId != null && workoutId != -1) {
            loadWorkout(workoutId)
        } else {
            // Jeśli to nowy trening, spróbuj ustawić domyślny typ (np. pierwszy z listy)
            // Robimy to w collect w widoku lub tutaj nasłuchując zmian availableTypes
            viewModelScope.launch {
                getWorkoutTypesUseCase().collect { types ->
                    if (selectedType == null && types.isNotEmpty()) {
                        selectedType = types.first()
                    }
                }
            }
        }
    }

    private fun loadWorkout(id: Int) {
        viewModelScope.launch {
            getWorkoutByIdUseCase(id)?.let { workout ->
                // Musimy znaleźć typ pasujący nazwą do tego z zapisanego treningu
                // UWAGA: To uproszczenie. W idealnym świecie Workout trzymałby ID typu.
                // Tutaj szukamy po nazwie.
                getWorkoutTypesUseCase().collect { types ->
                    selectedType = types.find { it.name.equals(workout.type, ignoreCase = true) }
                        ?: types.firstOrNull()
                }

                startTime = workout.date.toLocalTime()
                endTime = workout.date.toLocalTime().plusMinutes(workout.durationMinutes.toLong())
                notes = workout.notes
                isEditMode = true
            }
        }
    }

    fun saveWorkout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentType = selectedType
            if (currentType == null) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Wybierz typ treningu"))
                return@launch
            }

            val duration = ChronoUnit.MINUTES.between(startTime, endTime).toInt()
            if (duration <= 0) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Czas zakończenia musi być późniejszy niż rozpoczęcia"))
                return@launch
            }

            val workoutDate = LocalDateTime.of(LocalDate.now(), startTime)

            // Obliczamy kalorie dynamicznie!
            val calories = duration * currentType.caloriesPerMinute

            val workout = Workout(
                id = if (isEditMode) workoutId ?: 0 else 0,
                type = currentType.name, // Zapisujemy nazwę (np. "Siłownia")
                durationMinutes = duration,
                caloriesBurned = calories,
                date = workoutDate,
                notes = notes
            )

            insertWorkoutUseCase(workout)
            _eventFlow.emit(UiEvent.SaveSuccess)
            onSuccess()
        }
    }
}