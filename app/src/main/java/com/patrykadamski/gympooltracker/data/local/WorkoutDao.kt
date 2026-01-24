package com.patrykadamski.gympooltracker.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    // --- Workouts ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun getWorkoutWithExercises(workoutId: Int): Flow<WorkoutWithExercises?>

    @Query("DELETE FROM workouts WHERE id = :workoutId")
    suspend fun deleteWorkout(workoutId: Long)

    // --- Exercises ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Query("DELETE FROM exercises WHERE id = :exerciseId")
    suspend fun deleteExercise(exerciseId: Int)

    @Query("SELECT DISTINCT name FROM exercises ORDER BY name ASC")
    fun getExerciseNames(): Flow<List<String>>

    // --- Sets ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity)

    @Query("UPDATE sets SET reps = :reps, weight = :weight, isCompleted = :isCompleted WHERE id = :setId")
    suspend fun updateSet(setId: Int, reps: String, weight: Double, isCompleted: Boolean)

    @Query("DELETE FROM sets WHERE id = :setId")
    suspend fun deleteSet(setId: Int)

    @Query("""
        SELECT s.* FROM sets s
        INNER JOIN exercises e ON s.exerciseId = e.id
        INNER JOIN workouts w ON e.workoutId = w.id
        WHERE e.name = :exerciseName
        ORDER BY w.date DESC, s.id DESC
        LIMIT 1
    """)
    suspend fun getLastSetForExercise(exerciseName: String): SetEntity?

    // FIX: Added query to calculate Personal Record (Max Weight)
    @Query("""
        SELECT MAX(s.weight) FROM sets s
        INNER JOIN exercises e ON s.exerciseId = e.id
        WHERE e.name = :exerciseName
    """)
    fun getPersonalRecord(exerciseName: String): Flow<Double?>
}