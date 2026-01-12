package com.patrykadamski.gympooltracker.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Rozszerzenie contextu - tworzy instancję DataStore o nazwie "user_prefs"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val WEEKLY_GOAL_KCAL = intPreferencesKey("weekly_goal_kcal")
    }

    // Odczyt celu (domyślnie 2000 kcal)
    val weeklyGoalFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.WEEKLY_GOAL_KCAL] ?: 2000
        }

    // Aktualizacja celu
    suspend fun updateWeeklyGoal(kcal: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.WEEKLY_GOAL_KCAL] = kcal
        }
    }
}