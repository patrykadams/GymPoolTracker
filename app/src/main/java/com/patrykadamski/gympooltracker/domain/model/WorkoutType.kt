package com.patrykadamski.gympooltracker.domain.model

data class WorkoutType(
    val id: Int,
    val name: String,
    val caloriesPerMinute: Int,
    val iconName: String
)