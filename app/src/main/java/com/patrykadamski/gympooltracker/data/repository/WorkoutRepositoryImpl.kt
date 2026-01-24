package com.patrykadamski.gympooltracker.data.repository

import com.patrykadamski.gympooltracker.data.local.*
import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val workoutTypeDao: WorkoutTypeDao
) : WorkoutRepository {

    override fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutDao.getAllWorkouts().map { entities ->
            entities.map { entity ->
                Workout(
                    id = entity.id.toInt(),
                    type = entity.type,
                    date = Instant.ofEpochMilli(entity.date)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                    duration = entity.duration,
                    calories = entity.calories
                )
            }
        }
    }

    override fun getWorkoutDetails(workoutId: Int): Flow<WorkoutDetails?> {
        return workoutDao.getWorkoutWithExercises(workoutId).map { complexObj ->
            complexObj?.let {
                WorkoutDetails(
                    workout = Workout(
                        id = it.workout.id.toInt(),
                        type = it.workout.type,
                        date = Instant.ofEpochMilli(it.workout.date)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime(),
                        duration = it.workout.duration,
                        calories = it.workout.calories
                    ),
                    exercises = it.exercises
                )
            }
        }
    }

    override fun getExerciseNames(): Flow<List<String>> {
        return workoutDao.getExerciseNames()
    }

    override suspend fun getLastSetForExercise(exerciseName: String): GymSet? {
        val entity = workoutDao.getLastSetForExercise(exerciseName)
        return entity?.let {
            GymSet(
                id = it.id.toInt(),
                exerciseId = it.exerciseId,
                setNumber = it.setNumber,
                reps = it.reps,
                weight = it.weight,
                rpe = it.rpe,
                isCompleted = it.isCompleted,
                restSeconds = it.restSeconds
            )
        }
    }

    // FIX: Implemented getPersonalRecord
    override fun getPersonalRecord(exerciseName: String): Flow<Double?> {
        return workoutDao.getPersonalRecord(exerciseName)
    }

    override suspend fun createWorkout(type: String): Int {
        val entity = WorkoutEntity(
            type = type,
            date = System.currentTimeMillis(),
            duration = 0,
            calories = 0
        )
        return workoutDao.insertWorkout(entity).toInt()
    }

    override suspend fun deleteWorkout(workoutId: Int) {
        workoutDao.deleteWorkout(workoutId.toLong())
    }

    override suspend fun addExercise(workoutId: Int, name: String) {
        val exercise = ExerciseEntity(
            workoutId = workoutId,
            name = name
        )
        workoutDao.insertExercise(exercise)
    }

    override suspend fun addSet(exerciseId: Long) {
        val newSet = SetEntity(
            exerciseId = exerciseId,
            setNumber = 1,
            reps = "",
            weight = 0.0,
            rpe = 0.0,
            isCompleted = false
        )
        workoutDao.insertSet(newSet)
    }

    override suspend fun updateSet(setId: Long, reps: String, weight: Double, isCompleted: Boolean) {
        workoutDao.updateSet(setId.toInt(), reps, weight, isCompleted)
    }

    override suspend fun deleteSet(setId: Long) {
        workoutDao.deleteSet(setId.toInt())
    }

    override suspend fun deleteExercise(exerciseId: Long) {
        workoutDao.deleteExercise(exerciseId.toInt())
    }
}