// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/workout_details/WorkoutDetailsScreen.kt
package com.patrykadamski.gympooltracker.presentation.workout_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.model.WorkoutExercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    onNavigateUp: () -> Unit,
    // FIX: Updated to use the renamed ViewModel class 'WorkoutDetailsVM'
    viewModel: WorkoutDetailsVM = hiltViewModel()
) {
    val workoutDetails by viewModel.workoutDetails.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // For saving routine dialog
    var showMenu by remember { mutableStateOf(false) }

    if (uiState.isSaveRoutineDialogVisible) {
        SaveRoutineDialog(
            onDismiss = { viewModel.hideSaveRoutineDialog() },
            onConfirm = { name -> viewModel.saveAsRoutine(name) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = workoutDetails?.workout?.type ?: "Workout Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Save as Routine") },
                            onClick = {
                                showMenu = false
                                viewModel.showSaveRoutineDialog()
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Show dialog to add new exercise */ }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Exercise")
            }
        }
    ) { paddingValues ->
        val details = workoutDetails
        if (details == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(details.exercises) { exercise ->
                    ExerciseItem(
                        exercise = exercise,
                        onAddSet = { viewModel.addSet(exercise.id, exercise.sets.size) },
                        onUpdateSet = { set -> viewModel.updateSet(set) },
                        onDeleteSet = { set -> viewModel.deleteSet(set) },
                        onDeleteExercise = { viewModel.deleteExercise(exercise.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseItem(
    exercise: WorkoutExercise,
    onAddSet: () -> Unit,
    onUpdateSet: (com.patrykadamski.gympooltracker.domain.model.GymSet) -> Unit,
    onDeleteSet: (com.patrykadamski.gympooltracker.domain.model.GymSet) -> Unit,
    onDeleteExercise: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDeleteExercise) {
                    Icon(
                        imageVector = Icons.Default.MoreVert, // Using MoreVert as placeholder for delete menu or direct delete icon
                        contentDescription = "Delete Exercise"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Header row
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Set", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                Text(text = "Reps", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                Text(text = "Weight", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                Text(text = "", modifier = Modifier.width(48.dp)) // Placeholder for delete button
            }

            exercise.sets.forEachIndexed { index, set ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${index + 1}", modifier = Modifier.weight(1f))
                    Text(text = set.reps, modifier = Modifier.weight(1f))
                    Text(text = "${set.weight} kg", modifier = Modifier.weight(1f))
                    // Simplification: In a real app, these Texts would be TextFields to allow editing

                    IconButton(onClick = { onDeleteSet(set) }, modifier = Modifier.size(24.dp)) {
                        // Simple 'X' or delete icon
                        Text("x", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Button(
                onClick = onAddSet,
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
            ) {
                Text("Add Set")
            }
        }
    }
}

@Composable
fun SaveRoutineDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save as Routine") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Routine Name") }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}