// file: app/src/main/java/com/patrykadamski/gympooltracker/domain/usecase/GetLastSetForExerciseUseCase.kt
package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import javax.inject.Inject

class GetLastSetForExerciseUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    /**
     * Retrieves the last recorded set for a given exercise name across all history.
     * Useful for suggesting weights for a new workout.
     */
    suspend operator fun invoke(exerciseName: String): GymSet? {
        return repository.getLastSetForExercise(exerciseName)
    }
}