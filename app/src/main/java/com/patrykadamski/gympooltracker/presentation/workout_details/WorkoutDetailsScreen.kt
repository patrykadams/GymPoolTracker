package com.patrykadamski.gympooltracker.presentation.workout_details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime // Upewnij się, że masz zależność extended, lub zmień na Info
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykadamski.gympooltracker.domain.model.GymExercise
import com.patrykadamski.gympooltracker.domain.model.GymSet

/**
 * Main screen for viewing and editing details of a specific workout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    onNavigateBack: () -> Unit,
    viewModel: WorkoutDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    val exerciseHistory by viewModel.exerciseHistory.collectAsState()
    val personalRecords by viewModel.personalRecords.collectAsState()

    var showAddExerciseDialog by remember { mutableStateOf(false) }

    if (showAddExerciseDialog) {
        AddExerciseDialog(
            history = exerciseHistory,
            onDismiss = { showAddExerciseDialog = false },
            onConfirm = { name ->
                viewModel.addExercise(name)
                showAddExerciseDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(uiState?.workout?.type?.uppercase() ?: "Workout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (!timerState.isRunning) {
                ExtendedFloatingActionButton(
                    onClick = { showAddExerciseDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Exercise") }
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = timerState.isRunning,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                RestTimerBar(
                    timeLeft = timerState.timeLeft,
                    totalTime = timerState.totalTime,
                    onStop = { viewModel.stopTimer() },
                    onAdd10s = { viewModel.addTime(10) }
                )
            }
        }
    ) { padding ->
        if (uiState == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val details = uiState!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(details.exercises, key = { it.id }) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        personalRecord = personalRecords[exercise.id],
                        onAddSet = { viewModel.addSet(exercise.id, exercise.sets) },
                        onUpdateSet = { set, reps, weight, rpe ->
                            viewModel.updateSet(set, reps, weight, rpe)
                        },
                        onUpdateRestTime = { set, time -> viewModel.updateSetRestTime(set, time) },
                        onToggleSet = { set -> viewModel.toggleSetCompleted(set) },
                        onDeleteSet = { set -> viewModel.deleteSet(set) },
                        onDeleteExercise = { viewModel.deleteExercise(exercise.id) }
                    )
                }
            }
        }
    }
}

/**
 * A persistent bottom bar that displays the rest timer.
 */
@Composable
fun RestTimerBar(
    timeLeft: Int,
    totalTime: Int,
    onStop: () -> Unit,
    onAdd10s: () -> Unit
) {
    val progress = if (totalTime > 0) timeLeft.toFloat() / totalTime.toFloat() else 0f

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Rest Timer", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = formatTime(timeLeft),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onAdd10s, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))) {
                        Text("+10s")
                    }
                    IconButton(onClick = onStop) {
                        Icon(Icons.Default.Close, contentDescription = "Stop Timer")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}

