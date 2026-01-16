// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/GymDatabase.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

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
abstract class GymDatabase : RoomDatabase() {

    abstract val workoutDao: WorkoutDao
    abstract val routineDao: RoutineDao
    abstract val workoutTypeDao: WorkoutTypeDao


    companion object {
        const val DATABASE_NAME = "gym_pool_tracker_db"
    }
}