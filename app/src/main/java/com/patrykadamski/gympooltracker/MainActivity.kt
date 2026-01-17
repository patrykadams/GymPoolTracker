// file: app/src/main/java/com/patrykadamski/gympooltracker/MainActivity.kt
package com.patrykadamski.gympooltracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.patrykadamski.gympooltracker.presentation.add_workout.AddWorkoutScreen
import com.patrykadamski.gympooltracker.presentation.home.HomeScreen
import com.patrykadamski.gympooltracker.presentation.routines.RoutineListScreen
import com.patrykadamski.gympooltracker.presentation.workout_details.WorkoutDetailsScreen
import com.patrykadamski.gympooltracker.presentation.theme.GymPoolTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymPoolTrackerTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Item: Home
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
                    onClick = {
                        navController.navigate("home") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                // Item: Routines
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Routines") },
                    label = { Text("Routines") },
                    selected = currentDestination?.hierarchy?.any { it.route == "routines" } == true,
                    onClick = {
                        navController.navigate("routines") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Screen: Home
            composable("home") {
                HomeScreen(
                    onNavigateToWorkoutDetails = { workoutId ->
                        // FIX: Navigate to details screen with ID
                        navController.navigate("workout_details/$workoutId")
                    },
                    onNavigateToCreateWorkout = {
                        // FIX: Navigate to "Add Workout" screen
                        navController.navigate("add_workout")
                    }
                )
            }

            // Screen: Add Workout (Select Type)
            composable("add_workout") {
                AddWorkoutScreen(
                    onWorkoutCreated = { workoutId ->
                        // After creating, remove "add_workout" from backstack and go to details
                        navController.popBackStack()
                        navController.navigate("workout_details/$workoutId")
                    }
                )
            }

            // Screen: Workout Details
            composable(
                route = "workout_details/{workoutId}",
                arguments = listOf(navArgument("workoutId") { type = NavType.IntType }) // Assuming ID is Int. Change to LongType if needed.
            ) {
                WorkoutDetailsScreen(
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }

            // Screen: Routines List
            composable("routines") {
                RoutineListScreen(
                    onNavigateToCreate = {
                        // Logic for creating routine directly can be added later
                    }
                )
            }
        }
    }
}