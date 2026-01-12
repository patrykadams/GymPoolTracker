package com.patrykadamski.gympooltracker.data.mapper

import com.patrykadamski.gympooltracker.data.local.WorkoutEntity
import com.patrykadamski.gympooltracker.domain.model.Workout

fun WorkoutEntity.toDomain(): Workout {
    return Workout(
        id = id,
        type = type,
        durationMinutes = durationMinutes,
        caloriesBurned = caloriesBurned,
        date = date,
        notes = notes
    )
}

fun Workout.toEntity(): WorkoutEntity {
    return WorkoutEntity(
        id = id,
        type = type,
        durationMinutes = durationMinutes,
        caloriesBurned = caloriesBurned,
        date = date,
        notes = notes
    )
}