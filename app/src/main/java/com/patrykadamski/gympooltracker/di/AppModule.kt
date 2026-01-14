// file: app/src/main/java/com/patrykadamski/gympooltracker/di/AppModule.kt
package com.patrykadamski.gympooltracker.di

import android.app.Application
import androidx.room.Room
import com.patrykadamski.gympooltracker.data.local.GymDatabase
import com.patrykadamski.gympooltracker.data.local.RoutineDao
import com.patrykadamski.gympooltracker.data.local.WorkoutDao
import com.patrykadamski.gympooltracker.data.local.WorkoutTypeDao
import com.patrykadamski.gympooltracker.data.repository.RoutineRepositoryImpl
import com.patrykadamski.gympooltracker.data.repository.WorkoutRepositoryImpl
import com.patrykadamski.gympooltracker.domain.repository.RoutineRepository
import com.patrykadamski.gympooltracker.domain.repository.WorkoutRepository
import com.patrykadamski.gympooltracker.domain.usecase.GetRoutinesUseCase
import com.patrykadamski.gympooltracker.domain.usecase.SaveWorkoutAsRoutineUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- Database ---

    @Provides
    @Singleton
    fun provideGymDatabase(app: Application): GymDatabase {
        return Room.databaseBuilder(
            app,
            GymDatabase::class.java,
            GymDatabase.DATABASE_NAME
        )
            // Fallback strategy for database migrations (clears DB on version change mismatch)
            .fallbackToDestructiveMigration()
            .build()
    }

    // --- DAOs ---

    @Provides
    @Singleton
    fun provideWorkoutDao(database: GymDatabase): WorkoutDao {
        return database.dao
    }

    @Provides
    @Singleton
    fun provideWorkoutTypeDao(database: GymDatabase): WorkoutTypeDao {
        return database.workoutTypeDao
    }

    @Provides
    @Singleton
    fun provideRoutineDao(database: GymDatabase): RoutineDao {
        return database.routineDao
    }

    // --- Repositories ---

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

    // --- Use Cases ---

    @Provides
    @Singleton
    fun provideGetRoutinesUseCase(
        repository: RoutineRepository
    ): GetRoutinesUseCase {
        return GetRoutinesUseCase(repository)
    }

    // FIX: Updated parameter to RoutineRepository (was WorkoutRepository)
    @Provides
    @Singleton
    fun provideSaveWorkoutAsRoutineUseCase(
        repository: RoutineRepository
    ): SaveWorkoutAsRoutineUseCase {
        return SaveWorkoutAsRoutineUseCase(repository)
    }
}