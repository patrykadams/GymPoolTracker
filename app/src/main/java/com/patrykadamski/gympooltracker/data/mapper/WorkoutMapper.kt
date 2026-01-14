// file: app/src/main/java/com/patrykadamski/gympooltracker/data/mapper/WorkoutMapper.kt
package com.patrykadamski.gympooltracker.data.mapper

// FIX: Removed '.entity' from the import path.
// Assuming WorkoutEntity is located in 'com.patrykadamski.gympooltracker.data.local'
import com.patrykadamski.gympooltracker.data.local.WorkoutEntity
import com.patrykadamski.gympooltracker.domain.model.Workout

fun WorkoutEntity.toDomain(): Workout {
    return Workout(
        id = id,
        type = type,
        date = date,
        durationMinutes = durationMinutes,
        caloriesBurned = caloriesBurned,
        notes = notes
    )
}

fun Workout.toEntity(): WorkoutEntity {
    return WorkoutEntity(
        id = id,
        type = type,
        date = date,
        durationMinutes = durationMinutes,
        caloriesBurned = caloriesBurned,
        notes = notes
    )
}