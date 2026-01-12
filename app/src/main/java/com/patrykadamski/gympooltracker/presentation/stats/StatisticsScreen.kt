package com.patrykadamski.gympooltracker.presentation.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
// Importy dla Vico 1.x
import androidx.hilt.navigation.compose.hiltViewModel// Importy dla Vico 1.x
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.lineComponent // CORRECTED IMPORT
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Statystyki", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Sekcja Wykresu Kalorii
            StatsCard(title = "Spalone kalorie (7 dni)") {
                if (uiState.chartEntryModel != null) {
                    val axisFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                        uiState.bottomAxisLabels[value] ?: ""
                    }

                    Chart(
                        chart = columnChart(
                            columns = listOf(lineComponent(
                                color = MaterialTheme.colorScheme.primary,
                                thickness = 12.dp,
                                shape = Shapes.roundedCornerShape(allPercent = 40)
                            ))
                        ),
                        model = uiState.chartEntryModel!!,
                        // API Vico 1.x: funkcje zamiast remember
                        startAxis = startAxis(),
                        bottomAxis = bottomAxis(valueFormatter = axisFormatter),
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth()
                    )
                } else {
                    Box(Modifier.height(200.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Brak danych do wyświetlenia", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Sekcja Proporcji (Basen vs Siłownia)
            StatsCard(title = "Rozkład treningów") {
                DistributionBar(
                    gymPercent = uiState.gymPercentage,
                    poolPercent = uiState.poolPercentage
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LegendItem(color = MaterialTheme.colorScheme.primary, text = "Siłownia")
                    LegendItem(color = MaterialTheme.colorScheme.secondary, text = "Basen")
                }
            }
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
fun DistributionBar(gymPercent: Float, poolPercent: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .clip(RoundedCornerShape(50))
    ) {
        // Część Siłownia
        if (gymPercent > 0) {
            Box(
                modifier = Modifier
                    .weight(gymPercent)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        // Część Basen
        if (poolPercent > 0) {
            Box(
                modifier = Modifier
                    .weight(poolPercent)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.secondary)
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}