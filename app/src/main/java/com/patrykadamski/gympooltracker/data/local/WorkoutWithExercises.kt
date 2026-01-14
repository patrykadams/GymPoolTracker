// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/WorkoutWithExercises.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId",
        entity = ExerciseEntity::class
    )
    val exercises: List<ExerciseWithSets>
)