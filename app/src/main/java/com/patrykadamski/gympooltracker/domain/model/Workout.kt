package com.patrykadamski.gympooltracker.domain.model

import java.time.LocalDateTime

data class Workout(
    val id: Int,
    val type: String,
    val date: LocalDateTime,
    val duration: Long,
    val calories: Int
)