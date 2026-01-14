// file: app/src/main/java/com/patrykadamski/gympooltracker/presentation/navigation/NavGraph/NavGraph.kt
package com.patrykadamski.gympooltracker.presentation.navigation.NavGraph

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.patrykadamski.gympooltracker.presentation.home.HomeScreen
import com.patrykadamski.gympooltracker.presentation.routines.RoutineListScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Screen: Home
        composable(route = "home") {
            HomeScreen(
                onNavigateToWorkoutDetails = { workoutId ->
                    // TODO: Navigate to Workout Details Screen using workoutId
                    // navController.navigate("workout_details/$workoutId")
                },
                // FIX: Renamed parameter from 'onNavigateToAddWorkout' to 'onNavigateToCreateWorkout'
                onNavigateToCreateWorkout = {
                    // TODO: Navigate to Create Workout Screen
                    // navController.navigate("create_workout")
                }
            )
        }

        // Screen: Routines
        composable(route = "routines") {
            RoutineListScreen(
                onNavigateToCreate = {
                    // TODO: Navigate to Create Routine Screen
                    // navController.navigate("create_routine")
                }
            )
        }
    }
}