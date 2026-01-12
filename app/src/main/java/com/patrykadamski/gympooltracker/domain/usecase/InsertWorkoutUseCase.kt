package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import javax.inject.Inject

class InsertWorkoutUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    /**
     * Zapisuje nowy trening w bazie danych.
     * Walidacja danych powinna nastąpić przed wywołaniem tej metody (w ViewModelu)
     * lub wewnątrz niej, jeśli są to reguły biznesowe (np. nie można trenować w przyszłości).
     */
    suspend operator fun invoke(workout: Workout) {
        repository.insertWorkout(workout)
    }
}