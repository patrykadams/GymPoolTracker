package com.patrykadamski.gympooltracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutTypeDao {
    @Query("SELECT * FROM workout_types")
    fun getAllTypes(): Flow<List<WorkoutTypeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(types: List<WorkoutTypeEntity>)
}