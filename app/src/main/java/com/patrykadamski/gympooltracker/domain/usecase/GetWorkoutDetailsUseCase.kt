package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

class GetWorkoutDetailsUseCase(
    private val repository: WorkoutRepository
) {
    operator fun invoke(workoutId: Int): Flow<WorkoutDetails?> {
        return repository.getWorkoutDetails(workoutId)
    }
}