// file: app/src/main/java/com/patrykadamski/gympooltracker/domain/repository/RoutineRepository.kt
package com.patrykadamski.gympooltracker.domain.repository

import com.patrykadamski.gympooltracker.data.local.RoutineEntity
import com.patrykadamski.gympooltracker.data.local.RoutineExerciseEntity
import com.patrykadamski.gympooltracker.data.local.RoutineWithExercises
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {
    fun getAllRoutines(): Flow<List<RoutineWithExercises>>

    suspend fun insertRoutine(routine: RoutineEntity, exercises: List<RoutineExerciseEntity>)

    suspend fun deleteRoutine(routineId: Long)
}