package com.patrykadamski.gympooltracker.domain.model

data class Routine(
    val id: Long,
    val name: String,
    val description: String,
    val exercises: List<RoutineExercise>
)

data class RoutineExercise(
    val id: Long,
    val name: String,
    val sets: Int,
    val reps: String,
    val targetRpe: String,
    val orderIndex: Int
)