package com.example.footballstatsapp.datamodel

data class Quarterbacks (
    val name: String = "",
    val team: String = "",
    val passing_touchdowns: String = "",
    val passing_yards: String = "",
    val completions: String = "",
    val attempts: String = "",
    val completion_percentage: String = "",
    val interceptions: String = ""
) {
    //to enable sorting by numeric stats
    val yardsInt get() = passing_yards.toIntOrNull() ?: 0
    val tdInt get() = passing_touchdowns.toIntOrNull() ?: 0
    val completionsInt get() = completions.toIntOrNull() ?: 0
    val attemptsInt get() = attempts.toIntOrNull() ?: 0
    val intInt get() = interceptions.toIntOrNull() ?: 0
    val percentageFloat get() = completion_percentage.replace("%", "").toFloatOrNull() ?: 0f
}