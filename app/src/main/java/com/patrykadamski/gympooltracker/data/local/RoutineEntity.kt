// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/RoutineEntity.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Table 1: Workout Routine (Templates)
@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,         // e.g. "Upper Body A"
    val description: String   // e.g. "Bench Press / Rows"
)

// Table 2: Exercises inside a Routine
@Entity(
    tableName = "routine_exercises",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE // Delete routine -> delete its exercises
        )
    ]
)
data class RoutineExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: Long,
    val name: String,
    val sets: Int,
    val reps: String,
    val targetRpe: String,
    val orderIndex: Int
)