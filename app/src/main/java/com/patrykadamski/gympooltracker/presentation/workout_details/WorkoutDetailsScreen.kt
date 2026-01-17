// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/workout_details/WorkoutDetailsScreen.kt
package com.patrykadamski.gympooltracker.presentation.workout_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import com.patrykadamski.gympooltracker.domain.model.WorkoutExercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    onNavigateUp: () -> Unit,
    viewModel: WorkoutDetailsVM = hiltViewModel()
) {
    val workoutDetails by viewModel.workoutDetails.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val timerState by viewModel.timerState.collectAsState()

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
            FloatingActionButton(onClick = { viewModel.addExercise("New Exercise") /* Placeholder */ }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Exercise")
            }
        },
        bottomBar = {
            RestTimerOverlay(
                timerState = timerState,
                onCancel = { viewModel.cancelTimer() },
                onAdd10s = { viewModel.addTime(10) }
            )
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
                contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isSwimmingWorkout(details.workout.type)) {
                    item {
                        SwimmingDistanceCard(
                            distance = details.workout.distanceMeters,
                            onDistanceChange = { newDistance ->
                                viewModel.updateDistance(newDistance)
                            }
                        )
                    }
                }

                items(details.exercises) { exercise ->
                    ExerciseItem(
                        exercise = exercise,
                        onAddSet = { viewModel.addSet(exercise.id, exercise.sets.size) },
                        onUpdateSet = { set -> viewModel.updateSet(set) },
                        onToggleSet = { set, isChecked -> viewModel.toggleSetCompletion(set, isChecked) },
                        onDeleteSet = { set -> viewModel.deleteSet(set) },
                        onDeleteExercise = { viewModel.deleteExercise(exercise.id) }
                    )
                }
            }
        }
    }
}

fun isSwimmingWorkout(type: String): Boolean {
    return type.contains("pool", ignoreCase = true) ||
            type.contains("swimming", ignoreCase = true) ||
            type.contains("basen", ignoreCase = true) ||
            type.contains("pływanie", ignoreCase = true)
}

@Composable
fun SwimmingDistanceCard(
    distance: Int,
    onDistanceChange: (Int) -> Unit
) {
    var textValue by remember(distance) { mutableStateOf(distance.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Pool Session Stats",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = textValue,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        textValue = newValue
                        onDistanceChange(newValue.toIntOrNull() ?: 0)
                    }
                },
                label = { Text("Total Distance (meters)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
fun RestTimerOverlay(
    timerState: TimerState,
    onCancel: () -> Unit,
    onAdd10s: () -> Unit
) {
    AnimatedVisibility(
        visible = timerState.isRunning,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.inverseSurface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = MaterialTheme.colorScheme.inverseOnSurface
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Rest: ${formatTime(timerState.remainingSeconds)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }

                Row {
                    TextButton(onClick = onAdd10s) {
                        Text("+10s", color = MaterialTheme.colorScheme.inverseOnSurface)
                    }
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colorScheme.inverseOnSurface
                        )
                    }
                }
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}

@Composable
fun ExerciseItem(
    exercise: WorkoutExercise,
    onAddSet: () -> Unit,
    onUpdateSet: (GymSet) -> Unit,
    onToggleSet: (GymSet, Boolean) -> Unit,
    onDeleteSet: (GymSet) -> Unit,
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
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Delete Exercise")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "#", modifier = Modifier.width(30.dp), style = MaterialTheme.typography.bodySmall)
                Text(text = "Reps", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                Text(text = "Kg", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.width(40.dp)) // For Checkbox
                Spacer(modifier = Modifier.width(40.dp)) // For Delete
            }

            exercise.sets.forEachIndexed { index, set ->
                SetRow(
                    index = index + 1,
                    set = set,
                    onUpdate = onUpdateSet,
                    onToggle = onToggleSet,
                    onDelete = onDeleteSet
                )
            }

            Button(
                onClick = onAddSet,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                Text("Add Set")
            }
        }
    }
}

@Composable
fun SetRow(
    index: Int,
    set: GymSet,
    onUpdate: (GymSet) -> Unit,
    onToggle: (GymSet, Boolean) -> Unit,
    onDelete: (GymSet) -> Unit
) {
    // TextFields for editing values directly
    var repsText by remember(set.reps) { mutableStateOf(set.reps) }
    var weightText by remember(set.weight) { mutableStateOf(set.weight.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$index",
            modifier = Modifier.width(30.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        // FIX: Removed 'contentPadding' parameter which caused the error
        OutlinedTextField(
            value = repsText,
            onValueChange = {
                repsText = it
                onUpdate(set.copy(reps = it))
            },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        // FIX: Removed 'contentPadding' parameter here as well
        OutlinedTextField(
            value = weightText,
            onValueChange = {
                weightText = it
                val newWeight = it.toDoubleOrNull() ?: 0.0
                onUpdate(set.copy(weight = newWeight))
            },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Checkbox(
            checked = set.isCompleted,
            onCheckedChange = { isChecked ->
                onToggle(set, isChecked)
            }
        )

        IconButton(onClick = { onDelete(set) }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete Set",
                tint = MaterialTheme.colorScheme.error
            )
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