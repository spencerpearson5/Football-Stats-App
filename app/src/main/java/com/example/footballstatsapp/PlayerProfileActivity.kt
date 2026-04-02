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

        val playerNameText: TextView = findViewById(R.id.playerNameText)
        val teamText: TextView = findViewById(R.id.teamText)
        val passingYardsText: TextView = findViewById(R.id.passingYardsText)
        val passingTDText: TextView = findViewById(R.id.passingTDText)
        val tdValueLabel: TextView = findViewById(R.id.tdValueLabel)
        val tdBar: View = findViewById(R.id.tdBar)

        val playerName = intent.getStringExtra("player_name") ?: "Unknown Player"
        val team = intent.getStringExtra("team") ?: "Unknown Team"
        val passingYards = intent.getStringExtra("passing_yards") ?: "N/A"
        val passingTouchdowns =
            intent.getStringExtra("passing_touchdowns") ?: "N/A"

        playerNameText.text = playerName
        teamText.text = team
        passingYardsText.text = passingYards
        passingTDText.text = passingTouchdowns
        tdValueLabel.text = passingTouchdowns

        val tdNumber = passingTouchdowns.toIntOrNull() ?: 0

        // scale one bar based on a max of 60 TDs
        val maxTouchdowns = 60
        val minBarHeightDp = 40
        val maxBarHeightDp = 140

        val scaledHeightDp = if (tdNumber <= 0) {
            minBarHeightDp
        } else {
            minBarHeightDp + ((tdNumber.toFloat() / maxTouchdowns) *
                    (maxBarHeightDp - minBarHeightDp)).toInt()
        }

        val scaledHeightPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            scaledHeightDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        val layoutParams = tdBar.layoutParams
        layoutParams.height = scaledHeightPx
        tdBar.layoutParams = layoutParams

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
                    Toast.makeText(
                        this,
                        "Leaderboards coming soon",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                R.id.nav_players -> {
                    val intent = Intent(this, PlayersActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }

                R.id.nav_compare -> {
                    Toast.makeText(
                        this,
                        "Compare page coming soon",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }

                else -> false
            }
        }
    }
}