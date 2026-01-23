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
        // 1. Create a new workout entry based on the routine type
        val workoutId = repository.createWorkout(routine.type)

        // 2. Add exercises from the routine to the new workout
        routine.exercises.forEach { routineExercise ->
            // Add the exercise
            repository.addExercise(workoutId, routineExercise.name)

            // Note: Currently repository.addExercise doesn't return the new exercise ID,
            // so we cannot immediately add sets here without refactoring the repository.
            // For the MVP, we just add the exercises. The user can add sets manually.
        }

        return workoutId
    }

    // Helper function to create a domain object (if used internally)
    private fun createInitialWorkout(id: Int, type: String): Workout {
        return Workout(
            id = id,
            type = type,
            date = LocalDateTime.now(),
            // FIX: Updated parameter names to match Workout.kt
            duration = 0L,     // Was durationMinutes
            calories = 0       // Was caloriesBurned
        )
    }
}