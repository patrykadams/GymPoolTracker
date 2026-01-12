// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/TemplateEntities.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Table 1: Workout Routine (e.g., "Day A - Legs")
@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,         // e.g., "DAY A – LEGS"
    val description: String   // e.g., "Squat / Hips"
)

// Table 2: Exercise in a Routine (e.g., "Back Squat", 4 sets)
@Entity(
    tableName = "routine_exercises",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE // Deleting a routine deletes its exercises
        )
    ]
)
data class RoutineExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: Long,      // Foreign Key
    val name: String,         // e.g., "Back Squat"
    val sets: Int,            // e.g., 4
    val reps: String,         // e.g., "5" or "12-15"
    val targetRpe: String,    // e.g., "6-7"
    val orderIndex: Int       // Order on the list
)