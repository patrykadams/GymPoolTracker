package com.patrykadamski.gympooltracker.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")
    data object AddTraining : Screen("add_training_screen")
    data object Statistics : Screen("statistics_screen")
}