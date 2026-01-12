package com.patrykadamski.gympooltracker.domain.repository

import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.model.WorkoutType
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getWorkouts(): Flow<List<Workout>>

    suspend fun getWorkoutById(id: Int): Workout?

    fun getWorkoutDetails(workoutId: Int): Flow<WorkoutDetails?>

    suspend fun insertWorkout(workout: Workout): Long

    suspend fun deleteWorkout(workout: Workout)

    fun getWorkoutTypes(): Flow<List<WorkoutType>>

    fun getExerciseNames(): Flow<List<String>>

    // --- NEW: Personal Record ---
    fun getPersonalRecord(exerciseName: String): Flow<Double?>

    // --- Exercise and Set Management ---

    suspend fun addExercise(workoutId: Int, name: String): Long
    suspend fun deleteExercise(exerciseId: Long, workoutId: Int)

    suspend fun addSet(
        exerciseId: Long,
        setNumber: Int,
        reps: String = "0",
        weight: Double = 0.0,
        restSeconds: Int = 60
    ): Long

    suspend fun updateSet(set: GymSet)
    suspend fun deleteSet(set: GymSet)
}