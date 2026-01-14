// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/routines/RoutineListScreen.kt
package com.patrykadamski.gympooltracker.presentation.routines

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykadamski.gympooltracker.data.local.RoutineWithExercises

@Composable
fun RoutineListScreen(
    onNavigateToCreate: () -> Unit,
    viewModel: RoutineViewModel = hiltViewModel()
) {
    val routines by viewModel.routines.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Routine")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(routines) { item ->
                RoutineItem(
                    item = item,
                    
                    onDeleteClick = { viewModel.deleteRoutine(item.routine.id) }
                )
            }
        }
    }
}

@Composable
fun RoutineItem(
    item: RoutineWithExercises,
    onDeleteClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Text info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.routine.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = item.routine.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.exercises.isNotEmpty()) {
                    Text(
                        text = "${item.exercises.size} exercises",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Right side: Delete Button
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Routine",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}