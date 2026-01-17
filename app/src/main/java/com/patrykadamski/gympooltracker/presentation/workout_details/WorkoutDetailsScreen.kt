package com.patrykadamski.gympooltracker.presentation.workout_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykadamski.gympooltracker.data.local.ExerciseWithSets
import com.patrykadamski.gympooltracker.data.local.SetEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    viewModel: WorkoutDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = state?.workout?.type ?: "Workout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Save as Routine */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddExerciseDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Exercise")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (state == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                WorkoutContent(
                    exercises = state!!.exercises,
                    onAddSet = viewModel::addSet,
                    onUpdateSet = viewModel::updateSet,
                    onDeleteSet = viewModel::deleteSet,
                    onDeleteExercise = viewModel::deleteExercise
                )
            }

            if (showAddExerciseDialog) {
                AddExerciseDialog(
                    onDismiss = { showAddExerciseDialog = false },
                    onConfirm = { name ->
                        viewModel.addExercise(name)
                        showAddExerciseDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun WorkoutContent(
    exercises: List<ExerciseWithSets>,
    onAddSet: (Int) -> Unit,
    onUpdateSet: (Int, String, Double, Boolean) -> Unit,
    onDeleteSet: (Int) -> Unit,
    onDeleteExercise: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        items(exercises) { exercise ->
            ExerciseCard(
                exercise = exercise,
                onAddSet = { onAddSet(exercise.exercise.id.toInt()) },
                onUpdateSet = onUpdateSet,
                onDeleteSet = onDeleteSet,
                onDeleteExercise = { onDeleteExercise(exercise.exercise.id.toInt()) }
            )
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: ExerciseWithSets,
    onAddSet: () -> Unit,
    onUpdateSet: (Int, String, Double, Boolean) -> Unit,
    onDeleteSet: (Int) -> Unit,
    onDeleteExercise: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Exercise Name + Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDeleteExercise) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Exercise",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Column Headers
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                Text("Set", modifier = Modifier.weight(1f), fontSize = 12.sp, color = Color.Gray)
                Text("Prev", modifier = Modifier.weight(2f), fontSize = 12.sp, color = Color.Gray)
                Text("Kg", modifier = Modifier.weight(2f), fontSize = 12.sp, color = Color.Gray)
                Text("Reps", modifier = Modifier.weight(2f), fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(32.dp))
            }

            // List of Sets
            exercise.sets.forEachIndexed { index, set ->
                SetRow(
                    set = set, // Tu jest klucz - 'set' musi być typu SetEntity
                    setNumber = index + 1,
                    onUpdateSet = onUpdateSet,
                    onDeleteSet = { onDeleteSet(set.id.toInt()) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }

            // Add Set Button
            TextButton(
                onClick = onAddSet,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Set")
            }
        }
    }
}

@Composable
fun SetRow(
    set: SetEntity, // FIX: Używamy SetEntity
    setNumber: Int,
    onUpdateSet: (Int, String, Double, Boolean) -> Unit,
    onDeleteSet: () -> Unit
) {
    // Zapamiętanie stanu, żeby kursor nie skakał
    var weightText by remember(set.weight) { mutableStateOf(if (set.weight > 0.0) set.weight.toString() else "") }
    var repsText by remember(set.reps) { mutableStateOf(set.reps) }

    val backgroundColor = if (set.isCompleted)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    else
        Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Numer serii
        Text(
            text = "$setNumber",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )

        // Previous (Placeholder)
        Text(
            text = "-",
            modifier = Modifier.weight(2f),
            color = Color.Gray,
            fontSize = 12.sp
        )

        // Waga
        OutlinedTextField(
            value = weightText,
            onValueChange = {
                weightText = it
                val w = it.toDoubleOrNull() ?: 0.0
                onUpdateSet(set.id.toInt(), repsText, w, set.isCompleted)
            },
            modifier = Modifier.weight(2f).height(50.dp).padding(end = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        // Powtórzenia
        OutlinedTextField(
            value = repsText,
            onValueChange = {
                repsText = it
                val w = weightText.toDoubleOrNull() ?: 0.0
                onUpdateSet(set.id.toInt(), it, w, set.isCompleted)
            },
            modifier = Modifier.weight(2f).height(50.dp).padding(end = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        // Checkbox
        IconButton(
            onClick = {
                val w = weightText.toDoubleOrNull() ?: 0.0
                onUpdateSet(set.id.toInt(), repsText, w, !set.isCompleted)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Complete",
                tint = if (set.isCompleted) MaterialTheme.colorScheme.primary else Color.LightGray
            )
        }
    }
}

@Composable
fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("New Exercise", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Exercise Name") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(onClick = { onConfirm(text) }, enabled = text.isNotBlank()) {
                        Text("Add")
                    }
                }
            }
        }
    }
}