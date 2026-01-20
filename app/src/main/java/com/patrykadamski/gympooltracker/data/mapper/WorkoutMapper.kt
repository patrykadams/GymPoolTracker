// file: app/src/main/java/com/patrykadamski/gympooltracker/data/mapper/WorkoutMapper.kt
package com.patrykadamski.gympooltracker.data.mapper

import com.patrykadamski.gympooltracker.data.local.SetEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutTypeEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutWithExercises
import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.model.WorkoutType
import java.time.Instant
import java.time.ZoneId

object WorkoutMapper {

    // --- Workout Mappers ---

    fun mapEntityToDomain(entity: WorkoutEntity): Workout {
        return Workout(
            // Map Entity ID (Long) to Domain ID (Int)
            id = entity.id.toInt(),
            type = entity.type,
            // Convert Timestamp (Long) to LocalDateTime
            date = Instant.ofEpochMilli(entity.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime(),
            duration = entity.duration,
            calories = entity.calories
        )
    }

    fun mapDomainToEntity(domain: Workout): WorkoutEntity {
        return WorkoutEntity(
            // Map Domain ID (Int) to Entity ID (Long)
            id = domain.id.toLong(),
            type = domain.type,
            // Convert LocalDateTime back to Timestamp (Long)
            date = domain.date.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            duration = domain.duration,
            calories = domain.calories
        )
    }

    // --- Relation Mappers ---

    fun mapRelationToDetails(relation: WorkoutWithExercises): WorkoutDetails {
        val workout = mapEntityToDomain(relation.workout)

        // Pass the ExerciseWithSets list directly to the domain model
        return WorkoutDetails(
            workout = workout,
            exercises = relation.exercises
        )
    }

    // --- Set Mappers ---

    fun mapSetEntityToDomain(entity: SetEntity): GymSet {
        return GymSet(
            // Note: If GymSet.id is Int, keep .toInt(). If Long, remove it.
            id = entity.id.toInt(),
            // FIX: Removed .toInt() as GymSet expects Long for exerciseId
            exerciseId = entity.exerciseId,
            setNumber = entity.setNumber,
            reps = entity.reps,
            weight = entity.weight,
            rpe = entity.rpe,
             restSeconds = entity.restSeconds,
            isCompleted = entity.isCompleted,

        )
    }

    fun mapSetDomainToEntity(domain: GymSet): SetEntity {
        return SetEntity(
            id = domain.id.toLong(),
            // FIX: Removed .toLong() as domain.exerciseId is already Long
            exerciseId = domain.exerciseId,
            setNumber = domain.setNumber,
            reps = domain.reps,
            weight = domain.weight,
            rpe = domain.rpe,
            // restSeconds = domain.restSeconds, // Uncomment if property exists in Domain
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