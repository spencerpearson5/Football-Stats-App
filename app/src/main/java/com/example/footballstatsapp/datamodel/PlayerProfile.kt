package com.example.footballstatsapp.datamodel

data class PlayerProfile(
    val name: String,
    val seasons: List<Player>
) {
    val latestSeason: Player
        get() = seasons.maxByOrNull { it.season } ?: seasons.first()
}