package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository

class DeleteExerciseUseCase(private val repository: WorkoutRepository) {
    suspend operator fun invoke(exerciseId: Long, workoutId: Int) = repository.deleteExercise(exerciseId, workoutId)
}