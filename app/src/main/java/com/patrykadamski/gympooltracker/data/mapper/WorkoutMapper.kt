package com.patrykadamski.gympooltracker.data.mapper

import com.patrykadamski.gympooltracker.data.local.SetEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutTypeEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutWithExercises
import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.model.WorkoutExercise
import com.patrykadamski.gympooltracker.domain.model.WorkoutType
import java.time.Instant
import java.time.ZoneId

object WorkoutMapper {

    // --- Workout Mappers ---

    fun mapEntityToDomain(entity: WorkoutEntity): Workout {
        return Workout(
            id = entity.id,
            type = entity.type,
            // FIX: Convert Long (timestamp) to LocalDateTime
            date = Instant.ofEpochMilli(entity.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime(),
            durationMinutes = entity.durationMinutes,
            caloriesBurned = entity.caloriesBurned,
            notes = entity.notes
        )
    }

    fun mapDomainToEntity(domain: Workout): WorkoutEntity {
        return WorkoutEntity(
            id = domain.id,
            type = domain.type,
            // FIX: Convert LocalDateTime back to Long (timestamp)
            date = domain.date.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            durationMinutes = domain.durationMinutes,
            caloriesBurned = domain.caloriesBurned,
            notes = domain.notes
        )
    }

    // --- Relation Mappers (Workout + Exercises + Sets) ---

    fun mapRelationToDetails(relation: WorkoutWithExercises): WorkoutDetails {

        val workout = mapEntityToDomain(relation.workout)

        val exercises = relation.exercises.map { exerciseWithSets ->
            WorkoutExercise(
                id = exerciseWithSets.exercise.id,
                name = exerciseWithSets.exercise.name,
                sets = exerciseWithSets.sets.map { mapSetEntityToDomain(it) }
            )
        }

        return WorkoutDetails(
            workout = workout,
            exercises = exercises
        )
    }

    // --- Set Mappers ---

    fun mapSetEntityToDomain(entity: SetEntity): GymSet {
        return GymSet(
            id = entity.id,
            exerciseId = entity.exerciseId,
            setNumber = entity.setNumber,
            reps = entity.reps,
            weight = entity.weight,
            rpe = entity.rpe,
            restSeconds = entity.restSeconds,
            isCompleted = entity.isCompleted
        )
    }

    fun mapSetDomainToEntity(domain: GymSet): SetEntity {
        return SetEntity(
            id = domain.id,
            exerciseId = 0, // ID handled by DB relations logic usually
            setNumber = 0,
            reps = domain.reps,
            weight = domain.weight,
            rpe = 0.0,
            restSeconds = 60,
            isCompleted = domain.isCompleted
        )
    }

    // --- Workout Type Mappers ---

    fun mapTypeEntityToDomain(entity: WorkoutTypeEntity): WorkoutType {
        return WorkoutType(
            id = entity.id,
            name = entity.name,
            iconName = entity.iconName,
            caloriesPerMinute = entity.caloriesPerMinute
        )
    }
}