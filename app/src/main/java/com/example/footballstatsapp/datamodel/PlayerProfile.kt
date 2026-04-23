package com.example.footballstatsapp.datamodel

data class PlayerProfile(
    val name: String,
    val seasons: List<Player>
) {
    var displaySeasonOverride: Player? = null

    val latestSeason: Player
        get() = displaySeasonOverride ?: seasons.maxByOrNull { it.season } ?: seasons.first()
}