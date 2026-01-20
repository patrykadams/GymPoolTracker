package com.patrykadamski.gympooltracker.domain.repository

import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getAllWorkouts(): Flow<List<Workout>>
    fun getWorkoutDetails(workoutId: Int): Flow<WorkoutDetails?>

    suspend fun createWorkout(type: String): Int

    // New methods for logging
    suspend fun addExercise(workoutId: Int, name: String)
    suspend fun addSet(exerciseId1: Long, exerciseId: Int)
    suspend fun updateSet(setId: Int, reps: String, weight: Double, isCompleted: Boolean)
    suspend fun deleteSet(setId: Int)
    suspend fun deleteExercise(exerciseId: Int)
}