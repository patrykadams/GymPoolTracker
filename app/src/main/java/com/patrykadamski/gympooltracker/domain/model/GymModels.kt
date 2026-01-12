package com.patrykadamski.gympooltracker.domain.model

/**
 * Represents a single set performed within an exercise.
 * Contains performance metrics like weight, reps, and RPE.
 */
data class GymSet(
    val id: Long = 0,
    val exerciseId: Long,
    val setNumber: Int,
    val reps: String,        // String to allow ranges (e.g., "8-12") or failure markers
    val weight: Double,      // Weight in kg
    val rpe: Double,         // Rate of Perceived Exertion (1-10)
    val restSeconds: Int,    // Rest time after this set
    val isCompleted: Boolean
)

/**
 * Represents a specific exercise (e.g., "Bench Press") containing a list of sets.
 */
data class GymExercise(
    val id: Long = 0,
    val workoutId: Int,
    val name: String,
    val sets: List<GymSet> = emptyList()
)

/**
 * Aggregate model representing a full workout session with all its exercises and sets.
 * Used primarily by the UI to display the workout details screen.
 */
data class WorkoutDetails(
    val workout: Workout,
    val exercises: List<GymExercise>
)