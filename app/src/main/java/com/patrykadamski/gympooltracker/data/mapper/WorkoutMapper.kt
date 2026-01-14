// file: app/src/main/java/com/patrykadamski/gympooltracker/data/mapper/WorkoutMapper.kt
package com.patrykadamski.gympooltracker.data.mapper

import com.patrykadamski.gympooltracker.data.local.SetEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutTypeEntity
import com.patrykadamski.gympooltracker.data.local.RoutineWithExercises
import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.model.WorkoutExercise
import com.patrykadamski.gympooltracker.domain.model.WorkoutType

object WorkoutMapper {

    // --- Workout Mappers ---

    fun mapEntityToDomain(entity: WorkoutEntity): Workout {
        return Workout(
            id = entity.id,
            type = entity.type,
            date = entity.date,
            durationMinutes = entity.durationMinutes,
            caloriesBurned = entity.caloriesBurned,
            notes = entity.notes
        )
    }

    fun mapDomainToEntity(domain: Workout): WorkoutEntity {
        return WorkoutEntity(
            id = domain.id,
            type = domain.type,
            date = domain.date,
            durationMinutes = domain.durationMinutes,
            caloriesBurned = domain.caloriesBurned,
            notes = domain.notes
        )
    }

    // --- Relation Mappers (Workout + Exercises + Sets) ---

    fun mapRelationToDetails(relation: RoutineWithExercises): WorkoutDetails {
        // RoutineWithExercises is assumed to be the POJO holding WorkoutEntity + List<ExerciseWithSets>
        // Note: You might need to adjust 'RoutineWithExercises' name if your relation class is named differently (e.g. WorkoutWithExercises)

        val workout = mapEntityToDomain(relation.routine) // Assuming 'routine' is the embedded WorkoutEntity field

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
            reps = entity.reps,
            weight = entity.weight,
            isCompleted = entity.isCompleted
            // restSeconds is usually internal logic, usually not needed in basic GymSet unless added to model
        )
    }

    fun mapSetDomainToEntity(domain: GymSet): SetEntity {
        // Note: This mapping is tricky because GymSet might not have all Entity fields (like parent ID).
        // Usually, updates happen via specific fields. This is a placeholder for updateSet.
        return SetEntity(
            id = domain.id,
            exerciseId = 0, // Caution: ID must be preserved or handled by DAO
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