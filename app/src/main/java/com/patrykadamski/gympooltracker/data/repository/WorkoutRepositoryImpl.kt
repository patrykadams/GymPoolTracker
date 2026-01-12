package com.patrykadamski.gympooltracker.data.repository

import com.patrykadamski.gympooltracker.data.local.ExerciseEntity
import com.patrykadamski.gympooltracker.data.local.SetEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutDao
import com.patrykadamski.gympooltracker.data.local.WorkoutTypeDao
import com.patrykadamski.gympooltracker.domain.model.GymExercise
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

    override fun getWorkouts(): Flow<List<Workout>> = dao.getWorkouts()

    override suspend fun getWorkoutById(id: Int): Workout? = dao.getWorkoutById(id)

    override suspend fun insertWorkout(workout: Workout): Long {
        return dao.insertWorkout(workout)
    }

    override suspend fun deleteWorkout(workout: Workout) {
        dao.deleteWorkout(workout)
    }

    override fun getWorkoutTypes(): Flow<List<WorkoutType>> {
        return workoutTypeDao.getAllTypes().map { entities ->
            entities.map { WorkoutType(it.id, it.name, it.caloriesPerMinute, it.iconName) }
        }
    }

    override fun getExerciseNames(): Flow<List<String>> {
        return dao.getExerciseNames()
    }

    // --- NEW: Personal Record Implementation ---
    override fun getPersonalRecord(exerciseName: String): Flow<Double?> {
        return dao.getPersonalRecord(exerciseName)
    }

    override fun getWorkoutDetails(workoutId: Int): Flow<WorkoutDetails?> {
        return dao.getWorkoutDetails(workoutId).map { relation ->
            relation?.let {
                WorkoutDetails(
                    workout = it.workout,
                    exercises = it.exercises.map { exWithSets ->
                        GymExercise(
                            id = exWithSets.exercise.id,
                            workoutId = exWithSets.exercise.workoutId,
                            name = exWithSets.exercise.name,
                            sets = exWithSets.sets.map { s ->
                                GymSet(
                                    id = s.id,
                                    exerciseId = s.exerciseId,
                                    setNumber = s.setNumber,
                                    reps = s.reps,
                                    weight = s.weight,
                                    rpe = s.rpe,
                                    restSeconds = s.restSeconds,
                                    isCompleted = s.isCompleted
                                )
                            }.sortedBy { set -> set.setNumber }
                        )
                    }.sortedBy { ex -> ex.id }
                )
            }
        }
    }

    override suspend fun addExercise(workoutId: Int, name: String): Long {
        val entity = ExerciseEntity(workoutId = workoutId, name = name, orderIndex = 0)
        return dao.insertExercise(entity)
    }

    override suspend fun deleteExercise(exerciseId: Long, workoutId: Int) {
        val entity = ExerciseEntity(id = exerciseId, workoutId = workoutId, name = "", orderIndex = 0)
        dao.deleteExercise(entity)
    }

    override suspend fun addSet(
        exerciseId: Long,
        setNumber: Int,
        reps: String,
        weight: Double,
        restSeconds: Int
    ): Long {
        val entity = SetEntity(
            exerciseId = exerciseId,
            setNumber = setNumber,
            reps = reps,
            weight = weight,
            rpe = 8.0,
            restSeconds = restSeconds,
            isCompleted = false
        )
        return dao.insertSet(entity)
    }

    override suspend fun updateSet(set: GymSet) {
        val entity = SetEntity(
            id = set.id,
            exerciseId = set.exerciseId,
            setNumber = set.setNumber,
            reps = set.reps,
            weight = set.weight,
            rpe = set.rpe,
            restSeconds = set.restSeconds,
            isCompleted = set.isCompleted
        )
        dao.updateSet(entity)
    }

    override suspend fun deleteSet(set: GymSet) {
        val entity = SetEntity(
            id = set.id,
            exerciseId = set.exerciseId,
            setNumber = set.setNumber,
            reps = set.reps,
            weight = set.weight,
            rpe = set.rpe,
            restSeconds = set.restSeconds,
            isCompleted = set.isCompleted
        )
        dao.deleteSet(entity)
    }
}