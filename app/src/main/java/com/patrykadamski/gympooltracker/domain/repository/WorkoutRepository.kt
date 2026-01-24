package com.patrykadamski.gympooltracker.domain.repository

import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    // Queries
    fun getAllWorkouts(): Flow<List<Workout>>
    fun getWorkoutDetails(workoutId: Int): Flow<WorkoutDetails?>

    // FIX: Added missing method signature
    fun getExerciseNames(): Flow<List<String>>

    // Commands
    suspend fun createWorkout(type: String): Int
    suspend fun deleteWorkout(workoutId: Int)

    // Logging Commands
    suspend fun addExercise(workoutId: Int, name: String)
    suspend fun addSet(exerciseId: Long)
    suspend fun updateSet(setId: Long, reps: String, weight: Double, isCompleted: Boolean)
    suspend fun deleteSet(setId: Long)
    suspend fun deleteExercise(exerciseId: Long)
}