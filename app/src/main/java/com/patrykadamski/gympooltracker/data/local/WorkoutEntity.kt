package com.patrykadamski.gympooltracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,          // np. "GYM" lub "POOL"
    val durationMinutes: Int,  // Czas trwania
    val caloriesBurned: Int,   // Spalone kalorie (opcjonalne, ale warto mieć)
    val date: LocalDateTime,   // Data treningu
    val notes: String = ""     // Notatki
)