@Composable
fun ExerciseCard(
    exercise: GymExercise,
    personalRecord: Double?,
    onAddSet: () -> Unit,
    onUpdateSet: (GymSet, String, Double, Double) -> Unit,
    onUpdateRestTime: (GymSet, Int) -> Unit,
    onToggleSet: (GymSet) -> Unit,
    onDeleteSet: (GymSet) -> Unit,
    onDeleteExercise: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(12.dp)) {
            // Header
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (personalRecord != null && personalRecord > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = "PR", modifier = Modifier.size(14.dp), tint = Color(0xFFFFC107))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "PR: ${personalRecord}kg",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
                IconButton(onClick = onDeleteExercise, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove Exercise",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Table Header
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("#", Modifier.width(25.dp), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Kg", Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Reps", Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("RPE", Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.AccessTime, contentDescription = "Rest", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.width(4.dp))
                Text("Done", Modifier.width(40.dp), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Divider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

            // Sets
            exercise.sets.forEach { set ->
                SwipeToDeleteContainer(onDelete = { onDeleteSet(set) }) {
                    SetRow(
                        set = set,
                        onUpdate = onUpdateSet,
                        onUpdateRestTime = onUpdateRestTime,
                        onToggleSet = onToggleSet
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Add Set
            TextButton(
                onClick = onAddSet,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Add Set")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteContainer(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            val color by animateColorAsState(
                if (state.targetValue == SwipeToDismissBoxValue.EndToStart)
                    Color.Red.copy(alpha = 0.8f) else Color.Transparent,
                label = "color"
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        content = { content() }
    )
}

@Composable
fun SetRow(
    set: GymSet,
    onUpdate: (GymSet, String, Double, Double) -> Unit,
    onUpdateRestTime: (GymSet, Int) -> Unit,
    onToggleSet: (GymSet) -> Unit
) {
    val backgroundColor = if (set.isCompleted)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    else
        MaterialTheme.colorScheme.surface

    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        RestTimePickerDialog(
            initialSeconds = set.restSeconds,
            onDismiss = { showTimePicker = false },
            onConfirm = { newTime ->
                onUpdateRestTime(set, newTime)
                showTimePicker = false
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = set.setNumber.toString(),
            modifier = Modifier.width(25.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        CompactDecimalInput(
            value = set.weight,
            onValueChange = { newWeight -> onUpdate(set, set.reps, newWeight, set.rpe) },
            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
        )
        CompactStringInput(
            value = set.reps,
            onValueChange = { newReps -> onUpdate(set, newReps, set.weight, set.rpe) },
            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
        )
        CompactDecimalInput(
            value = set.rpe,
            onValueChange = { newRpe -> onUpdate(set, set.reps, set.weight, newRpe) },
            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { showTimePicker = true }
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${set.restSeconds}s",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(Modifier.width(40.dp), contentAlignment = Alignment.Center) {
            Checkbox(
                checked = set.isCompleted,
                onCheckedChange = { onToggleSet(set) }
            )
        }
    }
}

@Composable
fun RestTimePickerDialog(
    initialSeconds: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val options = listOf(30, 60, 90, 120, 180, 300)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Rest Time") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Choose standard time:", style = MaterialTheme.typography.bodySmall)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    options.take(3).forEach { sec ->
                        SuggestionChip(
                            onClick = { onConfirm(sec) },
                            label = { Text("${sec}s") },
                            colors = if (sec == initialSeconds) SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else SuggestionChipDefaults.suggestionChipColors()
                        )
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    options.drop(3).forEach { sec ->
                        SuggestionChip(
                            onClick = { onConfirm(sec) },
                            label = { Text(if (sec >= 60) "${sec/60}m" else "${sec}s") },
                            colors = if (sec == initialSeconds) SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else SuggestionChipDefaults.suggestionChipColors()
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

/**
 * Dialog for entering the name of a new exercise with Autocomplete suggestions using ExposedDropdownMenuBox.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseDialog(
    history: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    // Filter logic
    val filteredHistory = remember(text, history) {
        if (text.isBlank()) history else history.filter { it.contains(text, ignoreCase = true) }
    }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Exercise") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ExposedDropdownMenuBox handles the attachment logic natively
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            expanded = true // Ensure list opens when typing
                        },
                        label = { Text("Exercise Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(), // This connects the field to the menu
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    // Only show menu if there are items to show
                    if (filteredHistory.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            filteredHistory.take(5).forEach { suggestion ->
                                DropdownMenuItem(
                                    text = { Text(suggestion) },
                                    onClick = {
                                        text = suggestion
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                }

                // Recent suggestions (Chips)
                if (history.isNotEmpty() && text.isBlank()) {
                    Text("Recent:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        history.take(3).forEach { recent ->
                            SuggestionChip(onClick = { text = recent }, label = { Text(recent) })
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { if (text.isNotBlank()) onConfirm(text) }) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun CompactDecimalInput(value: Double, onValueChange: (Double) -> Unit, modifier: Modifier = Modifier) {
    var text by remember(value) { mutableStateOf(if (value == 0.0) "" else value.toString()) }
    OutlinedTextField(
        value = text,
        onValueChange = { newValue ->
            text = newValue
            val number = newValue.toDoubleOrNull() ?: 0.0
            onValueChange(number)
        },
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
    )
}

@Composable
fun CompactStringInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    var text by remember(value) { mutableStateOf(if (value == "0") "" else value) }
    OutlinedTextField(
        value = text,
        onValueChange = { newValue ->
            text = newValue
            onValueChange(newValue)
        },
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
    )
}