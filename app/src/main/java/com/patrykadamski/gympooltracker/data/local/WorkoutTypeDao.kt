// file: app/src/main/java/com/patrykadamski/gympooltracker/data/local/WorkoutTypeDao.kt
package com.patrykadamski.gympooltracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutTypeDao {

    // FIX: Added @Query annotation here (this was likely missing)
    @Query("SELECT * FROM workout_types")
    fun getAllWorkoutTypes(): Flow<List<WorkoutTypeEntity>>

    // Helper to pre-populate data if needed
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(types: List<WorkoutTypeEntity>)
}