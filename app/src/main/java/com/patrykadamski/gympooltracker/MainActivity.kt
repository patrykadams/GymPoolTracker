package com.patrykadamski.gympooltracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.patrykadamski.gympooltracker.presentation.add_workout.AddWorkoutScreen
import com.patrykadamski.gympooltracker.presentation.home.HomeScreen
import com.patrykadamski.gympooltracker.presentation.stats.StatisticsScreen
import com.patrykadamski.gympooltracker.presentation.theme.GymPoolTrackerTheme
import com.patrykadamski.gympooltracker.presentation.workout_details.WorkoutDetailsScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GymPoolTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GymPoolTrackerApp()
                }
            }
        }
    }
}

/**
 * The main entry point for the application's UI.
 * Configures the Navigation Graph and routes.
 */
@Composable
fun GymPoolTrackerApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        // --- HOME SCREEN ---
        composable(route = Screen.Home.route) {
            HomeScreen(
                // Logic: If ID is null, we are creating a new workout -> Go to AddWorkoutScreen.
                // If ID is NOT null, we are viewing an existing workout -> Go to WorkoutDetailsScreen.
                onNavigateToAddWorkout = { workoutId ->
                    if (workoutId != null) {
                        navController.navigate(Screen.WorkoutDetails.createRoute(workoutId))
                    } else {
                        navController.navigate(Screen.AddWorkout.route)
                    }
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistics.route)
                }
            )
        }

        // --- ADD WORKOUT SCREEN (Creation Phase) ---
        composable(route = Screen.AddWorkout.route) {
            AddWorkoutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- STATISTICS SCREEN ---
        composable(route = Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- WORKOUT DETAILS SCREEN (Atlas / Execution Phase) ---
        composable(
            route = Screen.WorkoutDetails.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.IntType }
            )
        ) {
            WorkoutDetailsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Sealed class defining all available navigation routes and helper methods for arguments.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddWorkout : Screen("add_workout")
    object Statistics : Screen("statistics")
    object WorkoutDetails : Screen("workout_details/{workoutId}") {
        fun createRoute(workoutId: Int) = "workout_details/$workoutId"
    }
}