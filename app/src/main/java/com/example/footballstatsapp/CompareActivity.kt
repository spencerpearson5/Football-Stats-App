package com.example.footballstatsapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.footballstatsapp.datamodel.PlayerProfile
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class CompareActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private lateinit var player1Selector: AutoCompleteTextView
    private lateinit var player2Selector: AutoCompleteTextView
    private lateinit var comparisonLayout: LinearLayout
    private lateinit var player1Name: TextView
    private lateinit var player2Name: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    private var allPlayers: List<PlayerProfile> = emptyList()
    private var selectedPlayer1: PlayerProfile? = null
    private var selectedPlayer2: PlayerProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_compare)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        player1Selector = findViewById(R.id.player1Search)
        player2Selector = findViewById(R.id.player2Search)
        comparisonLayout = findViewById(R.id.comparisonLayout)
        player1Name = findViewById(R.id.player1Name)
        player2Name = findViewById(R.id.player2Name)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        player1Selector.post {
            player1Selector.dropDownWidth = player1Selector.width
        }
        player2Selector.post {
            player2Selector.dropDownWidth = player2Selector.width
        }

        lifecycleScope.launch {
            viewModel.players.collect { players ->
                allPlayers = players
                setupAutocomplete(allPlayers)
            }
        }

        setupBottomNavigation()
    }

    private fun setupAutocomplete(players: List<PlayerProfile>) {
        val playerNames = players.map { it.name }.distinct().sorted()

        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_player_item,
            R.id.dropdownText,
            playerNames
        )

        player1Selector.setAdapter(adapter)
        player1Selector.threshold = 1

        player2Selector.setAdapter(adapter)
        player2Selector.threshold = 1

        player1Selector.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            selectedPlayer1 = allPlayers.find {
                it.name.equals(selectedName, ignoreCase = true)
            }
            updateComparison()
        }

        player2Selector.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position).toString()
            selectedPlayer2 = allPlayers.find {
                it.name.equals(selectedName, ignoreCase = true)
            }
            updateComparison()
        }
    }

    private fun updateComparison() {
        val p1 = selectedPlayer1
        val p2 = selectedPlayer2

        if (p1 != null && p2 != null) {
            val s1 = p1.latestSeason
            val s2 = p2.latestSeason

            comparisonLayout.visibility = View.VISIBLE
            player1Name.text = p1.name
            player2Name.text = p2.name

            findViewById<TextView>(R.id.p1Yards).text =
                s1.passingYards.toInt().toString()
            findViewById<TextView>(R.id.p2Yards).text =
                s2.passingYards.toInt().toString()

            findViewById<TextView>(R.id.p1TDs).text =
                s1.passingTouchdowns.toInt().toString()
            findViewById<TextView>(R.id.p2TDs).text =
                s2.passingTouchdowns.toInt().toString()

            findViewById<TextView>(R.id.p1Completions).text =
                s1.passingCompletions.toInt().toString()
            findViewById<TextView>(R.id.p2Completions).text =
                s2.passingCompletions.toInt().toString()

            findViewById<TextView>(R.id.p1Attempts).text =
                s1.passingAttempts.toInt().toString()
            findViewById<TextView>(R.id.p2Attempts).text =
                s2.passingAttempts.toInt().toString()

            findViewById<TextView>(R.id.p1Percentage).text =
                String.format("%.1f%%", s1.completionPercentage)
            findViewById<TextView>(R.id.p2Percentage).text =
                String.format("%.1f%%", s2.completionPercentage)

            findViewById<TextView>(R.id.p1Interceptions).text =
                s1.passingInterceptions.toInt().toString()
            findViewById<TextView>(R.id.p2Interceptions).text =
                s2.passingInterceptions.toInt().toString()
        } else {
            comparisonLayout.visibility = View.GONE
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_compare
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_players -> {
                    startActivity(Intent(this, PlayersActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_leaderboards -> {
                    startActivity(Intent(this, LeaderboardActivity::class.java))
                    true
                }
                R.id.nav_compare -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}