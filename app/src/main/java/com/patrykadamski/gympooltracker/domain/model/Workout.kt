package com.patrykadamski.gympooltracker.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "workout_table") // <--- TO JEST KLUCZOWA POPRAWKA
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,          // np. "Siłownia", "Basen"
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val date: LocalDateTime,
    val notes: String = ""
)