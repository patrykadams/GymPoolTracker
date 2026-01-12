// file: app/src/main/java/com/patrykadamski/gympooltracker/data/repository/RoutineRepositoryImpl.kt
package com.patrykadamski.gympooltracker.data.repository

import com.patrykadamski.gympooltracker.data.local.RoutineDao
import com.patrykadamski.gympooltracker.data.local.RoutineEntity
import com.patrykadamski.gympooltracker.data.local.RoutineExerciseEntity
import com.patrykadamski.gympooltracker.domain.model.Routine
import com.patrykadamski.gympooltracker.domain.model.RoutineExercise
import com.patrykadamski.gympooltracker.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoutineRepositoryImpl(
    private val dao: RoutineDao
) : RoutineRepository {

    override fun getRoutines(): Flow<List<Routine>> {
        return dao.getRoutinesWithExercises().map { entities ->
            entities.map { entity ->
                Routine(
                    id = entity.routine.id,
                    name = entity.routine.name,
                    description = entity.routine.description,
                    exercises = entity.exercises.map { exEntity ->
                        RoutineExercise(
                            id = exEntity.id,
                            name = exEntity.name,
                            sets = exEntity.sets,
                            reps = exEntity.reps,
                            targetRpe = exEntity.targetRpe,
                            orderIndex = exEntity.orderIndex
                        )
                    }.sortedBy { it.orderIndex }
                )
            }
        }
    }

    override suspend fun createRoutine(routine: Routine): Long {
        // 1. Insert Routine Entity
        val routineEntity = RoutineEntity(
            name = routine.name,
            description = routine.description
        )
        val routineId = dao.insertRoutine(routineEntity)

        // 2. Insert Exercise Entities linked to routineId
        val exerciseEntities = routine.exercises.map { ex ->
            RoutineExerciseEntity(
                routineId = routineId,
                name = ex.name,
                sets = ex.sets,
                reps = ex.reps,
                targetRpe = ex.targetRpe,
                orderIndex = ex.orderIndex
            )
        }
        dao.insertExercises(exerciseEntities)

        return routineId
    }
}