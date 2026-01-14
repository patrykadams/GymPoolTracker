// file: app/src/main/java/com/patrykadamski/gympooltracker/data/repository/RoutineRepositoryImpl.kt
package com.patrykadamski.gympooltracker.data.repository

import com.patrykadamski.gympooltracker.data.local.RoutineDao
import com.patrykadamski.gympooltracker.data.local.RoutineEntity
import com.patrykadamski.gympooltracker.data.local.RoutineExerciseEntity
import com.patrykadamski.gympooltracker.data.local.RoutineWithExercises
import com.patrykadamski.gympooltracker.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow

class RoutineRepositoryImpl(
    private val dao: RoutineDao
) : RoutineRepository {

    override fun getAllRoutines(): Flow<List<RoutineWithExercises>> {
        return dao.getRoutinesWithExercises()
    }

    override suspend fun insertRoutine(routine: RoutineEntity, exercises: List<RoutineExerciseEntity>) {
        // 1. Insert Routine and get its ID
        val routineId = dao.insertRoutine(routine)

        // 2. Assign that ID to all exercises
        val exercisesWithId = exercises.map { it.copy(routineId = routineId) }

        // 3. Insert exercises
        dao.insertExercises(exercisesWithId)
    }

    override suspend fun deleteRoutine(routineId: Long) {
        dao.deleteRoutine(routineId)
    }
}