// file: app/src/main/java/com/patrykadamski/gympooltracker/domain/model/Workout.kt
package com.patrykadamski.gympooltracker.domain.model

import java.time.LocalDateTime

/**
 * Domain model representing a workout session.
 * This class is independent of the database implementation (Room).
 */
data class Workout(
    val id: Int = 0,
    val type: String,          // e.g., "Gym" or "Pool"
    val durationMinutes: Int,  // Duration of the session
    val caloriesBurned: Int,   // Estimated calories burned
    val date: LocalDateTime?,   // Date and time of the workout
    val notes: String = ""     // User notes
)