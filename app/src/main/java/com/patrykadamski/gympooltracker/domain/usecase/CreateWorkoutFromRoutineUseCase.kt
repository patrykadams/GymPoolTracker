package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.model.Routine
import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import java.time.LocalDateTime
import javax.inject.Inject

class CreateWorkoutFromRoutineUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {

    suspend operator fun invoke(routine: Routine): Int {
        // 1. Create a new workout entry using the routine's name as the workout type
        // FIX: Changed 'routine.type' to 'routine.name'
        val workoutId = repository.createWorkout(routine.name)

        // 2. Add exercises from the routine to the new workout
        routine.exercises.forEach { routineExercise ->
            repository.addExercise(workoutId, routineExercise.name)
        }

        return workoutId
    }

    // Helper function (if needed for internal logic)
    private fun createInitialWorkout(id: Int, type: String): Workout {
        return Workout(
            id = id,
            type = type,
            date = LocalDateTime.now(),
            duration = 0L,
            calories = 0
        )
    }
}