// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/GymDatabase.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// FIX: Ensure all entities (including WorkoutEntity) are listed here
@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class,
        SetEntity::class,
        WorkoutTypeEntity::class,
        RoutineEntity::class,
        RoutineExerciseEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val workoutDao: WorkoutDao
    abstract val routineDao: RoutineDao
    abstract val workoutTypeDao: WorkoutTypeDao
}