package com.example.footballstatsapp

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PlayerProfileActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_player_profile)

        // mapping
        val playerNameText: TextView = findViewById(R.id.playerNameText)
        val teamText: TextView = findViewById(R.id.teamText)
        val passingYardsText: TextView = findViewById(R.id.passingYardsText)
        val passingTDText: TextView = findViewById(R.id.passingTDText)
        val completionsText: TextView = findViewById(R.id.completionsText)
        val attemptsText: TextView = findViewById(R.id.attemptsText)
        val compPercentageText: TextView = findViewById(R.id.compPercentageText)
        val interceptionsText: TextView = findViewById(R.id.interceptionsText)
        val tdValueLabel: TextView = findViewById(R.id.tdValueLabel)
        val tdBar: View = findViewById(R.id.tdBar)

        val playerName = intent.getStringExtra("player_name") ?: "Unknown Player"
        val team = intent.getStringExtra("team") ?: "Unknown Team"


        val passingYards = intent.getDoubleExtra("passing_yards", 0.0)
        val passingTouchdowns = intent.getDoubleExtra("passing_touchdowns", 0.0)
        val completions = intent.getDoubleExtra("completions", 0.0)
        val attempts = intent.getDoubleExtra("attempts", 0.0)
        val compPercentage = intent.getDoubleExtra("completion_percentage", 0.0)
        val interceptions = intent.getDoubleExtra("interceptions", 0.0)

        // convert doubles to ints
        playerNameText.text = playerName
        teamText.text = team
        passingYardsText.text = passingYards.toInt().toString()
        passingTDText.text = passingTouchdowns.toInt().toString()
        completionsText.text = completions.toInt().toString()
        attemptsText.text = attempts.toInt().toString()

        // Format percentage to look cleaner
        compPercentageText.text = String.format("%.1f%%", compPercentage)

        interceptionsText.text = interceptions.toInt().toString()
        tdValueLabel.text = passingTouchdowns.toInt().toString()

        val tdNumber = passingTouchdowns.toInt()
        val maxTouchdowns = 60
        val minBarHeightDp = 40
        val maxBarHeightDp = 140

        val scaledHeightDp = if (tdNumber <= 0) {
            minBarHeightDp
        } else {
            val progress = (tdNumber.toFloat() / maxTouchdowns).coerceAtMost(1.0f)
            minBarHeightDp + (progress * (maxBarHeightDp - minBarHeightDp)).toInt()
        }

        val scaledHeightPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            scaledHeightDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        val layoutParams = tdBar.layoutParams
        layoutParams.height = scaledHeightPx
        tdBar.layoutParams = layoutParams

        setupNavigation()
    }

    private fun setupNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_players

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    true
                }
                R.id.nav_leaderboards -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    true
                }
                R.id.nav_players -> {
                    startActivity(Intent(this, PlayersActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    true
                }
                R.id.nav_compare -> {
                    startActivity(Intent(this, CompareActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}