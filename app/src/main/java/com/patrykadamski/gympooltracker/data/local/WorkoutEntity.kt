// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/WorkoutEntity.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

// FIX: Changed tableName to "workout_table" to match existing data
@Entity(tableName = "workout_table")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,          // np. "GYM" lub "POOL"
    val durationMinutes: Int,  // Czas trwania
    val caloriesBurned: Int,   // Spalone kalorie
    val date: LocalDateTime,   // Data treningu
    val notes: String = ""     // Notatki
)