// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/GymDatabase.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.patrykadamski.gympooltracker.domain.model.Workout

// FIX: Added WorkoutEntity, RoutineEntity, RoutineExerciseEntity to the list
@Database(
    entities = [
        WorkoutEntity::class, // Replaces Workout::class as the DB Entity
        WorkoutTypeEntity::class,
        ExerciseEntity::class,
        SetEntity::class,
        RoutineEntity::class,
        RoutineExerciseEntity::class
    ],
    version = 4, // Bumped version to include new tables
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GymDatabase : RoomDatabase() {

    abstract val dao: WorkoutDao
    abstract val workoutTypeDao: WorkoutTypeDao
    abstract val routineDao: RoutineDao // Ensure this is accessible

    companion object {
        const val DATABASE_NAME = "workout_db"
    }
}