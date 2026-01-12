// file: app/src/main/java/com/patrykadamski/gympooltracker/domain/usecase/CreateWorkoutFromRoutineUseCase.kt
package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.model.Routine
import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import java.time.LocalDateTime
import javax.inject.Inject

class CreateWorkoutFromRoutineUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    /**
     * Creates a new workout based on the selected routine.
     * 1. Creates a Workout entry.
     * 2. Copies exercises from Routine to Workout.
     * 3. Pre-fills sets for each exercise (with 0 weight).
     * Returns the ID of the newly created workout.
     */
    suspend operator fun invoke(routine: Routine): Int {
        // 1. Create Workout
        val workout = Workout(
            type = routine.name, // Use routine name as workout type/title
            durationMinutes = 0,
            caloriesBurned = 0,
            date = LocalDateTime.now(),
            notes = "Started from: ${routine.name}"
        )
        val workoutId = repository.insertWorkout(workout).toInt()

        // 2. Add Exercises and Sets
        routine.exercises.forEach { routineExercise ->
            // Add Exercise
            val exerciseId = repository.addExercise(workoutId, routineExercise.name)

            // Add Sets (Empty placeholders based on routine template)
            repeat(routineExercise.sets) { setIndex ->
                repository.addSet(
                    exerciseId = exerciseId,
                    setNumber = setIndex + 1,
                    reps = routineExercise.reps,
                    weight = 0.0,
                    restSeconds = 90 // Default rest
                )
            }
        }

        return workoutId
    }
}