package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.model.WorkoutType
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

class GetWorkoutTypesUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<WorkoutType>> {
        return repository.getWorkoutTypes()
    }
}