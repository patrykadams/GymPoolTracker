package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository

class DeleteExerciseUseCase(private val repository: WorkoutRepository) {
    // FIX: Removed 'workoutId' parameter to match Repository signature
    suspend operator fun invoke(exerciseId: Long) = repository.deleteExercise(exerciseId)
}