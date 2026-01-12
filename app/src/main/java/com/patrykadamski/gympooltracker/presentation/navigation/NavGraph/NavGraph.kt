package com.patrykadamski.gympooltracker.presentation.navigation.NavGraph

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.patrykadamski.gympooltracker.presentation.add_workout.AddWorkoutScreen
import com.patrykadamski.gympooltracker.presentation.home.HomeScreen
import com.patrykadamski.gympooltracker.presentation.navigation.Screen
import com.patrykadamski.gympooltracker.presentation.stats.StatisticsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddWorkout = { workoutId ->
                    val id = workoutId ?: -1
                    navController.navigate("add_workout/$id")
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistics.route)
                }
            )
        }

        composable(
            route = "add_workout/{workoutId}",
            arguments = listOf(
                navArgument("workoutId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            AddWorkoutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}