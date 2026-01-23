package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import javax.inject.Inject

class AddSetUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    // FIX: Updated to accept only exerciseId (Long), matching the Repository signature
    suspend operator fun invoke(exerciseId: Long) {
        repository.addSet(exerciseId)
    }
}