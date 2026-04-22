package com.example.footballstatsapp

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.footballstatsapp.datamodel.Player
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class PlayerProfileActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private val database = FirebaseDatabase.getInstance().getReference("seasons")

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

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val matchingSeasons = mutableListOf<Player>()

                for (yearSnapshot in snapshot.children) {
                    for (playerSnapshot in yearSnapshot.children) {
                        val player = playerSnapshot.getValue(Player::class.java)
                        if (player != null && player.name == playerName) {
                            matchingSeasons.add(player)
                        }
                    }
                }

                if (matchingSeasons.isNotEmpty()) {
                    val latestSeason =
                        matchingSeasons.maxByOrNull { it.season } ?: matchingSeasons.first()

                    playerNameText.text = latestSeason.name
                    teamText.text = "${latestSeason.team} • ${latestSeason.season}"
                    passingYardsText.text = latestSeason.passingYards.toInt().toString()
                    passingTDText.text = latestSeason.passingTouchdowns.toInt().toString()
                    completionsText.text = latestSeason.passingCompletions.toInt().toString()
                    attemptsText.text = latestSeason.passingAttempts.toInt().toString()
                    compPercentageText.text =
                        String.format("%.1f%%", latestSeason.completionPercentage)
                    interceptionsText.text =
                        latestSeason.passingInterceptions.toInt().toString()
                    tdValueLabel.text =
                        latestSeason.passingTouchdowns.toInt().toString()

                    updateTdBar(latestSeason.passingTouchdowns.toInt(), tdBar)

                } else {
                    playerNameText.text = playerName
                    teamText.text = "No data found"
                    passingYardsText.text = "0"
                    passingTDText.text = "0"
                    completionsText.text = "0"
                    attemptsText.text = "0"
                    compPercentageText.text = "0.0%"
                    interceptionsText.text = "0"
                    tdValueLabel.text = "0"
                    updateTdBar(0, tdBar)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                playerNameText.text = playerName
                teamText.text = "Error loading data"
            }
        })

        setupNavigation()
    }

    private fun updateTdBar(tdNumber: Int, tdBar: View) {
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