package com.patrykadamski.gympooltracker.domain.model

data class GymSet(
    val id: Long = 0,
    val exerciseId: Long,
    val setNumber: Int,
    val reps: String,
    val weight: Double,
    val rpe: Double,
    val restSeconds: Int,
    val isCompleted: Boolean
)

data class GymExercise(
    val id: Long = 0,
    val workoutId: Int,
    val name: String,
    val sets: List<GymSet> = emptyList(),
    val personalRecord: Double? = null
)

//
data class WorkoutDetails(
    val workout: Workout,
    val exercises: List<WorkoutExercise>
)