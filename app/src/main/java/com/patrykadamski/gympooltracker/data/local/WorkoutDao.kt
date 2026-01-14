// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/WorkoutDao.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // --- Workout Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Int): WorkoutEntity?

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun getWorkoutWithExercises(workoutId: Int): Flow<WorkoutWithExercises?>

    // --- Exercise Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Query("DELETE FROM exercises WHERE id = :exerciseId")
    suspend fun deleteExercise(exerciseId: Long)

    @Query("SELECT DISTINCT name FROM exercises ORDER BY name ASC")
    fun getAllExerciseNames(): Flow<List<String>>

    // --- Set Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity): Long

    @Update
    suspend fun updateSet(set: SetEntity)

    @Delete
    suspend fun deleteSet(set: SetEntity)

    // --- Stats / Records ---

    @Query("SELECT MAX(weight) FROM sets INNER JOIN exercises ON sets.exerciseId = exercises.id WHERE exercises.name = :exerciseName")
    fun getPersonalRecord(exerciseName: String): Flow<Double?>

    // FIX: Changed "SELECT *" to "SELECT sets.*" to avoid fetching Exercise columns into SetEntity
    @Query("SELECT sets.* FROM sets INNER JOIN exercises ON sets.exerciseId = exercises.id WHERE exercises.name = :exerciseName ORDER BY sets.id DESC LIMIT 1")
    suspend fun getLastSetForExercise(exerciseName: String): SetEntity?
}