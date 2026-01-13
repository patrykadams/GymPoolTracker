// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/WorkoutTypeEntity.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_types")
data class WorkoutTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,             // e.g. "Gym", "Pool"
    val caloriesPerMinute: Int,   // e.g. 7, 10
    val iconName: String          // e.g. "fitness_center"
)