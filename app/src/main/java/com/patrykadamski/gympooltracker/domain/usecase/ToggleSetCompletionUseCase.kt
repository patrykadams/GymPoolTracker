package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository

class ToggleSetCompletionUseCase(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(set: GymSet) {
        // Tworzymy kopię ze zmienionym statusem
        val updatedSet = set.copy(isCompleted = !set.isCompleted)
        repository.updateSet(updatedSet)
    }
}