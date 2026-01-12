package com.patrykadamski.gympooltracker.data.local

import androidx.room.*
import com.patrykadamski.gympooltracker.domain.model.Workout
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_table ORDER BY date DESC")
    fun getWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM workout_table WHERE id = :id")
    suspend fun getWorkoutById(id: Int): Workout?

    @Transaction
    @Query("SELECT * FROM workout_table WHERE id = :workoutId")
    fun getWorkoutDetails(workoutId: Int): Flow<WorkoutWithExercises?>

    // --- Autocomplete Helper ---
    @Query("SELECT DISTINCT name FROM exercises ORDER BY name ASC")
    fun getExerciseNames(): Flow<List<String>>

    // --- NEW: Personal Record Helper ---
    /**
     * Finds the maximum weight ever lifted for a specific exercise name.
     */
    @Query("""
        SELECT MAX(s.weight) FROM sets s 
        JOIN exercises e ON s.exerciseId = e.id 
        WHERE e.name = :exerciseName
    """)
    fun getPersonalRecord(exerciseName: String): Flow<Double?>

    // --- Workout Management ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    // --- Exercise and Set Management ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity): Long

    @Delete
    suspend fun deleteSet(set: SetEntity)

    @Update
    suspend fun updateSet(set: SetEntity)
}