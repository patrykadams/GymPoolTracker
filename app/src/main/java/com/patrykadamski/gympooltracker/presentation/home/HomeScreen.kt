// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/home/HomeScreen.kt
package com.patrykadamski.gympooltracker.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykadamski.gympooltracker.data.local.RoutineWithExercises
import com.patrykadamski.gympooltracker.domain.model.Workout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToWorkoutDetails: (Int) -> Unit,
    onNavigateToCreateWorkout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // FIX: Collect flows separately instead of using 'uiState'
    val recentWorkouts by viewModel.recentWorkouts.collectAsState()
    val routines by viewModel.routines.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateWorkout) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Start Workout")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Welcome Back!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Section: Your Routines
            item {
                Text(
                    text = "Your Routines",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (routines.isEmpty()) {
                item {
                    Text(
                        text = "No routines created yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(routines) { routineItem ->
                    HomeRoutineItem(
                        item = routineItem,
                        onClick = { /* TODO: Navigate to start routine */ }
                    )
                }
            }

            // Section: Recent History
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recent Workouts",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (recentWorkouts.isEmpty()) {
                item {
                    Text(
                        text = "No workout history found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(recentWorkouts) { workout ->
                    HomeWorkoutItem(
                        workout = workout,
                        onClick = { onNavigateToWorkoutDetails(workout.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeRoutineItem(
    item: RoutineWithExercises,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Note: Accessing .routine.name because item is RoutineWithExercises
            Text(
                text = item.routine.name,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "${item.exercises.size} exercises",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HomeWorkoutItem(
    workout: Workout,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = workout.type.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(workout.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${workout.durationMinutes} min",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}