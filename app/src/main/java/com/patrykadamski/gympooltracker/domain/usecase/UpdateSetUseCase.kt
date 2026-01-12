package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository

class UpdateSetUseCase(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(set: GymSet) {
        repository.updateSet(set)
    }
}