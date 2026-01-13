// file: app/src/main/java/com/patrykadamski/gympooltracker/data/mapper/WorkoutMapper.kt
package com.patrykadamski.gympooltracker.data.mapper

import com.patrykadamski.gympooltracker.data.local.SetEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutTypeEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutWithExercises
import com.patrykadamski.gympooltracker.domain.model.GymExercise
import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.model.WorkoutType

object WorkoutMapper {

    fun mapEntityToDomain(entity: WorkoutEntity): Workout {
        return Workout(
            id = entity.id,
            type = entity.type,
            durationMinutes = entity.durationMinutes,
            caloriesBurned = entity.caloriesBurned,
            date = entity.date,
            notes = entity.notes
        )
    }

    fun mapDomainToEntity(domain: Workout): WorkoutEntity {
        return WorkoutEntity(
            id = domain.id,
            type = domain.type,
            durationMinutes = domain.durationMinutes,
            caloriesBurned = domain.caloriesBurned,
            date = domain.date,
            notes = domain.notes
        )
    }

    fun mapTypeEntityToDomain(entity: WorkoutTypeEntity): WorkoutType {
        return WorkoutType(
            id = entity.id,
            name = entity.name,
            caloriesPerMinute = entity.caloriesPerMinute,
            iconName = entity.iconName
        )
    }

    fun mapSetEntityToDomain(entity: SetEntity): GymSet {
        return GymSet(
            id = entity.id,
            exerciseId = entity.exerciseId,
            setNumber = entity.setNumber,
            reps = entity.reps,
            weight = entity.weight,
            rpe = 0.0, // Default value as Entity might not have RPE yet
            restSeconds = entity.restSeconds,
            isCompleted = entity.isCompleted
        )
    }

    fun mapSetDomainToEntity(domain: GymSet): SetEntity {
        return SetEntity(
            id = domain.id,
            exerciseId = domain.exerciseId,
            setNumber = domain.setNumber,
            reps = domain.reps,
            weight = domain.weight,
            restSeconds = domain.restSeconds,
            isCompleted = domain.isCompleted
        )
    }

    fun mapRelationToDetails(relation: WorkoutWithExercises): WorkoutDetails {
        return WorkoutDetails(
            workout = mapEntityToDomain(relation.workout),
            exercises = relation.exercises.map { exerciseWithSets ->
                GymExercise(
                    id = exerciseWithSets.exercise.id,
                    workoutId = exerciseWithSets.exercise.workoutId,
                    name = exerciseWithSets.exercise.name,
                    sets = exerciseWithSets.sets.map { mapSetEntityToDomain(it) },
                    personalRecord = null // Will be populated by ViewModel
                )
            }
        )
    }
}