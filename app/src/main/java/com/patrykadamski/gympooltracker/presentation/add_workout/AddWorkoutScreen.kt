// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/add_workout/AddWorkoutScreen.kt
package com.patrykadamski.gympooltracker.presentation.add_workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykadamski.gympooltracker.domain.model.WorkoutType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutScreen(
    onNavigateBack: () -> Unit,
    onWorkoutCreated: (Int) -> Unit,
    // FIX: Ensure this class is imported from the file above
    viewModel: AddWorkoutViewModel = hiltViewModel()
) {
    val workoutTypes by viewModel.workoutTypes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Start New Workout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Choose Workout Type",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(workoutTypes) { type ->
                WorkoutTypeItem(
                    type = type,
                    onClick = {
                        viewModel.createWorkout(type) { newId ->
                            onWorkoutCreated(newId.toInt())
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WorkoutTypeItem(
    type: WorkoutType,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = type.name,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}