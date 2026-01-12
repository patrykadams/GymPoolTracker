package com.patrykadamski.gympooltracker.data.local

import androidx.room.Embedded
import androidx.room.Relation
import com.patrykadamski.gympooltracker.domain.model.Workout

/**
 * Helper class for Room to map the one-to-many relationship: Exercise -> Sets.
 */
data class ExerciseWithSets(
    @Embedded val exercise: ExerciseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseId"
    )
    val sets: List<SetEntity>
)

/**
 * Helper class for Room to map the relationship: Workout -> Exercises (which contain Sets).
 */
data class WorkoutWithExercises(
    @Embedded val workout: Workout,
    @Relation(
        entity = ExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val exercises: List<ExerciseWithSets>
)