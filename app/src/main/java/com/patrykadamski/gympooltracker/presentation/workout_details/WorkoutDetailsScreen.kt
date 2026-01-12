// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/workout_details/WorkoutDetailsScreen.kt
package com.patrykadamski.gympooltracker.presentation.workout_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykadamski.gympooltracker.domain.model.GymExercise
import com.patrykadamski.gympooltracker.domain.model.GymSet
import com.patrykadamski.gympooltracker.domain.model.WorkoutDetails
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    onNavigateBack: () -> Unit,
    viewModel: WorkoutDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val suggestions by viewModel.exerciseSuggestions.collectAsState()

    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var showSaveRoutineDialog by remember { mutableStateOf(false) }

    if (showSaveRoutineDialog) {
        SaveRoutineDialog(
            onDismiss = { showSaveRoutineDialog = false },
            onConfirm = { name ->
                viewModel.saveAsRoutine(name)
                showSaveRoutineDialog = false
            }
        )
    }

    if (showAddExerciseDialog) {
        AddExerciseDialog(
            suggestions = suggestions,
            onDismiss = { showAddExerciseDialog = false },
            onConfirm = { name ->
                viewModel.addExercise(name)
                showAddExerciseDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trening") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSaveRoutineDialog = true }) {
                        Icon(Icons.Default.Save, contentDescription = "Zapisz jako szablon")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddExerciseDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Exercise")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is WorkoutDetailsUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is WorkoutDetailsUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is WorkoutDetailsUiState.Success -> {
                    WorkoutContent(
                        details = state.details,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun SaveRoutineDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Zapisz jako Szablon") },
        text = {
            Column {
                Text("Podaj nazwę dla tego szablonu (np. Pull Day):")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Nazwa szablonu") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onConfirm(text) },
                enabled = text.isNotBlank()
            ) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun WorkoutContent(
    details: WorkoutDetails,
    viewModel: WorkoutDetailsViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WorkoutHeader(details)
        }

        items(details.exercises) { exercise ->
            ExerciseCard(
                exercise = exercise,
                onAddSet = { weight, reps ->
                    viewModel.addSet(exercise.id, weight, reps)
                },
                onUpdateSet = { set, r, w -> viewModel.updateSet(set, r, w) },
                onToggleSet = { set, completed -> viewModel.toggleSetCompletion(set, completed) },
                onDeleteSet = { set -> viewModel.deleteSet(set) },
                onDeleteExercise = { viewModel.deleteExercise(exercise.id) }
            )
        }
    }
}

@Composable
fun WorkoutHeader(details: WorkoutDetails) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = details.workout.type.uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = details.workout.date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("pl"))),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${details.workout.caloriesBurned} kcal", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${details.workout.durationMinutes} min")
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: GymExercise,
    onAddSet: (Double?, String?) -> Unit,
    onUpdateSet: (GymSet, String, String) -> Unit,
    onToggleSet: (GymSet, Boolean) -> Unit,
    onDeleteSet: (GymSet) -> Unit,
    onDeleteExercise: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDeleteExercise, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.outline)
                }
            }

            // PR Display
            if (exercise.personalRecord != null && exercise.personalRecord > 0.0) {
                Text(
                    text = "PR: ${exercise.personalRecord} kg",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Sets Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("#", modifier = Modifier.width(24.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                Text("KG", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                Text("REPS", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.width(32.dp)) // Checkbox area
            }

            // Sets List
            exercise.sets.forEachIndexed { index, set ->
                SetRow(
                    set = set,
                    index = index + 1,
                    onUpdate = { r, w -> onUpdateSet(set, r, w) },
                    onToggle = { onToggleSet(set, it) },
                    onDelete = { onDeleteSet(set) }
                )
            }

            // Add Set Button
            TextButton(
                onClick = {
                    val lastSet = exercise.sets.lastOrNull()
                    onAddSet(lastSet?.weight, lastSet?.reps)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Dodaj serię")
            }
        }
    }
}

@Composable
fun SetRow(
    set: GymSet,
    index: Int,
    onUpdate: (String, String) -> Unit,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    // Local state for smooth typing before committing to VM
    var weightText by remember(set.weight) { mutableStateOf(if (set.weight > 0.0) set.weight.toString() else "") }
    var repsText by remember(set.reps) { mutableStateOf(set.reps) }

    val backgroundColor = if (set.isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, MaterialTheme.shapes.extraSmall)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Set Number / Delete
        Box(modifier = Modifier.width(24.dp), contentAlignment = Alignment.Center) {
            Text(
                text = index.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.clickable { onDelete() }
            )
        }

        // Weight
        CompactTextField(
            value = weightText,
            onValueChange = {
                weightText = it
                onUpdate(repsText, it)
            },
            modifier = Modifier.weight(1f)
        )

        // Reps
        CompactTextField(
            value = repsText,
            onValueChange = {
                repsText = it
                onUpdate(it, weightText)
            },
            modifier = Modifier.weight(1f)
        )

        // Checkbox
        Checkbox(
            checked = set.isCompleted,
            onCheckedChange = { onToggle(it) },
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = { if (it.length < 6) onValueChange(it) },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        modifier = modifier
            .height(36.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
            .wrapContentHeight(Alignment.CenterVertically)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: androidx.compose.ui.text.TextStyle,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        modifier = modifier.padding(horizontal = 4.dp),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.Center) {
                if (value.isEmpty()) {
                    Text("-", style = textStyle, color = MaterialTheme.colorScheme.outline)
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun AddExerciseDialog(
    suggestions: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val filteredSuggestions = remember(text, suggestions) {
        if (text.isBlank()) emptyList()
        else suggestions.filter { it.contains(text, ignoreCase = true) }.take(3)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj ćwiczenie") },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Nazwa ćwiczenia") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                AnimatedVisibility(visible = filteredSuggestions.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
                    ) {
                        filteredSuggestions.forEach { suggestion ->
                            Text(
                                text = suggestion,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onConfirm(suggestion) }
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (text.isNotBlank()) onConfirm(text) }) {
                Text("Dodaj")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}