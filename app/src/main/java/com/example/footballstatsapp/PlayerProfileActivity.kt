package com.example.footballstatsapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import android.widget.Toast

private lateinit var bottomNavigation: BottomNavigationView

class PlayerProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_profile)

        val playerNameText: TextView = findViewById(R.id.playerNameText)
        val teamText: TextView = findViewById(R.id.teamText)
        val passingYardsText: TextView = findViewById(R.id.passingYardsText)
        val passingTouchdownsText: TextView =
            findViewById<TextView>(R.id.passingTDText)

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

        bottomNavigation = findViewById(R.id.bottomNavigation)

        bottomNavigation.selectedItemId = R.id.nav_home

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }

                R.id.nav_leaderboards -> {
                    Toast.makeText(this, "Leaderboards coming soon", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_players -> {
                    Toast.makeText(this, "Players page coming soon", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_compare -> {
                    Toast.makeText(this, "Compare page coming soon", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }
}