package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import javax.inject.Inject

class DeleteSetUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(set: GymSet) {
        // FIX: Extract the ID from the GymSet object and convert it to Long.
        // The repository expects an ID, not the whole object.
        repository.deleteSet(set.id.toLong())
    }
}