package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to retrieve the Personal Record (max weight) for a given exercise.
 */
class GetPersonalRecordUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(exerciseName: String): Flow<Double?> {
        return repository.getPersonalRecord(exerciseName)
    }
}