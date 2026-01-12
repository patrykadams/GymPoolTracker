package com.patrykadamski.gympooltracker.presentation.add_workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykadamski.gympooltracker.domain.model.WorkoutType
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddWorkoutViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Obserwujemy dostępne typy z bazy
    val availableTypes by viewModel.availableTypes.collectAsState()

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddWorkoutViewModel.UiEvent.SaveSuccess -> onNavigateBack()
                is AddWorkoutViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (viewModel.isEditMode) "Edytuj trening" else "Dodaj trening",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // SEKKCJA 1: Wybór Typu (Dynamiczna lista Chipów)
            Text(text = "Typ treningu", style = MaterialTheme.typography.titleMedium)

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(availableTypes) { type ->
                    FilterChip(
                        selected = viewModel.selectedType?.id == type.id,
                        onClick = { viewModel.selectedType = type },
                        label = { Text(type.name) },
                        leadingIcon = {
                            Icon(
                                imageVector = getIconForName(type.iconName),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            // SEKKCJA 2: Czas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimePickerField(
                    label = "Start",
                    time = viewModel.startTime,
                    onTimeChange = { viewModel.startTime = it },
                    modifier = Modifier.weight(1f)
                )
                TimePickerField(
                    label = "Koniec",
                    time = viewModel.endTime,
                    onTimeChange = { viewModel.endTime = it },
                    modifier = Modifier.weight(1f)
                )
            }

            // SEKKCJA 3: Notatki
            OutlinedTextField(
                value = viewModel.notes,
                onValueChange = { viewModel.notes = it },
                label = { Text("Notatki (opcjonalne)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            // Przycisk Zapisz
            Button(
                onClick = { viewModel.saveWorkout { onNavigateBack() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Zapisz trening")
            }
        }
    }
}

@Composable
fun TimePickerField(
    label: String,
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    // Proste pole tekstowe do edycji czasu (dla uproszczenia UI na tym etapie)
    // W przyszłości można tu podpiąć natywny TimePicker
    var text by remember(time) { mutableStateOf(time.format(DateTimeFormatter.ofPattern("HH:mm"))) }

    OutlinedTextField(
        value = text,
        onValueChange = { newValue ->
            text = newValue
            if (newValue.length == 5) {
                try {
                    val parsed = LocalTime.parse(newValue)
                    onTimeChange(parsed)
                } catch (e: Exception) {
                    // Ignorujemy błędny format podczas pisania
                }
            }
        },
        label = { Text(label) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
    )
}

// Funkcja pomocnicza do mapowania nazw ikon z bazy na ikony Material Design
fun getIconForName(iconName: String): ImageVector {
    return when (iconName) {
        "GYM" -> Icons.Default.Build        // Siłownia
        "POOL" -> Icons.Default.Face        // Basen
        "RUN" -> Icons.Default.PlayArrow    // Bieganie (zastępcza ikona z Core)
        "BIKE" -> Icons.Default.Star        // Rower (zastępcza ikona z Core)
        else -> Icons.Default.Info          // Domyślna
    }
}