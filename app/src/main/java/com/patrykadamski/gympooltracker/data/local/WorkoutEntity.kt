// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/WorkoutEntity.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// FIX: Explicitly set table name to "workouts" to match DAO queries
@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val date: Long,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val notes: String,
    // NEW: Added distance field for swimming (in meters)
    val distanceMeters: Int = 0
)