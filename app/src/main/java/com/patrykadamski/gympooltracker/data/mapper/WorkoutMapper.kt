// file: app/src/main/java/com/patrykadamski/gympooltracker/data/mapper/WorkoutMapper.kt
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
            // FIX: Konwersja Long -> Int
            id = entity.id.toInt(),
            type = entity.type,
            date = Instant.ofEpochMilli(entity.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime(),
            // FIX: Używamy nazw pól zgodnych z WorkoutEntity z poprzednich kroków
            duration = entity.duration,
            calories = entity.calories
        )
    }

    fun mapDomainToEntity(domain: Workout): WorkoutEntity {
        return WorkoutEntity(
            // FIX: Konwersja Int -> Long
            id = domain.id.toLong(),
            type = domain.type,
            date = domain.date.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            duration = domain.duration,
            calories = domain.calories
        )
    }

    // --- Relation Mappers ---

    // UWAGA: Ta funkcja jest potrzebna tylko jeśli używasz WorkoutExercise w UI.
    // Jeśli używasz ExerciseWithSets w UI (jak ustaliliśmy wcześniej), ta funkcja może nie być używana,
    // ale naprawiam ją dla spójności.
    fun mapRelationToDetails(relation: WorkoutWithExercises): WorkoutDetails {

        val workout = mapEntityToDomain(relation.workout)

        // Jeśli WorkoutDetails w domenie oczekuje ExerciseWithSets (jak zmieniliśmy ostatnio),
        // to tutaj powinniśmy po prostu przypisać:
        // exercises = relation.exercises

        // Jeśli jednak cofnąłeś zmianę i używasz WorkoutExercise, to zostawiam mapowanie:
        /*
        val exercises = relation.exercises.map { exerciseWithSets ->
            WorkoutExercise(
                id = exerciseWithSets.exercise.id.toInt(), // FIX: Long -> Int
                name = exerciseWithSets.exercise.name,
                sets = exerciseWithSets.sets.map { mapSetEntityToDomain(it) }
            )
        }
        */

        // Zgodnie z ostatnią naprawą "Layer Mismatch", zwracamy to co baza daje bezpośrednio:
        return WorkoutDetails(
            workout = workout,
            exercises = relation.exercises
        )
    }

    // --- Set Mappers ---

    fun mapSetEntityToDomain(entity: SetEntity): GymSet {
        return GymSet(
            // FIX: Konwersja Long -> Int
            id = entity.id.toInt(),
            exerciseId = entity.exerciseId.toInt(),
            setNumber = entity.setNumber,
            reps = entity.reps,
            weight = entity.weight,
            rpe = entity.rpe,
            // restSeconds = entity.restSeconds, // Jeśli nie masz tego w Entity, zakomentuj
            isCompleted = entity.isCompleted
        )
    }

    fun mapSetDomainToEntity(domain: GymSet): SetEntity {
        return SetEntity(
            id = domain.id.toLong(), // FIX: Int -> Long
            exerciseId = domain.exerciseId.toLong(), // FIX: Int -> Long
            setNumber = domain.setNumber, // Zakładam, że w DB setNumber to Int
            reps = domain.reps,
            weight = domain.weight,
            rpe = domain.rpe,
            // restSeconds = domain.restSeconds,
            isCompleted = domain.isCompleted
        )
    }
}