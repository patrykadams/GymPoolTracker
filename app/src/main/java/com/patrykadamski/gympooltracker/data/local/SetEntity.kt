// file: app/src/main/java/com/patrykadamski/gympooltracker/data/repository/WorkoutRepositoryImpl.kt
package com.patrykadamski.gympooltracker.data.repository

import com.patrykadamski.gympooltracker.data.local.ExerciseEntity
import com.patrykadamski.gympooltracker.data.local.SetEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutDao
import com.patrykadamski.gympooltracker.data.local.WorkoutTypeDao
import com.patrykadamski.gympooltracker.data.mapper.WorkoutMapper
import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.model.Workout
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.model.WorkoutType
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkoutRepositoryImpl(
    private val dao: WorkoutDao,
    private val workoutTypeDao: WorkoutTypeDao
) : WorkoutRepository {

    override fun getWorkouts(): Flow<List<Workout>> {
        return dao.getAllWorkouts().map { entities ->
            entities.map { WorkoutMapper.mapEntityToDomain(it) }
        }
    }

    override suspend fun getWorkoutById(id: Int): Workout? {
        val entity = dao.getWorkoutById(id) ?: return null
        return WorkoutMapper.mapEntityToDomain(entity)
    }

    override fun getWorkoutDetails(workoutId: Int): Flow<WorkoutDetails?> {
        return dao.getWorkoutWithExercises(workoutId).map { relation ->
            relation?.let { WorkoutMapper.mapRelationToDetails(it) }
        }
    }

    override suspend fun insertWorkout(workout: Workout): Long {
        val entity = WorkoutMapper.mapDomainToEntity(workout)
        return dao.insertWorkout(entity)
    }

    override suspend fun deleteWorkout(workout: Workout) {
        val entity = WorkoutMapper.mapDomainToEntity(workout)
        dao.deleteWorkout(entity)
    }

    override fun getWorkoutTypes(): Flow<List<WorkoutType>> {
        return workoutTypeDao.getAllWorkoutTypes().map { entities ->
            entities.map { WorkoutMapper.mapTypeEntityToDomain(it) }
        }
    }

    override fun getExerciseNames(): Flow<List<String>> {
        return dao.getAllExerciseNames()
    }

    override fun getPersonalRecord(exerciseName: String): Flow<Double?> {
        return dao.getPersonalRecord(exerciseName)
    }

    override suspend fun getLastSetForExercise(exerciseName: String): GymSet? {
        val entity = dao.getLastSetForExercise(exerciseName) ?: return null
        return WorkoutMapper.mapSetEntityToDomain(entity)
    }

    override suspend fun addExercise(workoutId: Int, name: String): Long {
        val entity = ExerciseEntity(workoutId = workoutId, name = name)
        return dao.insertExercise(entity)
    }

    override suspend fun deleteExercise(exerciseId: Long, workoutId: Int) {
        dao.deleteExercise(exerciseId)
    }

    override suspend fun addSet(
        exerciseId: Long,
        setNumber: Int,
        reps: String,
        weight: Double,
        restSeconds: Int
    ): Long {
        // FIX: Added 'rpe' parameter to SetEntity constructor
        val setEntity = SetEntity(
            exerciseId = exerciseId,
            setNumber = setNumber,
            reps = reps,
            weight = weight,
            rpe = 0.0, // Default value for new sets
            restSeconds = restSeconds,
            isCompleted = false
        )
        return dao.insertSet(setEntity)
    }

    override suspend fun updateSet(set: GymSet) {
        val entity = WorkoutMapper.mapSetDomainToEntity(set)
        dao.updateSet(entity)
    }

    override suspend fun deleteSet(set: GymSet) {
        val entity = WorkoutMapper.mapSetDomainToEntity(set)
        dao.deleteSet(entity)
    }
}