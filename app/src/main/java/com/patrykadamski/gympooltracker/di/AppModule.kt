// file: app/src/main/java/com/patrykadamski/gympooltracker/di/AppModule.kt
package com.patrykadamski.gympooltracker.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.patrykadamski.gympooltracker.data.local.GymDatabase
import com.patrykadamski.gympooltracker.data.local.RoutineDao
import com.patrykadamski.gympooltracker.data.local.WorkoutDao
import com.patrykadamski.gympooltracker.data.local.WorkoutTypeDao
import com.patrykadamski.gympooltracker.data.repository.RoutineRepositoryImpl
import com.patrykadamski.gympooltracker.data.repository.WorkoutRepositoryImpl
import com.patrykadamski.gympooltracker.domain.repository.RoutineRepository
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import com.patrykadamski.gympooltracker.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGymDatabase(app: Application): GymDatabase {
        return Room.databaseBuilder(
            app,
            GymDatabase::class.java,
            GymDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    // Seed Workout Types
                    val cursor = db.query("SELECT count(*) FROM workout_types")
                    if (cursor.moveToFirst()) {
                        val count = cursor.getInt(0)
                        if (count == 0) {
                            db.execSQL("INSERT INTO workout_types (name, caloriesPerMinute, iconName) VALUES ('Siłownia', 7, 'GYM')")
                            db.execSQL("INSERT INTO workout_types (name, caloriesPerMinute, iconName) VALUES ('Basen', 10, 'POOL')")
                            db.execSQL("INSERT INTO workout_types (name, caloriesPerMinute, iconName) VALUES ('Bieganie', 12, 'RUN')")
                            db.execSQL("INSERT INTO workout_types (name, caloriesPerMinute, iconName) VALUES ('Rower', 8, 'BIKE')")
                        }
                    }
                    cursor.close()
                }

                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Seed Routines on fresh install/create
                    CoroutineScope(Dispatchers.IO).launch {
                        // Insert Routine A
                        db.execSQL("INSERT INTO routines (name, description) VALUES ('Full Body A', 'Przysiad / Wyciskanie / Wiosłowanie')")
                        db.execSQL("INSERT INTO routine_exercises (routineId, name, sets, reps, targetRpe, orderIndex) VALUES (1, 'Back Squat', 3, '5', '8', 1)")
                        db.execSQL("INSERT INTO routine_exercises (routineId, name, sets, reps, targetRpe, orderIndex) VALUES (1, 'Bench Press', 3, '5', '9', 2)")
                        db.execSQL("INSERT INTO routine_exercises (routineId, name, sets, reps, targetRpe, orderIndex) VALUES (1, 'Barbell Row', 3, '8', '8', 3)")

                        // Insert Routine B
                        db.execSQL("INSERT INTO routines (name, description) VALUES ('Full Body B', 'Martwy ciąg / OHP / Podciąganie')")
                        db.execSQL("INSERT INTO routine_exercises (routineId, name, sets, reps, targetRpe, orderIndex) VALUES (2, 'Deadlift', 1, '5', '9', 1)")
                        db.execSQL("INSERT INTO routine_exercises (routineId, name, sets, reps, targetRpe, orderIndex) VALUES (2, 'Overhead Press (OHP)', 3, '8', '8', 2)")
                        db.execSQL("INSERT INTO routine_exercises (routineId, name, sets, reps, targetRpe, orderIndex) VALUES (2, 'Pull-Up', 3, 'AMRAP', '10', 3)")
                    }
                }
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkoutDao(db: GymDatabase): WorkoutDao {
        return db.dao
    }

    @Provides
    @Singleton
    fun provideWorkoutTypeDao(db: GymDatabase): WorkoutTypeDao {
        return db.workoutTypeDao
    }

    @Provides
    @Singleton
    fun provideRoutineDao(db: GymDatabase): RoutineDao {
        return db.routineDao
    }

    @Provides
    @Singleton
    fun provideWorkoutRepository(
        dao: WorkoutDao,
        workoutTypeDao: WorkoutTypeDao
    ): WorkoutRepository {
        return WorkoutRepositoryImpl(dao, workoutTypeDao)
    }

    @Provides
    @Singleton
    fun provideRoutineRepository(
        dao: RoutineDao
    ): RoutineRepository {
        return RoutineRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideGetWorkoutsUseCase(repository: WorkoutRepository): GetWorkoutsUseCase {
        return GetWorkoutsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideInsertWorkoutUseCase(repository: WorkoutRepository): InsertWorkoutUseCase {
        return InsertWorkoutUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetWorkoutByIdUseCase(repository: WorkoutRepository): GetWorkoutByIdUseCase {
        return GetWorkoutByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetWorkoutTypesUseCase(repository: WorkoutRepository): GetWorkoutTypesUseCase {
        return GetWorkoutTypesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetWorkoutDetailsUseCase(repository: WorkoutRepository): GetWorkoutDetailsUseCase {
        return GetWorkoutDetailsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddExerciseUseCase(repository: WorkoutRepository): AddExerciseUseCase {
        return AddExerciseUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddSetUseCase(repository: WorkoutRepository): AddSetUseCase {
        return AddSetUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateSetUseCase(repository: WorkoutRepository): UpdateSetUseCase {
        return UpdateSetUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideToggleSetCompletionUseCase(repository: WorkoutRepository): ToggleSetCompletionUseCase {
        return ToggleSetCompletionUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteSetUseCase(repository: WorkoutRepository): DeleteSetUseCase {
        return DeleteSetUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteExerciseUseCase(repository: WorkoutRepository): DeleteExerciseUseCase {
        return DeleteExerciseUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetExerciseNamesUseCase(repository: WorkoutRepository): GetExerciseNamesUseCase {
        return GetExerciseNamesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetPersonalRecordUseCase(repository: WorkoutRepository): GetPersonalRecordUseCase {
        return GetPersonalRecordUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetLastSetForExerciseUseCase(repository: WorkoutRepository): GetLastSetForExerciseUseCase {
        return GetLastSetForExerciseUseCase(repository)
    }

    // --- Routines UseCases ---
    @Provides
    @Singleton
    fun provideGetRoutinesUseCase(repository: RoutineRepository): GetRoutinesUseCase {
        return GetRoutinesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCreateWorkoutFromRoutineUseCase(repository: WorkoutRepository): CreateWorkoutFromRoutineUseCase {
        return CreateWorkoutFromRoutineUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveWorkoutAsRoutineUseCase(
        workoutRepository: WorkoutRepository,
        routineRepository: RoutineRepository
    ): SaveWorkoutAsRoutineUseCase {
        return SaveWorkoutAsRoutineUseCase(workoutRepository, routineRepository)
    }
}