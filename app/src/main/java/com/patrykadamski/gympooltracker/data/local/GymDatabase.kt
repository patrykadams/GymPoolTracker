// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/GymDatabase.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.patrykadamski.gympooltracker.domain.model.Workout

@Database(
    entities = [
        Workout::class,
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