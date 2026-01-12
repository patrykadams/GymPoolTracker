package com.patrykadamski.gympooltracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_types")
data class WorkoutTypeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,             // np. "Siłownia", "Bieganie"
    val caloriesPerMinute: Int,   // np. 7, 10
    val iconName: String          // Klucz do mapowania ikon, np. "GYM", "POOL", "RUN"
)