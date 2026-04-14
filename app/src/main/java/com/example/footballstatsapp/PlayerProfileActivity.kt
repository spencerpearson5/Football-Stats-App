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
        val completionsText: TextView = findViewById(R.id.completionsText)
        val attemptsText: TextView = findViewById(R.id.attemptsText)
        val compPercentageText: TextView = findViewById(R.id.compPercentageText)
        val interceptionsText: TextView = findViewById(R.id.interceptionsText)
        val tdValueLabel: TextView = findViewById(R.id.tdValueLabel)
        val tdBar: View = findViewById(R.id.tdBar)

        val playerName = intent.getStringExtra("player_name") ?: "Unknown Player"
        val team = intent.getStringExtra("team") ?: "Unknown Team"

        val playerCard = findViewById<com.google.android.material.card.MaterialCardView>(R.id.playerCard)

        val cleanTeam = team.split(" ").first().trim()
        val teamColor = getTeamColor(cleanTeam)

        playerCard.strokeColor = teamColor
        tdBar.setBackgroundColor(teamColor)

        val passingYards = intent.getStringExtra("passing_yards") ?: "N/A"
        val passingTouchdowns = intent.getStringExtra("passing_touchdowns") ?: "N/A"
        val completions = intent.getStringExtra("completions") ?: "N/A"
        val attempts = intent.getStringExtra("attempts") ?: "N/A"
        val compPercentage = intent.getStringExtra("completion_percentage") ?: "N/A"
        val interceptions = intent.getStringExtra("interceptions") ?: "N/A"

        playerNameText.text = playerName
        teamText.text = team
        passingYardsText.text = passingYards
        passingTDText.text = passingTouchdowns
        completionsText.text = completions
        attemptsText.text = attempts
        compPercentageText.text =
            if (compPercentage.contains("%")) compPercentage else "$compPercentage%"
        interceptionsText.text = interceptions
        tdValueLabel.text = passingTouchdowns

        val tdNumber = passingTouchdowns.toIntOrNull() ?: 0

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
                    startActivity(Intent(this, LeaderboardActivity::class.java))
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

    private fun getTeamColor(team: String): Int {
        return when (team) {

            // NFC North
            "MIN" -> android.graphics.Color.parseColor("#4F2683") // Vikings
            "GB" -> android.graphics.Color.parseColor("#203731")  // Packers
            "CHI" -> android.graphics.Color.parseColor("#0B162A") // Bears
            "DET" -> android.graphics.Color.parseColor("#0076B6") // Lions

            // NFC East
            "DAL" -> android.graphics.Color.parseColor("#003594") // Cowboys
            "PHI" -> android.graphics.Color.parseColor("#004C54") // Eagles
            "NYG" -> android.graphics.Color.parseColor("#0B2265") // Giants
            "WAS" -> android.graphics.Color.parseColor("#5A1414") // Commanders

            // NFC West
            "SF" -> android.graphics.Color.parseColor("#AA0000")  // 49ers
            "SEA" -> android.graphics.Color.parseColor("#002244") // Seahawks
            "LAR" -> android.graphics.Color.parseColor("#003594") // Rams
            "ARI" -> android.graphics.Color.parseColor("#97233F") // Cardinals

            // NFC South
            "NO" -> android.graphics.Color.parseColor("#D3BC8D")  // Saints
            "TB" -> android.graphics.Color.parseColor("#D50A0A")  // Buccaneers
            "ATL" -> android.graphics.Color.parseColor("#A71930") // Falcons
            "CAR" -> android.graphics.Color.parseColor("#0085CA") // Panthers

            // AFC North
            "BAL" -> android.graphics.Color.parseColor("#241773") // Ravens
            "CIN" -> android.graphics.Color.parseColor("#FB4F14") // Bengals
            "CLE" -> android.graphics.Color.parseColor("#311D00") // Browns
            "PIT" -> android.graphics.Color.parseColor("#FFB612") // Steelers

            // AFC East
            "BUF" -> android.graphics.Color.parseColor("#00338D") // Bills
            "MIA" -> android.graphics.Color.parseColor("#008E97") // Dolphins
            "NE" -> android.graphics.Color.parseColor("#002244")  // Patriots
            "NYJ" -> android.graphics.Color.parseColor("#125740") // Jets

            // AFC West
            "KC" -> android.graphics.Color.parseColor("#E31837")  // Chiefs
            "LV" -> android.graphics.Color.parseColor("#000000")  // Raiders
            "LAC" -> android.graphics.Color.parseColor("#0080C6") // Chargers
            "DEN" -> android.graphics.Color.parseColor("#FB4F14") // Broncos

            // AFC South
            "HOU" -> android.graphics.Color.parseColor("#03202F") // Texans
            "IND" -> android.graphics.Color.parseColor("#002C5F") // Colts
            "JAX" -> android.graphics.Color.parseColor("#006778") // Jaguars
            "TEN" -> android.graphics.Color.parseColor("#4B92DB") // Titans

            else -> android.graphics.Color.GRAY
        }
    }
}