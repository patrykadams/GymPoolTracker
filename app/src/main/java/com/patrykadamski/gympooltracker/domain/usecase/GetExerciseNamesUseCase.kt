package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

/**
 * Retrieves a list of all unique exercise names used in previous workouts.
 * Used for autocomplete suggestions.
 */
class GetExerciseNamesUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<String>> {
        return repository.getExerciseNames()
    }
}