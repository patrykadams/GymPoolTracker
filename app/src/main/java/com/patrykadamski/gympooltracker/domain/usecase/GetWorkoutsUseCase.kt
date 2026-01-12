package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWorkoutsUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    // Operator invoke pozwala wywoływać klasę jak funkcję:
    // getWorkoutsUseCase() zamiast getWorkoutsUseCase.execute()
    operator fun invoke(): Flow<List<Workout>> {
        return repository.getWorkouts()
    }
}