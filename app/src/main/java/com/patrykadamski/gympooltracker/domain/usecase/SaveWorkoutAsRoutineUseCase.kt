// file: app/src/main/java/com/patrykadamski/gympooltracker/domain/usecase/SaveWorkoutAsRoutineUseCase.kt
package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.model.Routine
import com.patrykadamski.gympooltracker.domain.model.RoutineExercise
import com.patrykadamski.gympooltracker.domain.repository.RoutineRepository
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SaveWorkoutAsRoutineUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val routineRepository: RoutineRepository
) {
    /**
     * Creates a new Routine based on an existing Workout.
     * @param workoutId ID of the source workout
     * @param routineName Name given by the user
     */
    suspend operator fun invoke(workoutId: Int, routineName: String) {
        // 1. Fetch full workout details
        // FIX: Collect the Flow using firstOrNull() to get the actual data object
        val details = workoutRepository.getWorkoutDetails(workoutId).firstOrNull() ?: return

        // 2. Generate Description (e.g. "Squat / Bench Press / ...")
        // FIX: Use 'name' property from GymExercise
        val description = details.exercises
            .take(3)
            .joinToString(separator = " / ") { it.name }
            .let { if (details.exercises.size > 3) "$it..." else it }

        // 3. Map Exercises
        val routineExercises = details.exercises.mapIndexed { index, gymExercise ->
            // Determine sets count
            val setsCount = gymExercise.sets.size

            // Determine representative reps (take from first set or default to "8-12")
            val representativeReps = gymExercise.sets.firstOrNull()?.reps ?: "10"

            RoutineExercise(
                id = 0, // Auto-generated
                name = gymExercise.name, // FIX: Property is 'name' in GymExercise
                sets = if (setsCount == 0) 3 else setsCount, // Default to 3 if no sets added yet
                reps = representativeReps,
                targetRpe = "", // Default empty
                orderIndex = index + 1
            )
        }

        // 4. Create Routine Object
        val routine = Routine(
            id = 0,
            name = routineName,
            description = description,
            exercises = routineExercises
        )

        // 5. Save to Repo
        routineRepository.createRoutine(routine)
    }
}