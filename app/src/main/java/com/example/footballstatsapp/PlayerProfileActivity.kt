package com.example.footballstatsapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PlayerProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_profile)

        val playerNameText: TextView = findViewById(R.id.playerNameText)
        val teamText: TextView = findViewById(R.id.teamText)
        val passingYardsText: TextView = findViewById(R.id.passingYardsText)
        val passingTouchdownsText: TextView =
            findViewById(R.id.passingTouchdownsText)

        val playerName = intent.getStringExtra("player_name") ?: "Unknown Player"
        val team = intent.getStringExtra("team") ?: "Unknown Team"
        val passingYards = intent.getStringExtra("passing_yards") ?: "N/A"
        val passingTouchdowns =
            intent.getStringExtra("passing_touchdowns") ?: "N/A"

        playerNameText.text = playerName
        teamText.text = team
        passingYardsText.text = "Passing Yards: $passingYards"
        passingTouchdownsText.text =
            "Passing Touchdowns: $passingTouchdowns"
    }
}