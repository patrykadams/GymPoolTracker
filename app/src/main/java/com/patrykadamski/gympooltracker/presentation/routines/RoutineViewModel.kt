// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/routines/RoutineViewModel.kt
package com.patrykadamski.gympooltracker.presentation.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykadamski.gympooltracker.data.local.RoutineEntity
import com.patrykadamski.gympooltracker.data.local.RoutineExerciseEntity
import com.patrykadamski.gympooltracker.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val repository: RoutineRepository
) : ViewModel() {

    // Load routines from DB
    val routines = repository.getAllRoutines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createRoutine(name: String, description: String) {
        viewModelScope.launch {
            val newRoutine = RoutineEntity(
                name = name,
                description = description
            )
            // Example empty exercises for now
            val exercises = emptyList<RoutineExerciseEntity>()

            repository.insertRoutine(newRoutine, exercises)
        }
    }

    fun deleteRoutine(routineId: Long) {
        viewModelScope.launch {
            repository.deleteRoutine(routineId)
        }
    }
}