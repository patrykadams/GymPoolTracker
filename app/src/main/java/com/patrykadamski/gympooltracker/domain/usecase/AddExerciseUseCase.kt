package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository

class AddExerciseUseCase(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(workoutId: Int, name: String) {
        repository.addExercise(workoutId, name)
    }
}