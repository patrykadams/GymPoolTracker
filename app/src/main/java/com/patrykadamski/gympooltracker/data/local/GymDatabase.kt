// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/GymDatabase.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.patrykadamski.gympooltracker.data.local.WorkoutEntity
import com.patrykadamski.gympooltracker.data.local.WorkoutTypeEntity
import com.patrykadamski.gympooltracker.data.local.ExerciseEntity
import com.patrykadamski.gympooltracker.data.local.SetEntity
import com.patrykadamski.gympooltracker.data.local.RoutineEntity
import com.patrykadamski.gympooltracker.data.local.RoutineExerciseEntity

import com.patrykadamski.gympooltracker.data.local.RoutineDao
import com.patrykadamski.gympooltracker.data.local.WorkoutDao
import com.patrykadamski.gympooltracker.data.local.WorkoutTypeDao

@Database(
    entities = [
        WorkoutEntity::class,
        WorkoutTypeEntity::class,
        ExerciseEntity::class,
        SetEntity::class,
        RoutineEntity::class,
        RoutineExerciseEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GymDatabase : RoomDatabase() {

    abstract val dao: WorkoutDao
    abstract val workoutTypeDao: WorkoutTypeDao

    abstract val routineDao: RoutineDao

    companion object {
        const val DATABASE_NAME = "workout_db"
    }
}