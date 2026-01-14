// file: app/src/main/java/com/patrykadamski/gympooltracker/domain/model/WorkoutExercise.kt
package com.patrykadamski.gympooltracker.domain.model

data class WorkoutExercise(
    val id: Long,
    val name: String,
    val sets: List<GymSet>
)