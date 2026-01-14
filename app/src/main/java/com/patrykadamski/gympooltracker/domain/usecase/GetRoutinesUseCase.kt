// file: app/src/main/java/com/patrykadamski/gympooltracker/domain/usecase/GetRoutinesUseCase.kt
package com.patrykadamski.gympooltracker.domain.usecase

import com.patrykadamski.gympooltracker.data.local.RoutineWithExercises
import com.patrykadamski.gympooltracker.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRoutinesUseCase @Inject constructor(
    private val repository: RoutineRepository
) {
    operator fun invoke(): Flow<List<RoutineWithExercises>> {
        return repository.getAllRoutines()
    }
}