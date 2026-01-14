// file: app/src/main/java/com/patrykadamski/gympooltracker/domain/usecase/SaveWorkoutAsRoutineUseCase.kt
package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.data.local.RoutineEntity
import com.patrykadamski.gympooltracker.data.local.RoutineExerciseEntity
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.repository.RoutineRepository
import javax.inject.Inject

class SaveWorkoutAsRoutineUseCase @Inject constructor(
    private val repository: RoutineRepository
) {
    suspend operator fun invoke(name: String, workoutDetails: WorkoutDetails) {
        // 1. Create the Routine Entity
        val routine = RoutineEntity(
            name = name,
            description = "Created from workout on ${workoutDetails.workout.date}"
        )

        // 2. Map Workout Exercises to Routine Exercises
        val routineExercises = workoutDetails.exercises.mapIndexed { index, exercise ->
            // Logic to determine target reps/sets based on what was done
            val setsCount = exercise.sets.size
            // Create a string representation of reps (e.g., "10" or "8-12")
            // Taking the reps from the first set as a default, or "10" if empty
            val representativeReps = exercise.sets.firstOrNull()?.reps ?: "10"

            RoutineExerciseEntity(
                routineId = 0, // Repository will assign this
                name = exercise.name,
                sets = setsCount,
                reps = representativeReps,
                targetRpe = "8.0", // Default target RPE
                orderIndex = index
            )
        }

        // FIX: Method name is 'insertRoutine', not 'createRoutine'
        repository.insertRoutine(routine, routineExercises)
    }
}