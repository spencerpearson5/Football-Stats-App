package com.example.footballstatsapp.datamodel


data class Player(
    val name: String = "",
    val team: String = "",
    val season: Int = 0,
    val passingYards: Double = 0.0,
    val passingTouchdowns: Double = 0.0,
    val passingCompletions: Double = 0.0,
    val passingAttempts: Double = 0.0,
    val passingInterceptions: Double = 0.0,
    val completionPercentage: Double = 0.0
)