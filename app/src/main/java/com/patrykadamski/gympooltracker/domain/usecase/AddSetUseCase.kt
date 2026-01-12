package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository

/**
 * Use case to add a new set.
 * Now supports optional parameters to enable "Smart Add" functionality (copying previous set data).
 */
class AddSetUseCase(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(
        exerciseId: Long,
        setNumber: Int,
        reps: String = "0",
        weight: Double = 0.0,
        restSeconds: Int = 60
    ) {
        repository.addSet(exerciseId, setNumber, reps, weight, restSeconds)
    }
}