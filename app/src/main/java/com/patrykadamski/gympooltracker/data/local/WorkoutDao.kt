// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/WorkoutDao.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// 1. New Helper Class: Exercise + List of Sets
data class ExerciseWithSets(
    @Embedded val exercise: ExerciseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseId"
    )
    val sets: List<SetEntity>
)

// 2. Updated Helper Class: Workout + List of (Exercise + Sets)
data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        entity = ExerciseEntity::class, // Explicitly state the entity
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    // FIX: Changed List<ExerciseEntity> to List<ExerciseWithSets>
    // This allows the Mapper to access '.exercise' and '.sets'
    val exercises: List<ExerciseWithSets>
)

@Dao
interface WorkoutDao {

    // --- Workouts ---
    @Query("SELECT * FROM workout_table ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workout_table WHERE id = :id")
    suspend fun getWorkoutById(id: Int): WorkoutEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    // --- Workout Types ---
    @Query("SELECT * FROM workout_types")
    fun getAllWorkoutTypes(): Flow<List<WorkoutTypeEntity>>

    // --- Exercises Relation (Updated return type) ---
    @Transaction
    @Query("SELECT * FROM workout_table WHERE id = :workoutId")
    fun getWorkoutWithExercises(workoutId: Int): Flow<WorkoutWithExercises?>

    // --- Exercises Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Query("DELETE FROM exercises WHERE id = :exerciseId")
    suspend fun deleteExercise(exerciseId: Long)

    @Query("SELECT DISTINCT name FROM exercises ORDER BY name ASC")
    fun getAllExerciseNames(): Flow<List<String>>

    // --- Sets Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity): Long

    @Update
    suspend fun updateSet(set: SetEntity)

    @Delete
    suspend fun deleteSet(set: SetEntity)

    // --- Statistics ---
    @Query("""
        SELECT MAX(s.weight) 
        FROM sets s
        INNER JOIN exercises e ON s.exerciseId = e.id 
        WHERE e.name = :exerciseName
    """)
    fun getPersonalRecord(exerciseName: String): Flow<Double?>

    @Query("""
        SELECT s.* FROM sets s
        INNER JOIN exercises e ON s.exerciseId = e.id
        INNER JOIN workout_table w ON e.workoutId = w.id
        WHERE e.name = :exerciseName
        ORDER BY w.date DESC, s.setNumber DESC
        LIMIT 1
    """)
    suspend fun getLastSetForExercise(exerciseName: String): SetEntity?
}