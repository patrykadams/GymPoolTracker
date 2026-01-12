// file: app/src/main/java/com/patrykadamski/gympooltracker/domain/repository/RoutineRepository.kt
package com.patrykadamski.gympooltracker.domain.repository

import com.patrykadamski.gympooltracker.domain.model.Routine
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {
    fun getRoutines(): Flow<List<Routine>>
}